package com.nexus.collaboration.repository;

import com.nexus.collaboration.entity.CaseTask;
import com.nexus.collaboration.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseTaskRepository extends JpaRepository<CaseTask, UUID> {
    List<CaseTask> findByCaseEntityIdOrderByCreatedAtAsc(UUID caseId);
    
    List<CaseTask> findByAssigneeIdAndStatusIn(UUID assigneeId, List<TaskStatus> statuses);
    
    List<CaseTask> findByCaseEntityAssignedTeamIdAndStatusIn(UUID teamId, List<TaskStatus> statuses);
    
    long countByAssigneeIdAndStatusIn(UUID assigneeId, List<TaskStatus> statuses);
    
    long countByCaseEntityAssignedTeamIdAndStatusIn(UUID teamId, List<TaskStatus> statuses);
    
    long countByCaseEntityAssignedTeamIdAndAssigneeIsNullAndStatusIn(UUID teamId, List<TaskStatus> statuses);
}
