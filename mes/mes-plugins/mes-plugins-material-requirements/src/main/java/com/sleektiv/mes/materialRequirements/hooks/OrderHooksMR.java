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
package com.sleektiv.mes.materialRequirements.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.materialRequirements.MaterialRequirementService;
import com.sleektiv.mes.materialRequirements.constants.OrderFieldsMR;
import com.sleektiv.mes.materialRequirements.constants.ParameterFieldsMR;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class OrderHooksMR {

    @Autowired
    private MaterialRequirementService materialRequirementService;

    @Autowired
    private ParameterService parameterService;

    public void onCreate(final DataDefinition orderDD, final Entity order) {
        setInputProductsRequiredForTypeDefaultValue(order);
    }

    public void onCopy(final DataDefinition orderDD, final Entity order) {
        setInputProductsRequiredForTypeDefaultValue(order);
    }

    private void setInputProductsRequiredForTypeDefaultValue(final Entity order) {
        materialRequirementService.setInputProductsRequiredForTypeDefaultValue(order,
                OrderFieldsMR.INPUT_PRODUCTS_REQUIRED_FOR_TYPE, getInputProductsRequiredForType());
    }

    private String getInputProductsRequiredForType() {
        return parameterService.getParameter().getStringField(ParameterFieldsMR.INPUT_PRODUCTS_REQUIRED_FOR_TYPE);
    }

}
