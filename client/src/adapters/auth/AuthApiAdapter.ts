import type {
  AuthApiPort,
  AuthSessionResponse,
  JointAuthSessionResponse,
  JointLoginRequest,
  LoginInput,
  RegisterInput,
  RegisterResponse,
} from '@domain/auth/AuthApiPort';
import type { HttpPort } from '@domain/http/HttpPort';
import { getApiClient } from '@adapters/http/apiClient';

export class AuthApiAdapter implements AuthApiPort {
  constructor(private readonly http: HttpPort) {}

  login(input: LoginInput): Promise<AuthSessionResponse> {
    return this.http.post<AuthSessionResponse>('/v1/auth/login', input);
  }

  register(input: RegisterInput): Promise<RegisterResponse> {
    return this.http.post<RegisterResponse>('/v1/participants', input);
  }

  loginGroup(request: JointLoginRequest): Promise<JointAuthSessionResponse> {
    return this.http.post<JointAuthSessionResponse>('/v1/auth/group', request);
  }

  async forgotPassword(email: string): Promise<void> {
    await this.http.post<void>('/v1/auth/forgot-password', { email });
  }

  async resetPassword(token: string, password: string): Promise<void> {
    await this.http.post<void>('/v1/auth/reset-password', { token, password });
  }
}

export function createAuthApi(): AuthApiPort {
  return new AuthApiAdapter(getApiClient());
}
