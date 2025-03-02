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
package com.sleektiv.mes.technologies.hooks;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.technologies.constants.OperationProductOutComponentFields;
import com.sleektiv.mes.technologies.constants.ParameterFieldsT;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OPOCDetailsHooks {

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private NumberService numberService;

    public void onBeforeRender(final ViewDefinitionState view) {
        setOPOCDefaultQuantityFromParameter(view);
    }

    private void setOPOCDefaultQuantityFromParameter(final ViewDefinitionState view) {
        FormComponent operationProductOutComponentForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent quantityField = (FieldComponent) view
                .getComponentByReference(OperationProductOutComponentFields.QUANTITY);
        if (operationProductOutComponentForm.getEntityId() == null && quantityField.getFieldValue() == null) {
            BigDecimal operationProductOutDefaultQuantity = parameterService.getParameter().getDecimalField(ParameterFieldsT.OPERATION_PRODUCT_OUT_DEFAULT_QUANTITY);
            if (operationProductOutDefaultQuantity != null) {
                quantityField.setFieldValue(numberService.formatWithMinimumFractionDigits(operationProductOutDefaultQuantity, 0));
            }
        }
    }

}
