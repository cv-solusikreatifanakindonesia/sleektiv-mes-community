package com.sleektiv.mes.materialFlowResources.exceptions;

import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.exception.EntityRuntimeException;

public final class InvalidResourceException extends EntityRuntimeException {

    public InvalidResourceException(Entity entity) {
        super(entity);
    }
}
