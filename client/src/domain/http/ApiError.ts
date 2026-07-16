/**
 * Error surfaced by the HTTP layer for any non-2xx response (or network
 * failure, with status 0 and code NETWORK_ERROR). Domain error resolvers map
 * `code` to i18n messages.
 */
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
