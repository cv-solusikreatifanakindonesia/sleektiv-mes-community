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
package com.sleektiv.mes.assignmentToShift.states;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.assignmentToShift.constants.AssignmentToShiftConstants;
import com.sleektiv.mes.assignmentToShift.states.aop.AssignmentToShiftStateChangeAspect;
import com.sleektiv.mes.assignmentToShift.states.constants.AssignmentToShiftState;
import com.sleektiv.mes.assignmentToShift.states.constants.AssignmentToShiftStateChangeDescriber;
import com.sleektiv.mes.assignmentToShift.states.constants.AssignmentToShiftStateChangeFields;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.aop.AbstractStateChangeAspect;
import com.sleektiv.mes.states.constants.StateChangeStatus;
import com.sleektiv.mes.states.service.StateChangeContextBuilder;
import com.sleektiv.mes.states.service.StateChangeEntityBuilder;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchCriterion;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class AssignmentToShiftStatesHelper {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private StateChangeEntityBuilder stateChangeEntityBuilder;

    @Autowired
    private StateChangeContextBuilder stateChangeContextBuilder;

    @Autowired
    private AssignmentToShiftStateChangeDescriber assignmentToShiftStateChangeDescriber;

    @Autowired
    private AssignmentToShiftStateChangeAspect assignmentToShiftStateChangeAspect;

    public AbstractStateChangeAspect getStateChangeService() {
        return assignmentToShiftStateChangeAspect;
    }

    public void setInitialState(final Entity assignmentToShift) {
        stateChangeEntityBuilder.buildInitial(assignmentToShiftStateChangeDescriber, assignmentToShift,
                AssignmentToShiftState.DRAFT);
    }

    public void changeState(final Entity assignmentToShift, final AssignmentToShiftState state) {
        StateChangeContext context = stateChangeContextBuilder.build(assignmentToShiftStateChangeDescriber, assignmentToShift,
                state.getStringValue());

        assignmentToShiftStateChangeAspect.changeState(context);
    }

    public StateChangeContext findPausedStateTransition(final Entity assignmentToShift) {
        Entity stateChangeEntity = findPausedStateChangeEntity(assignmentToShift);

        if (stateChangeEntity == null) {
            return null;
        }

        return stateChangeContextBuilder.build(assignmentToShiftStateChangeDescriber, stateChangeEntity);
    }

    private Entity findPausedStateChangeEntity(final Entity assignmentToShift) {
        DataDefinition stateChangeDD = dataDefinitionService.get(AssignmentToShiftConstants.PLUGIN_IDENTIFIER,
                AssignmentToShiftConstants.MODEL_ASSIGNMENT_TO_SHIFT_STATE_CHANGE);

        SearchCriteriaBuilder scb = stateChangeDD.find();

        for (SearchCriterion criterion : getCriteriaToFindWaitingStateChange(assignmentToShift)) {
            scb.add(criterion);
        }

        return scb.setMaxResults(1).uniqueResult();
    }

    public Iterable<SearchCriterion> getCriteriaToFindWaitingStateChange(final Entity assignmentToShift) {
        List<SearchCriterion> criterions = Lists.newArrayList();

        criterions.add(SearchRestrictions.belongsTo(AssignmentToShiftStateChangeFields.ASSIGNMENT_TO_SHIFT, assignmentToShift));
        criterions.add(SearchRestrictions.eq(AssignmentToShiftStateChangeFields.STATUS,
                StateChangeStatus.PAUSED.getStringValue()));

        return criterions;
    }

}
