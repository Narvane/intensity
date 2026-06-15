export const SEAL_LENGTH = 8;
export const SEAL_PATTERN = /^[A-F0-9]{8}$/;

export function isValidSealFormat(seal: string): boolean {
  return SEAL_PATTERN.test(seal);
}
