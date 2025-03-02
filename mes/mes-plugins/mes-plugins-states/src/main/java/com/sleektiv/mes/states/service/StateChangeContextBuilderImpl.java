/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.states.service;

import static com.sleektiv.mes.states.constants.StateChangeStatus.IN_PROGRESS;
import static com.sleektiv.mes.states.constants.StateChangeStatus.PAUSED;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.StateChangeContextImpl;
import com.sleektiv.mes.states.StateChangeEntityDescriber;
import com.sleektiv.mes.states.StateEnum;
import com.sleektiv.mes.states.exception.AnotherChangeInProgressException;
import com.sleektiv.mes.states.exception.StateTransitionNotAlloweException;
import com.sleektiv.mes.states.messages.MessageService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public final class StateChangeContextBuilderImpl implements StateChangeContextBuilder {

    @Autowired
    private MessageService messageService;

    @Autowired
    private StateChangeEntityBuilder stateChangeEntityBuilder;

    @Override
    @Transactional
    public StateChangeContext build(final StateChangeEntityDescriber describer, final Entity owner, final String targetStateString) {
        final Entity persistedOwner = owner.getDataDefinition().save(owner);
        final DataDefinition stateChangeDataDefinition = describer.getDataDefinition();
        final StateEnum sourceState = describer.parseStateEnum(owner.getStringField(describer.getOwnerStateFieldName()));
        final StateEnum targetState = describer.parseStateEnum(targetStateString);
        if (sourceState != null && !sourceState.canChangeTo(targetState)) {
            throw new StateTransitionNotAlloweException(sourceState, targetState);
        }
        final Entity stateChangeEntity = stateChangeEntityBuilder.build(describer, persistedOwner, targetState);

        checkForUnfinishedStateChange(describer, persistedOwner);
        return new StateChangeContextImpl(stateChangeDataDefinition.save(stateChangeEntity), describer, messageService);
    }

    @Override
    @Transactional
    public StateChangeContext build(final StateChangeEntityDescriber describer, final Entity stateChangeEntity) {
        return new StateChangeContextImpl(stateChangeEntity, describer, messageService);
    }

    protected void onCreate(final StateChangeEntityDescriber describer, final Entity stateChangeEntity, final Entity owner,
            final StateEnum sourceState, final StateEnum targetState) {

    }

    /**
     * Checks if given owner entity have not any unfinished state change request.
     * 
     * @param owner
     *            state change's owner entity
     * @throws AnotherChangeInProgressException
     *             if at least one unfinished state change request for given owner entity is found.
     */
    protected void checkForUnfinishedStateChange(final StateChangeEntityDescriber describer, final Entity owner) {
        final String ownerFieldName = describer.getOwnerFieldName();
        final String statusFieldName = describer.getStatusFieldName();
        final Set<String> unfinishedStatuses = Sets.newHashSet(IN_PROGRESS.getStringValue(), PAUSED.getStringValue());

        final SearchCriteriaBuilder searchCriteria = describer.getDataDefinition().find();
        searchCriteria.createAlias(ownerFieldName, ownerFieldName);
        searchCriteria.add(SearchRestrictions.eq(ownerFieldName + ".id", owner.getId()));
        searchCriteria.add(SearchRestrictions.in(statusFieldName, unfinishedStatuses));
        if (searchCriteria.list().getTotalNumberOfEntities() > 0) {
            throw new AnotherChangeInProgressException();
        }
    }
}
