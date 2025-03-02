/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
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
package com.sleektiv.mes.supplyNegotiations.hooks;

import static com.sleektiv.mes.supplyNegotiations.constants.RequestForQuotationFields.STATE;
import static com.sleektiv.mes.supplyNegotiations.states.constants.RequestForQuotationState.DRAFT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.states.service.StateChangeEntityBuilder;
import com.sleektiv.mes.supplyNegotiations.states.constants.RequestForQuotationStateChangeDescriber;
import com.sleektiv.mes.supplyNegotiations.states.constants.RequestForQuotationStateStringValues;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class RequestForQuotationHooks {

    @Autowired
    private StateChangeEntityBuilder stateChangeEntityBuilder;

    @Autowired
    private RequestForQuotationStateChangeDescriber describer;

    public void setInitialState(final DataDefinition assignmentToShiftDD, final Entity assignmentToShift) {
        stateChangeEntityBuilder.buildInitial(describer, assignmentToShift, DRAFT);
    }

    public void clearStateFieldOnCopy(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField(STATE, RequestForQuotationStateStringValues.DRAFT);
    }

}
