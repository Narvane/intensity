import { Capacitor, CapacitorHttp } from '@capacitor/core';
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
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    };

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const url = `${this.baseUrl}${path}`;

    try {
      if (Capacitor.isNativePlatform()) {
        // Call the native plugin directly. Do NOT enable the global fetch/XHR
        // patch (CapacitorHttp.enabled) — that path drops Authorization on Android.
        return await this.requestNative<T>(method, url, headers, body, token);
      }
      return await this.requestWeb<T>(method, url, headers, body, token);
    } catch (cause: unknown) {
      if (cause instanceof ApiError) {
        throw cause;
      }
      const detail =
        cause instanceof Error && cause.message.trim().length > 0
          ? cause.message
          : 'Network request failed';
      throw new ApiError(0, 'NETWORK_ERROR', detail);
    }
  }

  private async requestWeb<T>(
    method: string,
    url: string,
    headers: Record<string, string>,
    body: unknown | undefined,
    token?: string,
  ): Promise<T> {
    const response = await fetch(url, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });

    return this.parseHttpResult<T>(
      response.status,
      response.status === 204 ? undefined : await this.readFetchBody(response),
      response.statusText,
      token,
      method,
      url,
    );
  }

  private async requestNative<T>(
    method: string,
    url: string,
    headers: Record<string, string>,
    body: unknown | undefined,
    token?: string,
  ): Promise<T> {
    const result = await CapacitorHttp.request({
      url,
      method,
      headers,
      ...(body === undefined ? {} : { data: body }),
    });

    return this.parseHttpResult<T>(
      result.status,
      result.data,
      `HTTP ${result.status}`,
      token,
      method,
      url,
    );
  }

  private async readFetchBody(response: Response): Promise<unknown> {
    if (response.status === 204) {
      return undefined;
    }
    const text = await response.text();
    if (!text) {
      return undefined;
    }
    try {
      return JSON.parse(text) as unknown;
    } catch {
      return text;
    }
  }

  private parseHttpResult<T>(
    status: number,
    data: unknown,
    statusText: string,
    token: string | undefined,
    method: string,
    url: string,
  ): T {
    if (status < 200 || status >= 300) {
      const payload = asErrorBody(data, statusText);
      const path = url.replace(this.baseUrl, '') || url;
      console.warn(
        `[Intensity] API ${method} ${path} → ${status} ${payload.code}: ${payload.message}`,
      );

      const error = new ApiError(status, payload.code, payload.message);
      if (error.isInvalidToken()) {
        notifyUnauthorized(token);
      }
      throw error;
    }

    if (status === 204 || data === undefined || data === null || data === '') {
      return undefined as T;
    }

    return data as T;
  }
}

function asErrorBody(data: unknown, fallbackMessage: string): ApiErrorBody {
  if (data && typeof data === 'object' && 'code' in data) {
    const body = data as Partial<ApiErrorBody>;
    return {
      code: typeof body.code === 'string' ? body.code : 'UNKNOWN_ERROR',
      message: typeof body.message === 'string' ? body.message : fallbackMessage,
    };
  }
  if (typeof data === 'string' && data.trim()) {
    try {
      return asErrorBody(JSON.parse(data) as unknown, fallbackMessage);
    } catch {
      return { code: 'UNKNOWN_ERROR', message: data };
    }
  }
  return { code: 'UNKNOWN_ERROR', message: fallbackMessage };
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
