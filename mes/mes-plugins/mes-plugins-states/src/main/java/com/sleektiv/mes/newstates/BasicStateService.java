package com.sleektiv.mes.newstates;

import com.sleektiv.mes.states.StateChangeEntityDescriber;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

@Service
public abstract class BasicStateService implements StateService {

    @Override
    public Entity onValidate(Entity entity, String sourceState, String targetState, Entity stateChangeEntity, StateChangeEntityDescriber describer) {
        return entity;
    }

    @Override
    public Entity onBeforeSave(Entity entity, String sourceState, String targetState, Entity stateChangeEntity, StateChangeEntityDescriber describer) {
        return entity;
    }

    @Override
    public Entity onAfterSave(Entity entity, String sourceState, String targetState, Entity stateChangeEntity, StateChangeEntityDescriber describer) {
        return entity;
    }

}
