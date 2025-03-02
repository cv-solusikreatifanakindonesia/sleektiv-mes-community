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
package com.sleektiv.mes.supplyNegotiations.states.constants;

import static com.sleektiv.mes.supplyNegotiations.constants.SupplyNegotiationsConstants.MODEL_NEGOTIATION;
import static com.sleektiv.mes.supplyNegotiations.constants.SupplyNegotiationsConstants.MODEL_NEGOTIATION_STATE_CHANGE;
import static com.sleektiv.mes.supplyNegotiations.constants.SupplyNegotiationsConstants.PLUGIN_IDENTIFIER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.states.AbstractStateChangeDescriber;
import com.sleektiv.mes.states.StateEnum;
import com.sleektiv.mes.supplyNegotiations.constants.NegotiationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;

@Service
public final class NegotiationStateChangeDescriber extends AbstractStateChangeDescriber {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public DataDefinition getDataDefinition() {
        return dataDefinitionService.get(PLUGIN_IDENTIFIER, MODEL_NEGOTIATION_STATE_CHANGE);
    }

    @Override
    public String getOwnerFieldName() {
        return NegotiationStateChangeFields.MODEL_NEGOTIATION;
    }

    @Override
    public StateEnum parseStateEnum(final String stringValue) {
        return NegotiationState.parseString(stringValue);
    }

    @Override
    public DataDefinition getOwnerDataDefinition() {
        return dataDefinitionService.get(PLUGIN_IDENTIFIER, MODEL_NEGOTIATION);
    }

    @Override
    public String getOwnerStateChangesFieldName() {
        return NegotiationFields.STATE_CHANGES;
    };

}
