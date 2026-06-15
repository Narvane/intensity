package com.intensity.experiencia.repository;

import com.intensity.experiencia.entity.Experiencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExperienciaRepository extends JpaRepository<Experiencia, UUID> {

	List<Experiencia> findAllByCaixinha_IdOrderByCreatedAtDesc(UUID boxId);

	long countByCaixinha_Id(UUID boxId);
}
