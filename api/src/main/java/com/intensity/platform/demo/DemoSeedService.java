package com.intensity.platform.demo;

import com.intensity.box.entity.Box;
import com.intensity.box.entity.BoxType;
import com.intensity.box.repository.BoxRepository;
import com.intensity.experience.entity.Experience;
import com.intensity.experience.entity.ExperienceType;
import com.intensity.experience.repository.ExperienceRepository;
import com.intensity.experience.service.SealService;
import com.intensity.group.entity.Group;
import com.intensity.group.entity.GroupParticipant;
import com.intensity.group.repository.GroupParticipantRepository;
import com.intensity.group.repository.GroupRepository;
import com.intensity.invite.entity.Invite;
import com.intensity.invite.repository.InviteRepository;
import com.intensity.participant.entity.AllowlistEmail;
import com.intensity.participant.entity.Participant;
import com.intensity.participant.repository.AllowlistEmailRepository;
import com.intensity.participant.repository.ParticipantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Idempotent sample world for the public demo environment (see {@code demo-plan.md}).
 */
@Service
public class DemoSeedService {

	public static final String LEO_EMAIL = "leo@demo.intensity.app";
	public static final String MAYA_EMAIL = "maya@demo.intensity.app";
	public static final String NICO_EMAIL = "nico@demo.intensity.app";
	public static final String DEMO_PASSWORD = "demo1234";

	public static final String COUPLE_GROUP_NAME = "Leo & Maya";
	public static final String TRIO_GROUP_NAME = "Trio de viagem";

	/** Valid invite alphabet (no I/O/0/1). */
	public static final String DEMO_INVITE_CODE = "DEMOTR";
	public static final UUID DEMO_INVITE_LINK_TOKEN = UUID.fromString("aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeeeeeee");

	private static final Logger log = LoggerFactory.getLogger(DemoSeedService.class);

	private final AllowlistEmailRepository allowlistEmailRepository;
	private final ParticipantRepository participantRepository;
	private final GroupRepository groupRepository;
	private final GroupParticipantRepository groupParticipantRepository;
	private final BoxRepository boxRepository;
	private final ExperienceRepository experienceRepository;
	private final InviteRepository inviteRepository;
	private final PasswordEncoder passwordEncoder;
	private final SealService sealService;

	public DemoSeedService(
			AllowlistEmailRepository allowlistEmailRepository,
			ParticipantRepository participantRepository,
			GroupRepository groupRepository,
			GroupParticipantRepository groupParticipantRepository,
			BoxRepository boxRepository,
			ExperienceRepository experienceRepository,
			InviteRepository inviteRepository,
			PasswordEncoder passwordEncoder,
			SealService sealService) {
		this.allowlistEmailRepository = allowlistEmailRepository;
		this.participantRepository = participantRepository;
		this.groupRepository = groupRepository;
		this.groupParticipantRepository = groupParticipantRepository;
		this.boxRepository = boxRepository;
		this.experienceRepository = experienceRepository;
		this.inviteRepository = inviteRepository;
		this.passwordEncoder = passwordEncoder;
		this.sealService = sealService;
	}

