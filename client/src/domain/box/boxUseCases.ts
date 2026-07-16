import type { HttpPort } from '@domain/http/HttpPort';
import type { Box, BoxType, Group, GroupAccent } from '@domain/box/boxTypes';
import { DEFAULT_BOX_TYPE } from '@domain/box/boxTypes';
import type { NavigationPort } from '@domain/navigation/NavigationPort';

export class ListGroupsUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(token: string): Promise<Group[]> {
    return this.api.get<Group[]>('/v1/groups', token);
  }
}

export interface CreateGroupInput {
  name: string;
  color: GroupAccent;
}

export class CreateGroupUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(token: string, input: CreateGroupInput): Promise<Group> {
    return this.api.post<Group>(
      '/v1/groups',
      { name: input.name.trim(), color: input.color },
      token,
    );
  }
}

export interface UpdateGroupInput {
  name: string;
  color: GroupAccent;
}

export class UpdateGroupUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(groupId: string, token: string, input: UpdateGroupInput): Promise<Group> {
    return this.api.patch<Group>(
      `/v1/groups/${groupId}`,
      { name: input.name.trim(), color: input.color },
      token,
    );
  }
}

export class ListBoxesUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(groupId: string, token: string): Promise<Box[]> {
    return this.api.get<Box[]>(`/v1/groups/${groupId}/boxes`, token);
  }
}

export interface CreateBoxInput {
  groupId: string;
  name: string;
  type?: BoxType;
  requireAllParticipants?: boolean;
}

export class CreateBoxUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(token: string, input: CreateBoxInput): Promise<Box> {
    return this.api.post<Box>(
      '/v1/boxes',
      {
        groupId: input.groupId,
        name: input.name.trim(),
        type: input.type ?? DEFAULT_BOX_TYPE,
        requireAllParticipants: input.requireAllParticipants ?? false,
      },
      token,
    );
  }
}

export interface UpdateBoxInput {
  name: string;
  requireAllParticipants: boolean;
}

export class UpdateBoxUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(boxId: string, token: string, input: UpdateBoxInput): Promise<Box> {
    return this.api.patch<Box>(
      `/v1/boxes/${boxId}`,
      {
        name: input.name.trim(),
        requireAllParticipants: input.requireAllParticipants,
      },
      token,
    );
  }
}

export class DeleteBoxUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(boxId: string, token: string): Promise<void> {
    return this.api.delete(`/v1/boxes/${boxId}`, token);
  }
}

export class LeaveGroupUseCase {
  constructor(private readonly api: HttpPort) {}

  execute(groupId: string, token: string): Promise<void> {
    return this.api.delete(`/v1/groups/${groupId}/members`, token);
  }
}

export class SelectGroupUseCase {
  constructor(private readonly navigation: NavigationPort) {}

  async execute(groupId: string): Promise<void> {
    await this.navigation.save({ groupId, boxId: undefined, boxName: undefined });
  }
}

export class SelectBoxUseCase {
  constructor(private readonly navigation: NavigationPort) {}

  async execute(groupId: string, box: Pick<Box, 'id' | 'name'>): Promise<void> {
    await this.navigation.save({ groupId, boxId: box.id, boxName: box.name });
  }
}
