import type { Experience } from '@domain/experience/experienceTypes';
import {
  filterExperiencesByIntensity,
  type IntensityFilter,
} from '@domain/draw/IntensityFilterPolicy';

export interface DrawResult {
  experience: Experience;
  filter: IntensityFilter;
  drawnAt: number;
}

export class ExecuteDrawUseCase {
  execute(pool: Experience[], filter: IntensityFilter): DrawResult | null {
    const eligible = filterExperiencesByIntensity(pool, filter);
    if (eligible.length === 0) {
      return null;
    }

    const index = Math.floor(Math.random() * eligible.length);
    return {
      experience: eligible[index],
      filter,
      drawnAt: Date.now(),
    };
  }
}
