type UnauthorizedHandler = (token?: string) => void | Promise<void>;

let handler: UnauthorizedHandler | null = null;

export function registerUnauthorizedHandler(next: UnauthorizedHandler | null): void {
  handler = next;
}

export function notifyUnauthorized(token?: string): void {
  void handler?.(token);
}
