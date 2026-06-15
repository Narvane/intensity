import { describe, expect, it } from 'vitest';
import { ValidateInviteCodeFormatUseCase } from '@domain/auth/authUseCases';

describe('ValidateInviteCodeFormatUseCase', () => {
  const useCase = new ValidateInviteCodeFormatUseCase();

  it('accepts valid Crockford codes', () => {
    expect(useCase.execute('AB23CD')).toBe(true);
    expect(useCase.execute('ab23cd')).toBe(true);
  });

  it('rejects ambiguous or short codes', () => {
    expect(useCase.execute('AB12C')).toBe(false);
    expect(useCase.execute('AB10CD')).toBe(false);
    expect(useCase.execute('')).toBe(false);
  });
});
