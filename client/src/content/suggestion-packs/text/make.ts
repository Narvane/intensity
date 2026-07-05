import type { Locale } from '../../../i18n/index';

export interface SuggestionText {
  description: string;
  reflection: string;
}

export type SuggestionTextByLocale = Record<Locale, SuggestionText>;

type Pair = readonly [description: string, reflection: string];

/** Compact trilingual authoring helper: make(ptBR, en, it). */
export function make(ptBR: Pair, en: Pair, it: Pair): SuggestionTextByLocale {
  return {
    'pt-BR': { description: ptBR[0], reflection: ptBR[1] },
    en: { description: en[0], reflection: en[1] },
    it: { description: it[0], reflection: it[1] },
  };
}
