import type { HttpPort } from '@domain/http/HttpPort';
import type {
  AcceptInviteResult,
  Invite,
  InvitePreview,
} from '@domain/invite/inviteTypes';

export class CreateInviteUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(groupId: string, token: string): Promise<Invite> {
    return this.api.post<Invite>(`/v1/groups/${groupId}/invites`, {}, token);
  }
}

export class ValidateInviteUseCase {
  constructor(private readonly api: HttpPort) {}

  executeByCode(code: string): Promise<InvitePreview> {
    return this.api.get<InvitePreview>(`/v1/invites/validate?code=${encodeURIComponent(code.trim())}`);
  }

  executeByLinkToken(linkToken: string): Promise<InvitePreview> {
    return this.api.get<InvitePreview>(`/v1/invites/validate?t=${encodeURIComponent(linkToken.trim())}`);
  }
}

export class AcceptInviteUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(inviteId: string, token: string): Promise<AcceptInviteResult> {
    return this.api.post<AcceptInviteResult>(`/v1/invites/${inviteId}/accept`, {}, token);
  }
}

export class RevokeInviteUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(inviteId: string, token: string): Promise<void> {
    return this.api.delete(`/v1/invites/${inviteId}`, token);
  }
}
