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

import com.sleektiv.mes.materialRequirements.MaterialRequirementService;
import com.sleektiv.mes.materialRequirements.constants.InputProductsRequiredForType;
import com.sleektiv.mes.materialRequirements.constants.ParameterFieldsMR;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class ParameterHooksMR {

    @Autowired
    private MaterialRequirementService materialRequirementService;

    public void onCreate(final DataDefinition parameterDD, final Entity parameter) {
        setInputProductsRequiredForTypeDefaultValue(parameterDD, parameter);
    }

    public void setInputProductsRequiredForTypeDefaultValue(final DataDefinition parameterDD, final Entity parameter) {
        materialRequirementService.setInputProductsRequiredForTypeDefaultValue(parameter,
                ParameterFieldsMR.INPUT_PRODUCTS_REQUIRED_FOR_TYPE, InputProductsRequiredForType.START_ORDER.getStringValue());
    }

}
