package com.sleektiv.mes.materialFlowResources.exceptions;

import java.util.List;
import java.util.Objects;

import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.exception.EntityRuntimeException;

public final class DocumentBuildException extends EntityRuntimeException {

    private final List<Entity> invalidPositions;

    public DocumentBuildException(Entity entity, List<Entity> invalidPositions) {
        super(entity);
        this.invalidPositions = Objects.requireNonNull(invalidPositions);
    }

    public List<Entity> getInvalidPositions() {
        return invalidPositions;
    }

}
