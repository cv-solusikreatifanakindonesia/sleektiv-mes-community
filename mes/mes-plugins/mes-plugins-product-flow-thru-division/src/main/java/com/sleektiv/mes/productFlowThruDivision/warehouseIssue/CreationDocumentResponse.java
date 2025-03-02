package com.sleektiv.mes.productFlowThruDivision.warehouseIssue;

import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.validators.ErrorMessage;

import java.util.List;

public class CreationDocumentResponse {

    private List<ErrorMessage> errors;

    private boolean valid;

    private Entity document;

    public CreationDocumentResponse(boolean valid) {
        this.valid = valid;
    }

    public CreationDocumentResponse(boolean valid, List<ErrorMessage> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public List<ErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorMessage> errors) {
        this.errors = errors;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Entity getDocument() {
        return document;
    }

    public void setDocument(Entity document) {
        this.document = document;
    }

}
