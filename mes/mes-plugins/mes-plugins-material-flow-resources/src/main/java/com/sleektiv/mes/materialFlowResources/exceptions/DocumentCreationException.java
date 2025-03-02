package com.sleektiv.mes.materialFlowResources.exceptions;

import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.exception.EntityRuntimeException;

public class DocumentCreationException extends EntityRuntimeException {

    public DocumentCreationException(Entity entity) {
        super(entity);
    }
}
