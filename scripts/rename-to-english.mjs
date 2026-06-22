#!/usr/bin/env node
import { execSync } from 'node:child_process';
import fs from 'node:fs';
import path from 'node:path';

const root = path.resolve(import.meta.dirname, '..');

function gitMv(from, to) {
  const fromPath = path.join(root, from);
  const toPath = path.join(root, to);
  if (!fs.existsSync(fromPath)) return;
  fs.mkdirSync(path.dirname(toPath), { recursive: true });
  execSync(`git mv "${from.replace(/\\/g, '/')}" "${to.replace(/\\/g, '/')}"`, { cwd: root, stdio: 'inherit' });
}

function walk(dir, callback) {
  if (!fs.existsSync(dir)) return;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) walk(full, callback);
    else callback(full);
  }
}

function replaceInFile(filePath, pairs) {
  let content = fs.readFileSync(filePath, 'utf8');
  const original = content;
  for (const [from, to] of pairs) content = content.split(from).join(to);
  if (content !== original) fs.writeFileSync(filePath, content, 'utf8');
}

function replaceInTree(relDir, extensions, pairs) {
  const base = path.join(root, relDir);
  walk(base, (file) => {
    if (!extensions.some((ext) => file.endsWith(ext))) return;
    if (file.includes('node_modules') || file.includes(`${path.sep}target${path.sep}`)) return;
    replaceInFile(file, pairs);
  });
}

const packageMoves = [
  ['api/src/main/java/com/intensity/participante', 'api/src/main/java/com/intensity/participant'],
  ['api/src/main/java/com/intensity/grupo', 'api/src/main/java/com/intensity/group'],
  ['api/src/main/java/com/intensity/caixinha', 'api/src/main/java/com/intensity/box'],
  ['api/src/main/java/com/intensity/experiencia', 'api/src/main/java/com/intensity/experience'],
  ['api/src/main/java/com/intensity/convite', 'api/src/main/java/com/intensity/invite'],
];

for (const [from, to] of packageMoves) gitMv(from, to);

const fileRenames = {
  'Participante.java': 'Participant.java',
  'ParticipanteController.java': 'ParticipantController.java',
  'ParticipanteService.java': 'ParticipantService.java',
  'ParticipanteRepository.java': 'ParticipantRepository.java',
  'Grupo.java': 'Group.java',
  'GrupoParticipante.java': 'GroupParticipant.java',
  'GrupoController.java': 'GroupController.java',
  'GrupoMemberController.java': 'GroupMemberController.java',
  'GrupoService.java': 'GroupService.java',
  'GrupoMembershipService.java': 'GroupMembershipService.java',
  'GrupoQueryService.java': 'GroupQueryService.java',
  'GrupoRepository.java': 'GroupRepository.java',
  'Caixinha.java': 'Box.java',
  'CaixinhaController.java': 'BoxController.java',
  'CaixinhaService.java': 'BoxService.java',
  'CaixinhaRepository.java': 'BoxRepository.java',
  'Experiencia.java': 'Experience.java',
  'ExperienciaController.java': 'ExperienceController.java',
  'ExperienciaService.java': 'ExperienceService.java',
  'ExperienciaRepository.java': 'ExperienceRepository.java',
  'Convite.java': 'Invite.java',
  'ConviteController.java': 'InviteController.java',
  'ConviteService.java': 'InviteService.java',
  'ConviteRepository.java': 'InviteRepository.java',
  'ConviteFactory.java': 'InviteFactory.java',
  'ConviteIntegrationTest.java': 'InviteIntegrationTest.java',
  'ConvitePersistenceIntegrationTest.java': 'InvitePersistenceIntegrationTest.java',
  'ConviteTest.java': 'InviteTest.java',
  'CaixinhaIntegrationTest.java': 'BoxIntegrationTest.java',
  'ExperienciaIntegrationTest.java': 'ExperienceIntegrationTest.java',
  'GrupoIntegrationTest.java': 'GroupIntegrationTest.java',
};

for (const relDir of [
  'api/src/main/java/com/intensity/participant',
  'api/src/main/java/com/intensity/group',
  'api/src/main/java/com/intensity/box',
  'api/src/main/java/com/intensity/experience',
  'api/src/main/java/com/intensity/invite',
  'api/src/test/java/com/intensity',
]) {
  const base = path.join(root, relDir);
  walk(base, (file) => {
    const baseName = path.basename(file);
    const target = fileRenames[baseName];
    if (target) gitMv(path.relative(root, file), path.join(path.relative(root, path.dirname(file)), target));
  });
}

