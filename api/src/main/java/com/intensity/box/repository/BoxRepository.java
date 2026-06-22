package com.intensity.box.repository;

import com.intensity.box.entity.Box;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoxRepository extends JpaRepository<Box, UUID> {

	List<Box> findAllByGroup_IdOrderByCreatedAtDesc(UUID groupId);
}
