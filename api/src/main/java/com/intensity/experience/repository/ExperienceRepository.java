package com.intensity.experience.repository;

import com.intensity.experience.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

	List<Experience> findAllByBox_IdOrderByCreatedAtDesc(UUID boxId);

	long countByBox_Id(UUID boxId);

	long countByBox_IdAndAuthor_Id(UUID boxId, UUID authorId);

	void deleteByAuthor_IdInAndBox_Group_Id(Collection<UUID> authorIds, UUID groupId);
}
