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
package com.sleektiv.mes.technologies.tree.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.tree.builder.api.InternalOperationProductComponent;
import com.sleektiv.mes.technologies.tree.builder.api.InternalTechnologyOperationComponent;
import com.sleektiv.mes.technologies.tree.builder.api.OperationProductComponent;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;

@Component
public class TechnologyTreeComponentsFactoryImpl implements TechnologyTreeComponentsFactory {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public InternalTechnologyOperationComponent buildToc() {
        return new TechnologyOperationCompImpl(getTocDataDef());
    }

    @Override
    public InternalOperationProductComponent buildOpc(final OperationProductComponent.OperationCompType type) {
        return new OperationProductComponentImpl(type, getOpcDataDef(type));
    }

    private DataDefinition getOpcDataDef(final OperationProductComponent.OperationCompType type) {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, type.getModelName());
    }

    private DataDefinition getTocDataDef() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODEL_TECHNOLOGY_OPERATION_COMPONENT);
    }

}
