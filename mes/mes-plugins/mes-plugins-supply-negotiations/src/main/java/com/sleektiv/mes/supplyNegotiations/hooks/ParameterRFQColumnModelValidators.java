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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.supplyNegotiations.constants.SupplyNegotiationsConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;

@Service
public class ParameterRFQColumnModelValidators {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public final boolean checkIfColumnForRFQIsNotAlreadyUsed(final DataDefinition ordersColumnDD, final Entity ordersColumn) {
        return checkIfColumnIsNotUsed(ordersColumnDD, ordersColumn, "parameter", "columnForRequests",
                "parameterDeliveryOrderColumn");

    }

    public boolean checkIfColumnIsNotUsed(final DataDefinition componentDD, final Entity component, final String modelName,
            final String columnName, final String componentName) {

        if (component.getId() == null) {
            Entity column = component.getBelongsToField(columnName);

            if (column == null) {
                return true;
            } else {
                Entity model = component.getBelongsToField(modelName);

                if (model == null) {
                    return true;
                } else {
                    for (Entity columnUsed : dataDefinitionService
                            .get(SupplyNegotiationsConstants.PLUGIN_IDENTIFIER,
                                    SupplyNegotiationsConstants.MODEL_PARAMETER_COLUMN_FOR_REQUESTS).find().list().getEntities()) {

                        if (columnUsed.getBelongsToField("columnForRequests").getId().equals(column.getId())) {
                            component.addError(componentDD.getField(columnName),
                                    "supplyNegotiations.column.message.columnIsAlreadyUsed");

                            return false;
                        }
                    }

                }
            }
        }

        return true;
    }
}
