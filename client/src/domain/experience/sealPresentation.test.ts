import { describe, expect, it } from 'vitest';
import { isValidSealFormat } from '@domain/experience/sealPresentation';

describe('sealPresentation', () => {
  it('accepts eight-character uppercase hex seals', () => {
    expect(isValidSealFormat('A1B2C3D4')).toBe(true);
  });

  it('rejects invalid seal formats', () => {
    expect(isValidSealFormat('abc')).toBe(false);
    expect(isValidSealFormat('123456789')).toBe(false);
    expect(isValidSealFormat('gggggggg')).toBe(false);
  });
});
