import type { AuthApiPort, LoginInput, RegisterInput } from '@domain/auth/AuthApiPort';
import type { SessionPort, SessionState } from '@domain/session/SessionPort';
import { createExperienceBoxSessionMeta } from '@domain/session/experienceBoxSessionPolicy';

export type { LoginInput, RegisterInput } from '@domain/auth/AuthApiPort';

export class RegisterParticipantUseCase {
  constructor(
    private readonly authApi: AuthApiPort,
    private readonly sessionPort: SessionPort,
  ) {}

  async execute(input: RegisterInput): Promise<SessionState> {
    const response = await this.authApi.register(input);
    const session: SessionState = {
      token: response.token,
      accessMode: 'EXPERIENCES',
      participantId: response.id,
      displayName: response.displayName,
      email: response.email,
    };
    await this.sessionPort.saveExperiences(session);
    return session;
  }
}

export class LoginExperiencesUseCase {
  constructor(
    private readonly authApi: AuthApiPort,
    private readonly sessionPort: SessionPort,
  ) {}

  async execute(input: LoginInput): Promise<SessionState> {
    const response = await this.authApi.login(input);
    const session: SessionState = {
      token: response.token,
      accessMode: 'EXPERIENCES',
      participantId: response.participantId,
      displayName: response.displayName,
      email: input.email.trim(),
    };
    await this.sessionPort.saveExperiences(session);
    return session;
  }
}

export interface LoginExperienceBoxInput {
  credentials: LoginInput[];
  reuseSessionToken?: string;
  targetGroupId?: string;
  requireAllMembers?: boolean;
}

export class LoginExperienceBoxUseCase {
  constructor(
    private readonly authApi: AuthApiPort,
    private readonly sessionPort: SessionPort,
  ) {}

  async execute(input: LoginExperienceBoxInput): Promise<SessionState> {
    const response = await this.authApi.loginGroup({
      credentials: input.credentials,
      reuseSessionToken: input.reuseSessionToken,
      targetGroupId: input.targetGroupId,
      requireAllMembers: input.requireAllMembers,
    });
    const session: SessionState = {
      token: response.token,
      accessMode: 'EXPERIENCE_BOX',
      groupId: response.groupId,
      groupIds: response.groupIds?.length ? response.groupIds : [response.groupId],
      members: response.members,
      displayName: response.members.map((member) => member.displayName).join(', '),
      experienceBox: createExperienceBoxSessionMeta(),
    };
    await this.sessionPort.saveExperienceBox(session);
    return session;
  }
}

export class LogoutExperiencesUseCase {
  constructor(private readonly sessionPort: SessionPort) {}

  async execute(): Promise<void> {
    await this.sessionPort.clearExperiences();
  }
}

export class LogoutExperienceBoxUseCase {
  constructor(private readonly sessionPort: SessionPort) {}

  async execute(): Promise<void> {
    await this.sessionPort.clearExperienceBox();
  }
}

export class ValidateInviteCodeFormatUseCase {
  execute(code: string): boolean {
    return /^[A-HJ-NP-Z2-9]{6}$/i.test(code.trim());
  }
}

export class RequestPasswordResetUseCase {
  constructor(private readonly authApi: AuthApiPort) {}

  async execute(email: string): Promise<void> {
    await this.authApi.forgotPassword(email.trim());
  }
}

export class ResetPasswordUseCase {
  constructor(private readonly authApi: AuthApiPort) {}

  async execute(token: string, password: string): Promise<void> {
    await this.authApi.resetPassword(token, password);
  }
}
