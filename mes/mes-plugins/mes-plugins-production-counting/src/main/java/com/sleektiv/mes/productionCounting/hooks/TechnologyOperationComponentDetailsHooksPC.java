/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.productionCounting.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionCounting.ProductionCountingService;
import com.sleektiv.mes.productionCounting.constants.TechnologyFieldsPC;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.mes.timeNormsForOperations.constants.TechnologyOperationComponentFieldsTNFO;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologyOperationComponentDetailsHooksPC {

    @Autowired
    private ProductionCountingService productionCountingService;

    public void updateFieldsStateOnWindowLoad(final ViewDefinitionState viewDefinitionState) {
        FormComponent form = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent pieceworkProduction = (FieldComponent) viewDefinitionState.getComponentByReference(TechnologyOperationComponentFieldsTNFO.PIECEWORK_PRODUCTION);

        pieceworkProduction.setEnabled(productionCountingService.isTypeOfProductionRecordingForEach(form.getPersistedEntityWithIncludedFormValues()
                .getBelongsToField(TechnologyOperationComponentFields.TECHNOLOGY)
                .getStringField(TechnologyFieldsPC.TYPE_OF_PRODUCTION_RECORDING)));
    }
}
