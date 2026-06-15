import { describe, expect, it } from 'vitest';

describe('Intensity shell', () => {
  it('exposes API URL from environment', () => {
    expect(import.meta.env.VITE_API_URL).toBeTruthy();
  });
});