gitMv('client/src/domain/convite', 'client/src/domain/invite');
gitMv('client/src/domain/sorteio', 'client/src/domain/draw');
gitMv('client/src/domain/draw/ExecutarSorteioUseCase.ts', 'client/src/domain/draw/ExecuteDrawUseCase.ts');
gitMv('client/src/domain/draw/FiltroIntensidadePolicy.ts', 'client/src/domain/draw/IntensityFilterPolicy.ts');
gitMv('client/src/domain/draw/RevelacaoOrchestrator.ts', 'client/src/domain/draw/RevelationOrchestrator.ts');
gitMv('client/src/domain/draw/sorteio.test.ts', 'client/src/domain/draw/draw.test.ts');

const javaPairs = [
  ['com.intensity.participante', 'com.intensity.participant'],
  ['com.intensity.grupo', 'com.intensity.group'],
  ['com.intensity.caixinha', 'com.intensity.box'],
  ['com.intensity.experiencia', 'com.intensity.experience'],
  ['com.intensity.convite', 'com.intensity.invite'],
  ['GrupoParticipante', 'GroupParticipant'],
  ['GrupoMembershipService', 'GroupMembershipService'],
  ['GrupoMemberController', 'GroupMemberController'],
  ['GrupoQueryService', 'GroupQueryService'],
  ['ParticipanteController', 'ParticipantController'],
  ['ParticipanteService', 'ParticipantService'],
  ['ParticipanteRepository', 'ParticipantRepository'],
  ['Participante', 'Participant'],
  ['GrupoController', 'GroupController'],
  ['GrupoService', 'GroupService'],
  ['GrupoRepository', 'GroupRepository'],
  ['Grupo', 'Group'],
  ['CaixinhaController', 'BoxController'],
  ['CaixinhaService', 'BoxService'],
  ['CaixinhaRepository', 'BoxRepository'],
  ['Caixinha', 'Box'],
  ['ExperienciaController', 'ExperienceController'],
  ['ExperienciaService', 'ExperienceService'],
  ['ExperienciaRepository', 'ExperienceRepository'],
  ['Experiencia', 'Experience'],
  ['ConviteController', 'InviteController'],
  ['ConviteFactory', 'InviteFactory'],
  ['ConviteService', 'InviteService'],
  ['ConviteRepository', 'InviteRepository'],
  ['Convite', 'Invite'],
  ['ConviteIntegrationTest', 'InviteIntegrationTest'],
  ['ConvitePersistenceIntegrationTest', 'InvitePersistenceIntegrationTest'],
  ['ConviteTest', 'InviteTest'],
  ['CaixinhaIntegrationTest', 'BoxIntegrationTest'],
  ['ExperienciaIntegrationTest', 'ExperienceIntegrationTest'],
  ['GrupoIntegrationTest', 'GroupIntegrationTest'],
  ['@Table(name = "grupo_participante")', '@Table(name = "group_participant")'],
  ['@Table(name = "participante")', '@Table(name = "participant")'],
  ['@Table(name = "grupo")', '@Table(name = "group")'],
  ['@Table(name = "caixinha")', '@Table(name = "box")'],
  ['@Table(name = "experiencia")', '@Table(name = "experience")'],
  ['@Table(name = "convite")', '@Table(name = "invite")'],
  ['name = "grupo_id"', 'name = "group_id"'],
  ['name = "participante_id"', 'name = "participant_id"'],
  ['name = "caixinha_id"', 'name = "box_id"'],
  ['"/v1/auth/grupo"', '"/v1/auth/group"'],
  ['"/v1/participantes"', '"/v1/participants"'],
  ['"/v1/grupos/{groupId}/membros"', '"/v1/groups/{groupId}/members"'],
  ['"/v1/grupos/{groupId}/convites"', '"/v1/groups/{groupId}/invites"'],
  ['"/v1/grupos/{groupId}/caixinhas"', '"/v1/groups/{groupId}/boxes"'],
  ['"/v1/grupos"', '"/v1/groups"'],
  ['"/v1/convites/validar"', '"/v1/invites/validate"'],
  ['"/v1/convites/{inviteId}/aceitar"', '"/v1/invites/{inviteId}/accept"'],
  ['"/v1/convites/', '"/v1/invites/'],
  ['"/v1/caixinhas/{boxId}/experiencias"', '"/v1/boxes/{boxId}/experiences"'],
  ['"/v1/caixinhas/', '"/v1/boxes/'],
  ['"/v1/experiencias/', '"/v1/experiences/'],
  ['grupoMembershipService', 'groupMembershipService'],
  ['grupoQueryService', 'groupQueryService'],
  ['grupoService', 'groupService'],
  ['grupoRepository', 'groupRepository'],
  ['participanteRepository', 'participantRepository'],
  ['participanteService', 'participantService'],
  ['caixinhaService', 'boxService'],
  ['caixinhaRepository', 'boxRepository'],
  ['experienciaService', 'experienceService'],
  ['experienciaRepository', 'experienceRepository'],
  ['conviteService', 'inviteService'],
  ['conviteRepository', 'inviteRepository'],
  ['conviteFactory', 'inviteFactory'],
  [' private Group group', ' private Group group'],
  ['private Group group', 'private Group group'],
  ['(Group group', '(Group group'],
  ['Group group', 'Group group'],
  ['return group', 'return group'],
  ['this.group =', 'this.group ='],
  ['Participant participant', 'Participant participant'],
  ['private Participant participant', 'private Participant participant'],
  ['Box box', 'Box box'],
  ['Experience experience', 'Experience experience'],
  ['Invite invite', 'Invite invite'],
  ['post("/v1/auth/grupo"', 'post("/v1/auth/group"'],
  ['post("/v1/participantes"', 'post("/v1/participants"'],
  ['get("/v1/grupos"', 'get("/v1/groups"'],
  ['post("/v1/grupos/', 'post("/v1/groups/'],
  ['get("/v1/grupos/', 'get("/v1/groups/'],
  ['delete("/v1/grupos/', 'delete("/v1/groups/'],
  ['@RequestMapping("/v1/grupos/{groupId}/membros")', '@RequestMapping("/v1/groups/{groupId}/members")'],
  ['@GetMapping("/caixinhas/{boxId}/experiencias")', '@GetMapping("/boxes/{boxId}/experiences")'],
  ['@PostMapping("/caixinhas/{boxId}/experiencias")', '@PostMapping("/boxes/{boxId}/experiences")'],
  ['@PutMapping("/experiencias/{experienceId}")', '@PutMapping("/experiences/{experienceId}")'],
  ['@DeleteMapping("/experiencias/{experienceId}")', '@DeleteMapping("/experiences/{experienceId}")'],
];

