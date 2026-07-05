import type { ApiClient } from '@adapters/api/ApiClient';
import type { Experience, ExperienceInput } from '@domain/experience/experienceTypes';

export class ListExperiencesUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(boxId: string, token: string): Promise<Experience[]> {
    return this.api.get<Experience[]>(`/v1/boxes/${boxId}/experiences`, token);
  }
}

export class CreateExperienceUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(boxId: string, token: string, input: ExperienceInput): Promise<Experience> {
    return this.api.post<Experience>(`/v1/boxes/${boxId}/experiences`, input, token);
  }
}

export class CreateExperiencesBatchUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(boxId: string, token: string, inputs: ExperienceInput[]): Promise<Experience[]> {
    return this.api.post<Experience[]>(
      `/v1/boxes/${boxId}/experiences/batch`,
      { experiences: inputs },
      token,
    );
  }
}

export class UpdateExperienceUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(experienceId: string, token: string, input: ExperienceInput): Promise<Experience> {
    return this.api.put<Experience>(`/v1/experiences/${experienceId}`, input, token);
  }
}

export class DeleteExperienceUseCase {
  constructor(private readonly api: ApiClient) {}

  execute(experienceId: string, token: string): Promise<void> {
    return this.api.delete(`/v1/experiences/${experienceId}`, token);
  }
}