	@Transactional
	public boolean seedIfEmpty() {
		if (participantRepository.existsByEmailIgnoreCase(LEO_EMAIL)) {
			log.info("Demo seed already present; skipping");
			return false;
		}

		log.info("Applying demo seed (Leo, Maya, Nico)");

		ensureAllowlisted(LEO_EMAIL, MAYA_EMAIL, NICO_EMAIL);

		String passwordHash = passwordEncoder.encode(DEMO_PASSWORD);
		Participant leo = participantRepository.save(new Participant("Leo", LEO_EMAIL, passwordHash));
		Participant maya = participantRepository.save(new Participant("Maya", MAYA_EMAIL, passwordHash));
		Participant nico = participantRepository.save(new Participant("Nico", NICO_EMAIL, passwordHash));

		Group couple = groupRepository.save(Group.createNew(COUPLE_GROUP_NAME, "coral"));
		addMembers(couple, leo, maya);

		Group trio = groupRepository.save(Group.createNew(TRIO_GROUP_NAME, "teal"));
		addMembers(trio, leo, maya, nico);

		Box weekend = boxRepository.save(new Box(couple, "Fim de semana", BoxType.SAIDAS_EM_CASAL, false));
		Box routine = boxRepository.save(new Box(couple, "Sair da rotina", BoxType.SAIR_DA_ROTINA, false));
		boxRepository.save(new Box(couple, "Só nós dois", BoxType.MOMENTOS_DE_CONEXAO, true));

		Box trip = boxRepository.save(new Box(trio, "Próxima viagem", BoxType.VIAGENS_COM_AMIGOS, false));
		boxRepository.save(new Box(trio, "Rolês da galera", BoxType.EXPERIENCIAS_COM_AMIGOS, true));

		seedWeekendExperiences(weekend, leo, maya);
		seedRoutineExperiences(routine, leo, maya);
		seedTripExperiences(trip, leo, maya, nico);

		Instant now = Instant.now();
		inviteRepository.save(new Invite(
				trio,
				leo,
				DEMO_INVITE_CODE,
				DEMO_INVITE_LINK_TOKEN,
				now.plus(365, ChronoUnit.DAYS),
				now));

		log.info("Demo seed complete");
		return true;
	}

	private void ensureAllowlisted(String... emails) {
		for (String email : emails) {
			if (!allowlistEmailRepository.existsById(email.toLowerCase())) {
				allowlistEmailRepository.save(new AllowlistEmail(email));
			}
		}
	}

	private void addMembers(Group group, Participant... members) {
		for (Participant member : members) {
			groupParticipantRepository.save(new GroupParticipant(group, member.getId()));
		}
	}

	private void seedWeekendExperiences(Box box, Participant leo, Participant maya) {
		saveExperience(box, maya,
				"Café da manhã num lugar novo do bairro, sem celular na mesa",
				"Quero presença simples",
				1, 1, 2, 2, ExperienceType.CONNECTION);
		saveExperience(box, leo,
				"Trilha curta + piquenique improvisado",
				null,
				2, 3, 2, 3, ExperienceType.EXPLORE);
		saveExperience(box, maya,
				"Noite de jogos de tabuleiro em casa, só nós",
				null,
				2, 1, 1, 2, ExperienceType.CONNECTION);
		saveExperience(box, leo,
				"Jantar sem combinar o restaurante — um escolhe no caminho",
				"Gosto da surpresa controlada",
				3, 2, 4, 3, ExperienceType.RANDOMNESS);
		saveExperience(box, maya,
				"Sessão de fotos um do outro na cidade, 10 poses aleatórias",
				null,
				3, 2, 3, 4, ExperienceType.CREATIVITY);
		saveExperience(box, leo,
				"Karaoke em bar desconhecido (uma música cada)",
				"Fora da zona de conforto, mas juntos",
				4, 3, 4, 4, ExperienceType.EXPOSURE);
		saveExperience(box, maya,
				"Cozinhar um prato que nunca fizemos, só com ingredientes sorteados no mercado",
				null,
				4, 4, 3, 4, ExperienceType.CONSTRAINTS);
		saveExperience(box, leo,
				"Passar a tarde \"sem plano\": sair andando e só aceitar o que aparecer",
				"Quero ver se a gente se diverte no improvável",
				5, 2, 5, 5, ExperienceType.EXPLORE);
	}

	private void seedRoutineExperiences(Box box, Participant leo, Participant maya) {
		saveExperience(box, maya,
				"Escrever cartas um para o outro e ler em voz alta",
				null,
				3, 2, 2, 4, ExperienceType.CONNECTION);
		saveExperience(box, leo,
				"Trocar playlists \"segredos\" e ouvir de olhos vendados",
				null,
				3, 1, 3, 4, ExperienceType.CONTEMPLATION);
		saveExperience(box, maya,
				"Visitar um museu ou exposição sem ler as placas primeiro",
				null,
				2, 2, 3, 3, ExperienceType.EXPLORE);
		saveExperience(box, leo,
				"Fazer algo que um sempre quis e o outro nunca topou (combinar antes)",
				null,
				4, 3, 4, 4, ExperienceType.OVERCOMING);
	}

