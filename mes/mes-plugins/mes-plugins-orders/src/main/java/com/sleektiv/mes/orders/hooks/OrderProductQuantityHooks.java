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
package com.sleektiv.mes.orders.hooks;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.constants.ParameterFieldsO;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.sleektiv.mes.orders.constants.OrderFields.COMMENT_REASON_TYPE_DEVIATIONS_QUANTITY;
import static com.sleektiv.mes.orders.constants.OrderFields.COMMISSIONED_CORRECTED_QUANTITY;
import static com.sleektiv.mes.orders.constants.OrderFields.PLANNED_QUANTITY_FOR_ADDITIONAL_UNIT;
import static com.sleektiv.mes.orders.constants.OrderFields.PLANNED_QUANTITY;
import static com.sleektiv.mes.orders.constants.OrderFields.PRODUCT;
import static com.sleektiv.mes.orders.constants.OrderFields.STATE;
import static com.sleektiv.mes.orders.constants.OrderFields.TYPE_OF_CORRECTION_CAUSES;

@Service
public class OrderProductQuantityHooks {

    public static final String L_TYPE_OF_PRODUCTION_RECORDING = "typeOfProductionRecording";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ParameterService parameterService;

    public void changeFieldsEnabledForSpecificOrderState(final ViewDefinitionState view) {
        final FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form.getEntityId() == null) {
            List<String> references = Arrays.asList(COMMISSIONED_CORRECTED_QUANTITY, TYPE_OF_CORRECTION_CAUSES,
                    COMMENT_REASON_TYPE_DEVIATIONS_QUANTITY);
            changedEnabledFields(view, references, false);
            return;
        }
        final Entity order = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER)
                .get(form.getEntityId());

        if (allowQuantityChangeInAcceptedOrder() && (order.getStringField(STATE).equals(OrderState.ACCEPTED.getStringValue())
                || order.getStringField(STATE).equals(OrderState.IN_PROGRESS.getStringValue())
                || order.getStringField(STATE).equals(OrderState.INTERRUPTED.getStringValue())
                || order.getStringField(STATE).equals(OrderState.PENDING.getStringValue()))) {
            List<String> references = Arrays.asList(PLANNED_QUANTITY, PLANNED_QUANTITY_FOR_ADDITIONAL_UNIT);
            changedEnabledFields(view, references, true);

        }
        changedEnabledFields(view, Arrays.asList(TYPE_OF_CORRECTION_CAUSES), false);
        if (order.getStringField(STATE).equals(OrderState.PENDING.getStringValue())) {
            List<String> references = Arrays.asList(COMMISSIONED_CORRECTED_QUANTITY, TYPE_OF_CORRECTION_CAUSES,
                    COMMENT_REASON_TYPE_DEVIATIONS_QUANTITY);
            changedEnabledFields(view, references, false);
        }
        if (allowQuantityChangeInAcceptedOrder() && (order.getStringField(STATE).equals(OrderState.ACCEPTED.getStringValue())
                || order.getStringField(STATE).equals(OrderState.IN_PROGRESS.getStringValue())
                || order.getStringField(STATE).equals(OrderState.INTERRUPTED.getStringValue()))) {

            List<String> references = Arrays.asList(COMMISSIONED_CORRECTED_QUANTITY, TYPE_OF_CORRECTION_CAUSES,
                    COMMENT_REASON_TYPE_DEVIATIONS_QUANTITY);
            changedEnabledFields(view, references, true);
        }
    }

    private void changedEnabledFields(final ViewDefinitionState view, final List<String> references, final boolean enabled) {
        for (String reference : references) {
            FieldComponent field = (FieldComponent) view.getComponentByReference(reference);
            if (field == null) {
                continue;
            }
            field.setEnabled(enabled);
            field.requestComponentUpdateState();
        }
    }

    public boolean allowQuantityChangeInAcceptedOrder() {
        return parameterService.getParameter().getBooleanField(ParameterFieldsO.ALLOW_QUANTITY_CHANGE_IN_ACCEPTED_ORDER);
    }

    public void fillProductUnit(final ViewDefinitionState state) {
        List<String> references = Arrays.asList("unitCCQ", "unitCPQ", "unitAOPP", "unitRAOPTP", "wastesQuantityUnit", "unitReportedProductionQuantity");
        fillProductUnit(state, references);
    }

    public void fillProductUnit(final ViewDefinitionState state, final List<String> references) {
        FieldComponent productState = (FieldComponent) state.getComponentByReference(PRODUCT);
        for (String reference : references) {
            FieldComponent unitState = (FieldComponent) state.getComponentByReference(reference);
            unitState.requestComponentUpdateState();
            if (productState.getFieldValue() == null) {
                unitState.setFieldValue("");
            } else {
                Entity product = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT)
                        .get((Long) productState.getFieldValue());
                unitState.setFieldValue(product.getStringField("unit"));
            }
        }
    }
}
