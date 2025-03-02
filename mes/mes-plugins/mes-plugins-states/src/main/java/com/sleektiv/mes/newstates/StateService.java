package com.sleektiv.mes.newstates;

import com.sleektiv.mes.states.StateChangeEntityDescriber;
import com.sleektiv.model.api.Entity;

public interface StateService {

    public Entity onValidate(Entity entity, String sourceState, String targetState, Entity stateChangeEntity, StateChangeEntityDescriber describer);

    public Entity onBeforeSave(Entity entity, String sourceState, String targetState, Entity stateChangeEntity, StateChangeEntityDescriber describer);

    public Entity onAfterSave(Entity entity, String sourceState, String targetState, Entity stateChangeEntity, StateChangeEntityDescriber describer);

    public StateChangeEntityDescriber getChangeEntityDescriber();

}