	private void seedTripExperiences(Box box, Participant leo, Participant maya, Participant nico) {
		saveExperience(box, nico,
				"Hostel barato + um dia só de mapa aberto, sem itinerário",
				"Quero ver o lugar sem guia",
				2, 2, 3, 3, ExperienceType.EXPLORE);
		saveExperience(box, maya,
				"Pôr do sol num mirante que nenhum de nós conhece",
				null,
				2, 2, 2, 3, ExperienceType.CONNECTION);
		saveExperience(box, leo,
				"Um jantar \"local demais\": pedir o que o garçom recomendaria para a própria família",
				null,
				3, 2, 4, 3, ExperienceType.RANDOMNESS);
		saveExperience(box, nico,
				"Aluguel de bikes e seguir a rota mais longa no mapa",
				null,
				3, 4, 3, 3, ExperienceType.EXPLORE);
		saveExperience(box, maya,
				"Trocar de câmera/celular um com o outro por meio dia e só fotografar o outro",
				"Dinâmica boa a três",
				4, 2, 3, 4, ExperienceType.CREATIVITY);
		saveExperience(box, leo,
				"Pegar um transporte regional sem destino fixo e descer onde parecer legal",
				"Só se os três toparem o risco",
				5, 3, 5, 5, ExperienceType.EXPLORE);
	}

	private void saveExperience(
			Box box,
			Participant author,
			String description,
			String reflection,
			int intensity,
			int effort,
			int unpredictability,
			int novelty,
			ExperienceType type) {
		String seal = sealService.computeFromDescription(description);
		experienceRepository.save(new Experience(
				box,
				author,
				description,
				reflection,
				intensity,
				effort,
				unpredictability,
				novelty,
				type,
				seal));
	}

	public record SeedSnapshot(
			long participantCount,
			long coupleBoxCount,
			long trioBoxCount,
			long weekendExperienceCount,
			long tripExperienceCount,
			boolean invitePresent) {
	}

	@Transactional(readOnly = true)
	public SeedSnapshot snapshot() {
		Participant leo = participantRepository.findByEmailIgnoreCase(LEO_EMAIL).orElseThrow();
		List<UUID> leoGroupIds = groupParticipantRepository.findGroupIdsByParticipantId(leo.getId());

		Group couple = leoGroupIds.stream()
				.map(id -> groupRepository.findById(id).orElseThrow())
				.filter(g -> COUPLE_GROUP_NAME.equals(g.getName()))
				.findFirst()
				.orElseThrow();
		Group trio = leoGroupIds.stream()
				.map(id -> groupRepository.findById(id).orElseThrow())
				.filter(g -> TRIO_GROUP_NAME.equals(g.getName()))
				.findFirst()
				.orElseThrow();

		Box weekend = boxRepository.findAllByGroup_IdOrderByCreatedAtDesc(couple.getId()).stream()
				.filter(b -> "Fim de semana".equals(b.getName()))
				.findFirst()
				.orElseThrow();
		Box trip = boxRepository.findAllByGroup_IdOrderByCreatedAtDesc(trio.getId()).stream()
				.filter(b -> "Próxima viagem".equals(b.getName()))
				.findFirst()
				.orElseThrow();

		return new SeedSnapshot(
				participantRepository.count(),
				boxRepository.findAllByGroup_IdOrderByCreatedAtDesc(couple.getId()).size(),
				boxRepository.findAllByGroup_IdOrderByCreatedAtDesc(trio.getId()).size(),
				experienceRepository.countByBox_Id(weekend.getId()),
				experienceRepository.countByBox_Id(trip.getId()),
				inviteRepository.existsByCode(DEMO_INVITE_CODE));
	}
}
