import { ApiError, type ApiErrorBody } from '@domain/http/ApiError';
import type { HttpPort, UnauthorizedListener } from '@domain/http/HttpPort';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

/**
 * The only HTTP transport in the app: plain WebView fetch.
 *
 * Do NOT enable CapacitorHttp globally and do NOT route through
 * CapacitorHttp.request(): both have dropped the Authorization header on
 * Android and produced false INVALID_TOKEN logouts.
 */
export class FetchHttpClient implements HttpPort {
  private unauthorizedListener: UnauthorizedListener | null = null;

  constructor(private readonly baseUrl: string) {}

  /**
   * Typed interceptor for rejected Bearer tokens. Only invoked when the
   * failing request actually sent a token and the API answered
   * 401 INVALID_TOKEN — a token-less 401 never reaches the listener.
   */
  setUnauthorizedListener(listener: UnauthorizedListener | null): void {
    this.unauthorizedListener = listener;
  }

  async get<T>(path: string, token?: string): Promise<T> {
    return this.request<T>('GET', path, undefined, token);
  }

  async post<T>(path: string, body: unknown, token?: string): Promise<T> {
    return this.request<T>('POST', path, body, token);
  }

  async put<T>(path: string, body: unknown, token?: string): Promise<T> {
    return this.request<T>('PUT', path, body, token);
  }

  async patch<T>(path: string, body: unknown, token?: string): Promise<T> {
    return this.request<T>('PATCH', path, body, token);
  }

  async delete(path: string, token?: string): Promise<void> {
    await this.request<void>('DELETE', path, undefined, token);
  }

  private async request<T>(
    method: HttpMethod,
    path: string,
    body: unknown | undefined,
    token?: string,
  ): Promise<T> {
    const headers: Record<string, string> = { Accept: 'application/json' };
    if (method !== 'GET' && method !== 'DELETE') {
      // Always explicit on mutating calls; a missing Content-Type has
      // historically produced misleading errors on public routes.
      headers['Content-Type'] = 'application/json';
    }
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const url = `${this.baseUrl}${path}`;

    let response: Response;
    try {
      response = await fetch(url, {
        method,
        headers,
        body: body === undefined ? undefined : JSON.stringify(body),
      });
    } catch (cause: unknown) {
      const detail =
        cause instanceof Error && cause.message.trim().length > 0
          ? cause.message
          : 'Network request failed';
      throw new ApiError(0, 'NETWORK_ERROR', detail);
    }

    if (!response.ok) {
      const payload = await readErrorBody(response);
      // Log URL + status + whether a Bearer was sent; never the token itself.
      console.warn(
        `[Intensity] API ${method} ${url} → ${response.status} ${payload.code}: ${payload.message}` +
          (token ? ' (had Bearer)' : ' (no Bearer)'),
      );

      const error = new ApiError(response.status, payload.code, payload.message);
      if (error.isInvalidToken() && token) {
        this.unauthorizedListener?.(token);
      }
      throw error;
    }

    if (response.status === 204) {
      return undefined as T;
    }

    const text = await response.text();
    if (!text) {
      return undefined as T;
    }

    return JSON.parse(text) as T;
  }
}

async function readErrorBody(response: Response): Promise<ApiErrorBody> {
  const fallback: ApiErrorBody = {
    code: 'UNKNOWN_ERROR',
    message: response.statusText || `HTTP ${response.status}`,
  };

  try {
    const text = await response.text();
    if (!text) {
      return fallback;
    }
    const parsed = JSON.parse(text) as Partial<ApiErrorBody>;
    return {
      code: typeof parsed.code === 'string' ? parsed.code : fallback.code,
      message: typeof parsed.message === 'string' ? parsed.message : fallback.message,
    };
  } catch {
    return fallback;
  }
}
