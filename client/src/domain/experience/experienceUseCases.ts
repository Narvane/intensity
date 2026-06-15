import type { ApiClient } from '@adapters/api/ApiClient';
import type { Experience, ExperienceInput } from '@domain/experience/experienceTypes';

export class ListExperiencesUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(boxId: string, token: string): Promise<Experience[]> {
    return this.api.get<Experience[]>(`/v1/caixinhas/${boxId}/experiencias`, token);
  }
}

export class CreateExperienceUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(boxId: string, token: string, input: ExperienceInput): Promise<Experience> {
    return this.api.post<Experience>(`/v1/caixinhas/${boxId}/experiencias`, input, token);
  }
}

export class UpdateExperienceUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(experienceId: string, token: string, input: ExperienceInput): Promise<Experience> {
    return this.api.put<Experience>(`/v1/experiencias/${experienceId}`, input, token);
  }
}

export class DeleteExperienceUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(experienceId: string, token: string): Promise<void> {
    return this.api.delete(`/v1/experiencias/${experienceId}`, token);
  }
}
