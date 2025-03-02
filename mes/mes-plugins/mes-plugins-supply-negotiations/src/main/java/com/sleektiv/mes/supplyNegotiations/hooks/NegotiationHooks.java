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

import com.sleektiv.mes.states.service.StateChangeEntityBuilder;
import com.sleektiv.mes.supplyNegotiations.constants.NegotiationFields;
import com.sleektiv.mes.supplyNegotiations.constants.NegotiationProductFields;
import com.sleektiv.mes.supplyNegotiations.states.constants.NegotiationState;
import com.sleektiv.mes.supplyNegotiations.states.constants.NegotiationStateChangeDescriber;
import com.sleektiv.mes.supplyNegotiations.states.constants.NegotiationStateStringValues;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchRestrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;


@Service
public class NegotiationHooks {

    @Autowired
    private StateChangeEntityBuilder stateChangeEntityBuilder;

    @Autowired
    private NegotiationStateChangeDescriber describer;

    public void setInitialState(final DataDefinition negotiationDD, final Entity negotiation) {
        stateChangeEntityBuilder.buildInitial(describer, negotiation, NegotiationState.DRAFT);
    }

    public void clearStateFieldOnCopy(final DataDefinition negotiationDD, final Entity negotiation) {
        negotiation.setField(NegotiationFields.STATE, NegotiationStateStringValues.DRAFT);
    }

    public void updateFarthestLimitDate(final DataDefinition negotiationDD, final Entity negotiation) {
        if (Objects.nonNull(negotiation.getId())) {
            Entity negotiationProduct = negotiation.getHasManyField(NegotiationFields.NEGOTIATION_PRODUCTS).find()
                    .add(SearchRestrictions.belongsTo(NegotiationProductFields.NEGOTIATION, negotiation)).addOrder(SearchOrders.desc(NegotiationProductFields.DUE_DATE))
                    .setMaxResults(1).uniqueResult();

            if (Objects.nonNull(negotiationProduct)) {
                Date farestLimitDate = (Date) negotiationProduct.getField(NegotiationProductFields.DUE_DATE);

                negotiation.setField(NegotiationFields.FARTHEST_LIMIT_DATE, farestLimitDate);
            }
        }
    }

}
