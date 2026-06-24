import { describe, expect, it, vi } from 'vitest';
import { BOX_TYPES } from '@domain/box/boxTypes';
import {
  countSuggestions,
  getSuggestionById,
  listSuggestions,
  pickRandomSuggestion,
} from './index';
import { ALL_SUGGESTION_SEEDS } from './packs';

describe('suggestion packs', () => {
  it('exposes 165 suggestions across types and intensities', () => {
    expect(countSuggestions()).toBe(165);
    expect(ALL_SUGGESTION_SEEDS).toHaveLength(165);
  });

  it('returns 15 suggestions per box type', () => {
    for (const boxType of BOX_TYPES) {
      expect(countSuggestions(boxType)).toBe(15);
      expect(listSuggestions('pt-BR', boxType)).toHaveLength(15);
    }
  });

  it('returns three localized suggestions per intensity level', () => {
    const suggestions = listSuggestions('en', 'SAIDAS_COM_AMIGOS', 3);
    expect(suggestions).toHaveLength(3);
    expect(suggestions.every((item) => item.boxType === 'SAIDAS_COM_AMIGOS')).toBe(true);
    expect(suggestions.every((item) => item.intensity === 3)).toBe(true);
    expect(suggestions.every((item) => item.description.includes('Outings with friends'))).toBe(
      true,
    );
  });

  it('varies suggestions by box type', () => {
    const first = listSuggestions('en', BOX_TYPES[0], 2)[0];
    const second = listSuggestions('en', BOX_TYPES[1], 2)[0];
    expect(first.id).not.toBe(second.id);
    expect(first.description).not.toBe(second.description);
  });

  it('never mixes box types in list results', () => {
    for (const boxType of BOX_TYPES) {
      const suggestions = listSuggestions('pt-BR', boxType);
      expect(suggestions.every((item) => item.boxType === boxType)).toBe(true);
    }
  });

  it('resolves suggestions by stable id', () => {
    const suggestion = getSuggestionById('pt-BR', 'SAIDAS_EM_CASAL-3-02');
    expect(suggestion?.id).toBe('SAIDAS_EM_CASAL-3-02');
    expect(suggestion?.boxType).toBe('SAIDAS_EM_CASAL');
    expect(suggestion?.intensity).toBe(3);
    expect(suggestion?.reflection.length).toBeGreaterThan(0);
    expect(suggestion?.parameters.effort).toBeGreaterThanOrEqual(1);
  });

  it('pickRandomSuggestion stays within box type and intensity', () => {
    const randomSpy = vi.spyOn(Math, 'random').mockReturnValue(0);
    const picked = pickRandomSuggestion('en', 'VIAGENS_COM_AMIGOS', 4);
    randomSpy.mockRestore();

    expect(picked.boxType).toBe('VIAGENS_COM_AMIGOS');
    expect(picked.intensity).toBe(4);
  });

  it('pickRandomSuggestion can exclude the current suggestion', () => {
    const pool = listSuggestions('en', 'SAIR_DA_ROTINA', 1);
    const excludeId = pool[0].id;
  const randomSpy = vi.spyOn(Math, 'random').mockReturnValue(0);
    const picked = pickRandomSuggestion('en', 'SAIR_DA_ROTINA', 1, excludeId);
    randomSpy.mockRestore();

    expect(picked.id).not.toBe(excludeId);
  });
});
