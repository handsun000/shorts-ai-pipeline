package com.workfluffs.shortsai.domain.asset.repository;

import com.workfluffs.shortsai.domain.asset.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByProjectId(Long projectId);
}
