/**
 * Auth endpoints of the /v1 contract, expressed as a port so use cases never
 * touch the transport directly. Payload shapes mirror openapi/openapi.yaml.
 */
export interface LoginInput {
  email: string;
  password: string;
}

export interface RegisterInput {
  displayName: string;
  email: string;
  password: string;
}

export interface AuthSessionResponse {
  token: string;
  participantId: string;
  displayName: string;
  accessMode: 'EXPERIENCES';
}

export interface RegisterResponse {
  id: string;
  displayName: string;
  email: string;
  token: string;
}

export interface GroupMemberResponse {
  participantId: string;
  displayName: string;
}

export interface JointAuthSessionResponse {
  token: string;
  groupId: string;
  groupIds: string[];
  members: GroupMemberResponse[];
  accessMode: 'EXPERIENCE_BOX';
}

export interface JointLoginRequest {
  credentials: LoginInput[];
  reuseSessionToken?: string;
  targetGroupId?: string;
  requireAllMembers?: boolean;
}

export interface AuthApiPort {
  login(input: LoginInput): Promise<AuthSessionResponse>;
  register(input: RegisterInput): Promise<RegisterResponse>;
  loginGroup(request: JointLoginRequest): Promise<JointAuthSessionResponse>;
  forgotPassword(email: string): Promise<void>;
  resetPassword(token: string, password: string): Promise<void>;
}
