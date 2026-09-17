package com.nexus.problem.dto;

import com.nexus.problem.entity.ProblemStatus;

public class UpdateProblemRequest {

    private String title;
    private String suspectedRootCause;
    private String confirmedRootCause;
    private String investigationNotes;
    private String correctiveAction;
    private String preventiveAction;
    private ProblemStatus status;

    public UpdateProblemRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuspectedRootCause() {
        return suspectedRootCause;
    }

    public void setSuspectedRootCause(String suspectedRootCause) {
        this.suspectedRootCause = suspectedRootCause;
    }

    public String getConfirmedRootCause() {
        return confirmedRootCause;
    }

    public void setConfirmedRootCause(String confirmedRootCause) {
        this.confirmedRootCause = confirmedRootCause;
    }

    public String getInvestigationNotes() {
        return investigationNotes;
    }

    public void setInvestigationNotes(String investigationNotes) {
        this.investigationNotes = investigationNotes;
    }

    public String getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    public String getPreventiveAction() {
        return preventiveAction;
    }

    public void setPreventiveAction(String preventiveAction) {
        this.preventiveAction = preventiveAction;
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }
}
