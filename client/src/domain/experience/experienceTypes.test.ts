import { describe, expect, it } from 'vitest';
import { DEFAULT_PARAMETERS, suggestIntensity } from '@domain/experience/experienceTypes';

describe('suggestIntensity', () => {
  it('rounds the average of parameters', () => {
    expect(suggestIntensity({ effort: 2, unpredictability: 2, novelty: 2 })).toBe(2);
    expect(suggestIntensity({ effort: 3, unpredictability: 4, novelty: 5 })).toBe(4);
  });

  it('clamps to the 1-5 range', () => {
    expect(suggestIntensity(DEFAULT_PARAMETERS)).toBe(3);
  });
});
