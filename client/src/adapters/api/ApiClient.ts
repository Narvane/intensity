import { notifyUnauthorized } from './apiUnauthorizedBridge';

export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly code: string,
    message: string,
  ) {
    super(message);
    this.name = 'ApiError';
  }

  isInvalidToken(): boolean {
    return this.status === 401 && this.code === 'INVALID_TOKEN';
  }
}

export interface ApiErrorBody {
  code: string;
  message: string;
}

export class ApiClient {
  constructor(private readonly baseUrl: string) {}

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
    method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE',
    path: string,
    body: unknown | undefined,
    token?: string,
  ): Promise<T> {
    // Use WebView fetch — same path as the last known-good build.
    // Do NOT enable CapacitorHttp globally and do NOT route through
    // CapacitorHttp.request(): both have dropped Authorization on Android
    // and produced false INVALID_TOKEN logouts.
    const headers: Record<string, string> = {
      Accept: 'application/json',
      // Always set for mutating calls — Spring MVC security matchers historically
      // failed open routes when Content-Type was missing (false INVALID_TOKEN).
      ...(method === 'GET' || method === 'DELETE'
        ? {}
        : { 'Content-Type': 'application/json' }),
    };

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
      console.warn(
        `[Intensity] API ${method} ${url} → ${response.status} ${payload.code}: ${payload.message}` +
          (token ? ' (had Bearer)' : ' (no Bearer)'),
      );

      const error = new ApiError(response.status, payload.code, payload.message);
      // Only kill a session when we actually sent that session's token.
      // A bare 401 without a token must not wipe Preferences.
      if (error.isInvalidToken() && token) {
        notifyUnauthorized(token);
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

export function createApiClient(): ApiClient {
  const baseUrl = import.meta.env.VITE_API_URL?.trim() ?? '';
  if (!baseUrl) {
    console.error(
      '[Intensity] VITE_API_URL is empty. Rebuild with production/demo env or API calls will hit the WebView origin.',
    );
  }
  return new ApiClient(baseUrl);
}
