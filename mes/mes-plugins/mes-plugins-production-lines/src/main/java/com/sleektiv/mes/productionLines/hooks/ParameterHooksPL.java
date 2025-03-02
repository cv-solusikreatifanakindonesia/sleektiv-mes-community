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
package com.sleektiv.mes.productionLines.hooks;

import static com.sleektiv.mes.productionLines.constants.ProductionLinesConstants.PLUGIN_IDENTIFIER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionLines.constants.ParameterFieldsPL;
import com.sleektiv.mes.productionLines.constants.ProductionLinesConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;

@Service
public class ParameterHooksPL {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void addFieldsForParameter(final DataDefinition dataDefinition, final Entity parameter) {
        Entity productionLine = dataDefinitionService.get(PLUGIN_IDENTIFIER, ProductionLinesConstants.MODEL_PRODUCTION_LINE)
                .find().setMaxResults(1).uniqueResult();

        parameter.setField(ParameterFieldsPL.DEFAULT_PRODUCTION_LINE, productionLine);
    }

}
