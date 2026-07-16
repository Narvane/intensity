import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from '@domain/http/ApiError';
import { FetchHttpClient } from '@adapters/http/FetchHttpClient';

function jsonResponse(status: number, body?: unknown): Response {
  return new Response(body === undefined ? null : JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}

describe('FetchHttpClient', () => {
  const fetchMock = vi.fn();

  beforeEach(() => {
    vi.stubGlobal('fetch', fetchMock);
    fetchMock.mockReset();
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('sends Authorization and Content-Type on authenticated POSTs', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, { ok: true }));
    const client = new FetchHttpClient('https://api.test');

    await client.post('/v1/things', { a: 1 }, 'my-token');

    expect(fetchMock).toHaveBeenCalledWith('https://api.test/v1/things', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
        Authorization: 'Bearer my-token',
      },
      body: JSON.stringify({ a: 1 }),
    });
  });

  it('does not send Content-Type on GET', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, []));
    const client = new FetchHttpClient('https://api.test');

    await client.get('/v1/things');

    const headers = fetchMock.mock.calls[0][1].headers as Record<string, string>;
    expect(headers['Content-Type']).toBeUndefined();
    expect(headers.Authorization).toBeUndefined();
  });

  it('notifies the unauthorized listener with the rejected token on 401 INVALID_TOKEN', async () => {
    fetchMock.mockResolvedValue(
      jsonResponse(401, { code: 'INVALID_TOKEN', message: 'Invalid or expired token.' }),
    );
    const client = new FetchHttpClient('https://api.test');
    const listener = vi.fn();
    client.setUnauthorizedListener(listener);

    await expect(client.get('/v1/groups', 'rejected-token')).rejects.toBeInstanceOf(ApiError);

    expect(listener).toHaveBeenCalledTimes(1);
    expect(listener).toHaveBeenCalledWith('rejected-token');
  });

  it('does not notify the listener on a token-less 401', async () => {
    fetchMock.mockResolvedValue(
      jsonResponse(401, { code: 'INVALID_TOKEN', message: 'Invalid or expired token.' }),
    );
    const client = new FetchHttpClient('https://api.test');
    const listener = vi.fn();
    client.setUnauthorizedListener(listener);

    await expect(client.get('/v1/groups')).rejects.toBeInstanceOf(ApiError);

    expect(listener).not.toHaveBeenCalled();
  });

  it('does not notify the listener on 401 INVALID_CREDENTIALS', async () => {
    fetchMock.mockResolvedValue(
      jsonResponse(401, { code: 'INVALID_CREDENTIALS', message: 'Invalid email or password.' }),
    );
    const client = new FetchHttpClient('https://api.test');
    const listener = vi.fn();
    client.setUnauthorizedListener(listener);

    await expect(
      client.post('/v1/auth/login', { email: 'a@b.c', password: 'wrong-pass' }, 'some-token'),
    ).rejects.toMatchObject({ status: 401, code: 'INVALID_CREDENTIALS' });

    expect(listener).not.toHaveBeenCalled();
  });

  it('maps 204 to undefined', async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));
    const client = new FetchHttpClient('https://api.test');

    await expect(client.post('/v1/auth/forgot-password', { email: 'a@b.c' })).resolves.toBeUndefined();
  });

  it('maps network failures to ApiError NETWORK_ERROR', async () => {
    fetchMock.mockRejectedValue(new TypeError('Failed to fetch'));
    const client = new FetchHttpClient('https://api.test');

    await expect(client.get('/v1/groups')).rejects.toMatchObject({
      status: 0,
      code: 'NETWORK_ERROR',
    });
  });
});
