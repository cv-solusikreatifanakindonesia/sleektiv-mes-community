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
package com.sleektiv.mes.workPlans.validators;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.workPlans.WorkPlansService;
import com.sleektiv.mes.workPlans.constants.ParameterFieldsWP;
import com.sleektiv.mes.workPlans.constants.ParameterOrderColumnFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParameterOrderColumnValidators {

    @Autowired
    private WorkPlansService workPlansService;

    public boolean validatesWith(final DataDefinition parameterOrderColumnDD, final Entity parameterOrderColumn) {
        return checkIfColumnForOrdersIsNotAlreadyUsed(parameterOrderColumnDD, parameterOrderColumn);
    }

    private boolean checkIfColumnForOrdersIsNotAlreadyUsed(final DataDefinition parameterOrderColumnDD,
            final Entity parameterOrderColumn) {
        return workPlansService.checkIfColumnIsNotUsed(parameterOrderColumnDD, parameterOrderColumn,
                BasicConstants.MODEL_PARAMETER, ParameterOrderColumnFields.COLUMN_FOR_ORDERS,
                ParameterFieldsWP.PARAMETER_ORDER_COLUMNS);
    }

}
