import type { Locale } from '../../../i18n/index';
import type { SuggestionText, SuggestionTextByLocale } from './make';
import { DESCONFORTO_LEVE_TEXT } from './desconforto-leve';
import { EXPERIENCIAS_COM_AMIGOS_TEXT } from './experiencias-com-amigos';
import { EXPERIENCIAS_DIFERENTES_TEXT } from './experiencias-diferentes';
import { INTIMO_EM_CASAL_TEXT } from './intimo-em-casal';
import { MOMENTOS_DE_CONEXAO_TEXT } from './momentos-de-conexao';
import { PRIMEIRAS_VEZES_TEXT } from './primeiras-vezes';
import { SAIDAS_COM_AMIGOS_TEXT } from './saidas-com-amigos';
import { SAIDAS_EM_CASAL_TEXT } from './saidas-em-casal';
import { SAIR_DA_ROTINA_TEXT } from './sair-da-rotina';
import { VIAGENS_COM_AMIGOS_TEXT } from './viagens-com-amigos';
import { VIAGENS_EM_CASAL_TEXT } from './viagens-em-casal';

export type { SuggestionText, SuggestionTextByLocale } from './make';

/** All authored suggestion copy, keyed by stable suggestion id. */
export const SUGGESTION_TEXT: Record<string, SuggestionTextByLocale> = {
  ...SAIDAS_COM_AMIGOS_TEXT,
  ...SAIDAS_EM_CASAL_TEXT,
  ...VIAGENS_EM_CASAL_TEXT,
  ...INTIMO_EM_CASAL_TEXT,
  ...VIAGENS_COM_AMIGOS_TEXT,
  ...EXPERIENCIAS_COM_AMIGOS_TEXT,
  ...SAIR_DA_ROTINA_TEXT,
  ...PRIMEIRAS_VEZES_TEXT,
  ...DESCONFORTO_LEVE_TEXT,
  ...MOMENTOS_DE_CONEXAO_TEXT,
  ...EXPERIENCIAS_DIFERENTES_TEXT,
};

export function getSuggestionText(id: string, locale: Locale): SuggestionText | undefined {
  return SUGGESTION_TEXT[id]?.[locale];
}
