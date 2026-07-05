import { describe, expect, it } from 'vitest';
import type { Experience } from '@domain/experience/experienceTypes';
import { ExecuteDrawUseCase } from '@domain/draw/ExecuteDrawUseCase';
import { filterExperiencesByIntensity } from '@domain/draw/IntensityFilterPolicy';
import { RevelationOrchestrator } from '@domain/draw/RevelationOrchestrator';

function experience(intensity: number, id = crypto.randomUUID()): Experience {
  return {
    id,
    boxId: 'box-1',
    authorId: 'author-1',
    intensity,
    parameters: { effort: intensity, unpredictability: intensity, novelty: intensity },
    type: 'none',
    seal: 'ABCD1234',
    summaryOnly: false,
    createdAt: new Date().toISOString(),
  };
}

describe('IntensityFilterPolicy', () => {
  const pool = [experience(1), experience(3), experience(5)];

  it('filters exact intensity', () => {
    const filtered = filterExperiencesByIntensity(pool, { mode: 'EXACT', level: 3 });
    expect(filtered).toHaveLength(1);
    expect(filtered[0].intensity).toBe(3);
  });

  it('filters up to intensity', () => {
    const filtered = filterExperiencesByIntensity(pool, { mode: 'UP_TO', level: 3 });
    expect(filtered.map((item) => item.intensity)).toEqual([1, 3]);
  });
});

describe('ExecuteDrawUseCase', () => {
  it('returns null when filter pool is empty', () => {
    const useCase = new ExecuteDrawUseCase();
    const result = useCase.execute([experience(5)], { mode: 'EXACT', level: 1 });
    expect(result).toBeNull();
  });

  it('selects from eligible pool', () => {
    const useCase = new ExecuteDrawUseCase();
    const only = experience(3, '00000000-0000-4000-8000-000000000003');
    const result = useCase.execute([only], { mode: 'EXACT', level: 3 });
    expect(result?.experience.id).toBe('00000000-0000-4000-8000-000000000003');
  });
});

describe('RevelationOrchestrator', () => {
  it('moves from draw to reveal and back', () => {
    const orchestrator = new RevelationOrchestrator();
    const draw = new ExecuteDrawUseCase();
    const result = draw.execute([experience(2)], { mode: 'ANY', level: 3 });
    expect(result).not.toBeNull();

    const drawn = orchestrator.applyDraw(orchestrator.createIdleSession(), result!);
    expect(drawn.phase).toBe('drawn');

    const revealed = orchestrator.reveal(drawn);
    expect(revealed.phase).toBe('revealed');

    const reset = orchestrator.backToDraw();
    expect(reset.phase).toBe('idle');
    expect(reset.result).toBeNull();
  });
});
