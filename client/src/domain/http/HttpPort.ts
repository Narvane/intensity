/**
 * Generic JSON transport used by all API use cases. The optional `token` is a
 * Bearer token attached per call — the client never stores session state.
 */
export interface HttpPort {
  get<T>(path: string, token?: string): Promise<T>;
  post<T>(path: string, body: unknown, token?: string): Promise<T>;
  put<T>(path: string, body: unknown, token?: string): Promise<T>;
  patch<T>(path: string, body: unknown, token?: string): Promise<T>;
  delete(path: string, token?: string): Promise<void>;
}

/**
 * Fired by the HTTP client when a request that carried `failedToken` came
 * back 401 INVALID_TOKEN. Never fired for 401s on requests without a Bearer
 * token, so listeners can trust the token was genuinely rejected.
 */
export type UnauthorizedListener = (failedToken: string) => void;
