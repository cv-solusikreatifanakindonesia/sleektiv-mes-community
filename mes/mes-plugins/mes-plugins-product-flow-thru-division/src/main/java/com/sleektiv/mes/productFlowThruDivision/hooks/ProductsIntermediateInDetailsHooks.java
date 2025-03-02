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
package com.sleektiv.mes.productFlowThruDivision.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.productFlowThruDivision.constants.ProductionFlowComponent;
import com.sleektiv.mes.productFlowThruDivision.constants.TechnologyFieldsPFTD;
import com.sleektiv.mes.productionCounting.constants.TechnologyFieldsPC;
import com.sleektiv.mes.productionCounting.constants.TypeOfProductionRecording;
import com.sleektiv.mes.technologies.constants.OperationProductOutComponentFields;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ProductsIntermediateInDetailsHooks {



    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity opic = form.getPersistedEntityWithIncludedFormValues();

        LookupComponent productsFlowLocationLookup = (LookupComponent) view
                .getComponentByReference(TechnologyFieldsPFTD.PRODUCTS_FLOW_LOCATION);
        productsFlowLocationLookup.setEnabled(
                ProductionFlowComponent.WAREHOUSE.getStringValue().equals(opic.getField(TechnologyFieldsPFTD.PRODUCTION_FLOW)));
        String typeOfProductionRecording = opic.getBelongsToField(OperationProductOutComponentFields.OPERATION_COMPONENT)
                .getBelongsToField(TechnologyOperationComponentFields.TECHNOLOGY)
                .getStringField(TechnologyFieldsPC.TYPE_OF_PRODUCTION_RECORDING);
        FieldComponent productionFlowFieldComponent = (FieldComponent) view
                .getComponentByReference(TechnologyFieldsPFTD.PRODUCTION_FLOW);
        if (TypeOfProductionRecording.CUMULATED.getStringValue().equals(typeOfProductionRecording)) {
            productionFlowFieldComponent.setFieldValue(ProductionFlowComponent.WITHIN_THE_PROCESS.getStringValue());
            productionFlowFieldComponent.setEnabled(false);
            productsFlowLocationLookup.setEnabled(false);
        }
    }

    public void onProductionFlowComponentChange(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        LookupComponent productsFlowLocationLookup = (LookupComponent) view
                .getComponentByReference(TechnologyFieldsPFTD.PRODUCTS_FLOW_LOCATION);
        productsFlowLocationLookup.setFieldValue(null);
    }
}
