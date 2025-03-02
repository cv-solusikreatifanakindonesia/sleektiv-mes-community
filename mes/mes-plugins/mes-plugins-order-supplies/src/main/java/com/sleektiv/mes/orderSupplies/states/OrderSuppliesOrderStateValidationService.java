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
package com.sleektiv.mes.orderSupplies.states;

import com.sleektiv.mes.materialRequirements.constants.OrderFieldsMR;
import com.sleektiv.mes.orders.constants.InputProductsRequiredForType;
import com.sleektiv.mes.productionCounting.constants.OrderFieldsPC;
import com.sleektiv.mes.productionCounting.constants.TypeOfProductionRecording;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.model.api.Entity;

import org.springframework.stereotype.Service;

@Service
public class OrderSuppliesOrderStateValidationService {

    public void validationOnAccepted(final StateChangeContext stateChangeContext) {
        final Entity order = stateChangeContext.getOwner();

        String inputProductsRequiredForType = order.getStringField(OrderFieldsMR.INPUT_PRODUCTS_REQUIRED_FOR_TYPE);
        String typeOfProductionRecording = order.getStringField(OrderFieldsPC.TYPE_OF_PRODUCTION_RECORDING);

        if (InputProductsRequiredForType.START_OPERATIONAL_TASK.getStringValue().equals(inputProductsRequiredForType)
                && !TypeOfProductionRecording.FOR_EACH.getStringValue().equals(typeOfProductionRecording)) {
            stateChangeContext.addValidationError("orders.order.typeOfProductionRecording.error.typeIsntForEach");
        }
    }

}
