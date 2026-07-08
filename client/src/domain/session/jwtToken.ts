interface JwtPayload {
  exp?: number;
}

function decodePayload(token: string): JwtPayload | null {
  const parts = token.split('.');
  if (parts.length !== 3) {
    return null;
  }

  try {
    const normalized = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
    const json = atob(padded);
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
}

export function isTokenExpired(token: string, nowMs = Date.now()): boolean {
  const payload = decodePayload(token);
  if (!payload?.exp) {
    return false;
  }

  return payload.exp * 1000 <= nowMs;
}
