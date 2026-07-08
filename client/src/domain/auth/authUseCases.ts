import type { ApiClient } from '@adapters/api/ApiClient';
import type { SessionPort, SessionState } from '@domain/session/SessionPort';
import { createExperienceBoxSessionMeta } from '@domain/session/experienceBoxSessionPolicy';

export interface LoginInput {
  email: string;
  password: string;
}

export interface RegisterInput {
  displayName: string;
  email: string;
  password: string;
}

interface AuthSessionResponse {
  token: string;
  participantId: string;
  displayName: string;
  accessMode: 'EXPERIENCES';
}

interface RegisterResponse {
  id: string;
  displayName: string;
  email: string;
  token: string;
}

interface GroupMemberResponse {
  participantId: string;
  displayName: string;
}

interface JointAuthSessionResponse {
  token: string;
  groupId: string;
  groupIds: string[];
  members: GroupMemberResponse[];
  accessMode: 'EXPERIENCE_BOX';
}

export class RegisterParticipantUseCase {
  constructor(
    private readonly api: ApiClient,
    private readonly sessionPort: SessionPort,
  ) {}

  async execute(input: RegisterInput): Promise<SessionState> {
    const response = await this.api.post<RegisterResponse>('/v1/participants', input);
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
    private readonly api: ApiClient,
    private readonly sessionPort: SessionPort,
  ) {}

  async execute(input: LoginInput): Promise<SessionState> {
    const response = await this.api.post<AuthSessionResponse>('/v1/auth/login', input);
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
}

export class LoginExperienceBoxUseCase {
  constructor(
    private readonly api: ApiClient,
    private readonly sessionPort: SessionPort,
  ) {}

  async execute(input: LoginExperienceBoxInput): Promise<SessionState> {
    const response = await this.api.post<JointAuthSessionResponse>('/v1/auth/group', {
      credentials: input.credentials,
      reuseSessionToken: input.reuseSessionToken,
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
