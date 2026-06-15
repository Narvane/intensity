import { describe, expect, it } from 'vitest';
import { BOX_TYPES } from '@domain/box/boxTypes';
import { countSuggestionPack, getSuggestions } from '../../content/suggestion-packs/index';

describe('suggestion packs', () => {
  it('exposes 165 suggestions across types and intensities', () => {
    expect(countSuggestionPack()).toBe(165);
  });

  it('returns three localized suggestions per intensity', () => {
    const suggestions = getSuggestions('en', 'SAIDAS_COM_AMIGOS', 3);
    expect(suggestions).toHaveLength(3);
    expect(suggestions.every((item) => item.includes('Outings with friends'))).toBe(true);
  });

  it('varies suggestions by box type', () => {
    const first = getSuggestions('en', BOX_TYPES[0], 2)[0];
    const second = getSuggestions('en', BOX_TYPES[1], 2)[0];
    expect(first).not.toBe(second);
  });
});