replaceInTree('api/src', ['.java'], javaPairs);

const tsPairs = [
  ['@domain/convite/', '@domain/invite/'],
  ['@domain/sorteio/', '@domain/draw/'],
  ['ExecutarSorteioUseCase', 'ExecuteDrawUseCase'],
  ['FiltroIntensidadePolicy', 'IntensityFilterPolicy'],
  ['RevelacaoOrchestrator', 'RevelationOrchestrator'],
  ['/v1/auth/grupo', '/v1/auth/group'],
  ['/v1/participantes', '/v1/participants'],
  ['/v1/grupos/', '/v1/groups/'],
  ['/v1/grupos', '/v1/groups'],
  ['/v1/convites/validar', '/v1/invites/validate'],
  ['/v1/convites/', '/v1/invites/'],
  ['/v1/caixinhas/', '/v1/boxes/'],
  ['/v1/caixinhas', '/v1/boxes'],
  ['/v1/experiencias/', '/v1/experiences/'],
  ['/membros', '/members'],
  ['/convites', '/invites'],
  ['/caixinhas', '/boxes'],
  ['/experiencias', '/experiences'],
  ['/aceitar', '/accept'],
  ['/validar', '/validate'],
];

replaceInTree('client/src', ['.ts', '.tsx'], tsPairs);

const openapiPath = path.join(root, 'openapi/openapi.yaml');
const openapiPairs = [
  ['/v1/auth/grupo:', '/v1/auth/group:'],
  ['/v1/participantes:', '/v1/participants:'],
  ['/v1/grupos/{groupId}/membros:', '/v1/groups/{groupId}/members:'],
  ['/v1/grupos/{groupId}/convites:', '/v1/groups/{groupId}/invites:'],
  ['/v1/convites/validar:', '/v1/invites/validate:'],
  ['/v1/convites/{inviteId}/aceitar:', '/v1/invites/{inviteId}/accept:'],
  ['/v1/convites/{inviteId}:', '/v1/invites/{inviteId}:'],
  ['/v1/grupos/{groupId}/caixinhas:', '/v1/groups/{groupId}/boxes:'],
  ['/v1/caixinhas/{boxId}/experiencias:', '/v1/boxes/{boxId}/experiences:'],
  ['/v1/caixinhas/{boxId}:', '/v1/boxes/{boxId}:'],
  ['/v1/caixinhas:', '/v1/boxes:'],
  ['/v1/experiencias/{experienceId}:', '/v1/experiences/{experienceId}:'],
  ['/v1/grupos:', '/v1/groups:'],
];
replaceInFile(openapiPath, openapiPairs);

console.log('Rename to English completed.');
