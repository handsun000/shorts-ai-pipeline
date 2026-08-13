package com.workfluffs.shortsai.domain.project.repository;

import com.workfluffs.shortsai.domain.project.entity.Project;
import com.workfluffs.shortsai.domain.project.entity.PromptHistory;
import com.workfluffs.shortsai.domain.project.enums.PromptType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptHistoryRepository extends JpaRepository<PromptHistory, Long> {
    List<PromptHistory> findByProjectIdOrderByCutOrderAscVersionDesc(Long projectId);
    Optional<PromptHistory> findFirstByProjectIdAndCutOrderAndTypeOrderByVersionDesc(Long projectId, Integer cutOrder, PromptType type);
}
