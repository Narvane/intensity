import type { BoxType } from '@domain/box/boxTypes';
import type { ExperienceSuggestionSeed } from './types';
import { DESCONFORTO_LEVE_PACK } from './by-type/desconforto-leve';
import { EXPERIENCIAS_COM_AMIGOS_PACK } from './by-type/experiencias-com-amigos';
import { EXPERIENCIAS_DIFERENTES_PACK } from './by-type/experiencias-diferentes';
import { INTIMO_EM_CASAL_PACK } from './by-type/intimo-em-casal';
import { MOMENTOS_DE_CONEXAO_PACK } from './by-type/momentos-de-conexao';
import { PRIMEIRAS_VEZES_PACK } from './by-type/primeiras-vezes';
import { SAIDAS_COM_AMIGOS_PACK } from './by-type/saidas-com-amigos';
import { SAIDAS_EM_CASAL_PACK } from './by-type/saidas-em-casal';
import { SAIR_DA_ROTINA_PACK } from './by-type/sair-da-rotina';
import { VIAGENS_COM_AMIGOS_PACK } from './by-type/viagens-com-amigos';
import { VIAGENS_EM_CASAL_PACK } from './by-type/viagens-em-casal';

export const SUGGESTION_PACKS: Record<BoxType, ExperienceSuggestionSeed[]> = {
  SAIDAS_COM_AMIGOS: SAIDAS_COM_AMIGOS_PACK,
  SAIDAS_EM_CASAL: SAIDAS_EM_CASAL_PACK,
  VIAGENS_EM_CASAL: VIAGENS_EM_CASAL_PACK,
  INTIMO_EM_CASAL: INTIMO_EM_CASAL_PACK,
  VIAGENS_COM_AMIGOS: VIAGENS_COM_AMIGOS_PACK,
  EXPERIENCIAS_COM_AMIGOS: EXPERIENCIAS_COM_AMIGOS_PACK,
  SAIR_DA_ROTINA: SAIR_DA_ROTINA_PACK,
  PRIMEIRAS_VEZES: PRIMEIRAS_VEZES_PACK,
  DESCONFORTO_LEVE: DESCONFORTO_LEVE_PACK,
  MOMENTOS_DE_CONEXAO: MOMENTOS_DE_CONEXAO_PACK,
  EXPERIENCIAS_DIFERENTES: EXPERIENCIAS_DIFERENTES_PACK,
};

export const ALL_SUGGESTION_SEEDS: ExperienceSuggestionSeed[] = Object.values(
  SUGGESTION_PACKS,
).flat();

const SEED_BY_ID = new Map(ALL_SUGGESTION_SEEDS.map((seed) => [seed.id, seed]));

export function getSuggestionSeedById(id: string): ExperienceSuggestionSeed | undefined {
  return SEED_BY_ID.get(id);
}
