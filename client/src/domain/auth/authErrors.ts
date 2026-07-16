import { ApiError } from '@adapters/api/ApiClient';

const PASSWORD_MIN = 8;
const PASSWORD_MAX = 128;

export function isValidAuthPasswordLength(password: string): boolean {
  const length = password.length;
  return length >= PASSWORD_MIN && length <= PASSWORD_MAX;
}

export function resolveAuthError(err: unknown, t: (key: string) => string): string {
  if (!(err instanceof ApiError)) {
    return t('auth.errors.network');
  }

  switch (err.code) {
    case 'INVALID_CREDENTIALS':
      return t('auth.errors.invalidCredentials');
    case 'NETWORK_ERROR':
      return t('auth.errors.network');
    case 'GROUP_MEMBERSHIP_CONFLICT':
      return t('auth.errors.groupMembershipConflict');
    case 'GROUP_TARGET_MISMATCH':
      return t('auth.errors.groupTargetMismatch');
    case 'GROUP_REQUIRES_ALL_MEMBERS':
      return t('auth.errors.groupRequiresAllMembers');
    case 'EMAIL_NOT_ALLOWLISTED':
      return t('auth.errors.emailNotAllowlisted');
    case 'EMAIL_ALREADY_REGISTERED':
      return t('auth.errors.emailAlreadyRegistered');
    case 'INVALID_RESET_TOKEN':
      return t('auth.errors.invalidResetToken');
    case 'EMAIL_DELIVERY_FAILED':
      return t('auth.errors.emailDeliveryFailed');
    case 'VALIDATION_ERROR':
      return resolveValidationMessage(err.message, t);
    default:
      return looksTechnical(err.message) ? t('common.error') : err.message;
  }
}

function resolveValidationMessage(message: string, t: (key: string) => string): string {
  const lower = message.toLowerCase();

  if (lower.includes('password') && (lower.includes('size') || lower.includes('between'))) {
    return t('auth.errors.passwordLength');
  }
  if (lower.includes('email') && (lower.includes('well-formed') || lower.includes('email'))) {
    return t('auth.errors.invalidEmail');
  }
  if (lower.includes('displayname') || lower.includes('display_name') || lower.includes('display name')) {
    return t('auth.errors.displayNameInvalid');
  }
  if (lower.includes('must not be blank') || lower.includes('must not be empty')) {
    return t('auth.errors.requiredFields');
  }
  if (lower.includes('provide credentials') || lower.includes('reuse')) {
    return t('auth.errors.credentialsRequired');
  }

  return t('auth.errors.validation');
}

function looksTechnical(message: string): boolean {
  return /[\[\].]|must be|must not|size must|Constraint|Exception/i.test(message);
}
