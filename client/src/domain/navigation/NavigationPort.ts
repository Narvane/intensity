import type { BoxType } from '@domain/box/boxTypes';

export interface NavigationState {
  groupId?: string;
  boxId?: string;
  boxName?: string;
  boxType?: BoxType;
}

export interface NavigationPort {
  load(): Promise<NavigationState>;
  save(state: NavigationState): Promise<void>;
  clear(): Promise<void>;
}
