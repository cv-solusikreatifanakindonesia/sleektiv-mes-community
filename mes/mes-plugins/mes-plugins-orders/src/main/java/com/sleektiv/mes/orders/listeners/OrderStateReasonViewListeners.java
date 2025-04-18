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
package com.sleektiv.mes.orders.listeners;

import static com.sleektiv.mes.orders.states.constants.OrderStateChangeFields.REASON_REQUIRED;
import static com.sleektiv.mes.orders.states.constants.OrderStateChangeFields.REASON_TYPES;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.client.OrderStateChangeViewClient;
import com.sleektiv.mes.orders.states.constants.OrderStateChangeFields;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.constants.StateChangeStatus;
import com.sleektiv.mes.states.service.StateChangeContextBuilder;
import com.sleektiv.mes.states.service.client.util.ViewContextHolder;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderStateReasonViewListeners {

    

    private static final List<String> DATE_FIELDS = Lists.newArrayList("sourceCorrectedDateFrom", "sourceCorrectedDateTo",
            "sourceStartDate", "sourceFinishDate", "targetCorrectedDateFrom", "targetCorrectedDateTo", "targetStartDate",
            "targetFinishDate");

    @Autowired
    private OrderStateChangeAspect orderStateChangeService;

    @Autowired
    private StateChangeContextBuilder stateChangeContextBuilder;

    @Autowired
    private OrderStateChangeViewClient orderStateChangeViewClient;

    public void continueStateChange(final ViewDefinitionState view, final ComponentState component, final String[] args) {
        final FormComponent form = (FormComponent) component;
        form.performEvent(view, "save");
        if (!form.isValid()) {
            return;
        }

        final Entity stateChangeEntity = ((FormComponent) form).getEntity();
        final StateChangeContext stateContext = stateChangeContextBuilder.build(
                orderStateChangeService.getChangeEntityDescriber(), stateChangeEntity);

        stateContext.setStatus(StateChangeStatus.IN_PROGRESS);
        orderStateChangeService.changeState(stateContext);

        orderStateChangeViewClient.showMessages(new ViewContextHolder(view, form), stateContext);
    }

    public void cancelStateChange(final ViewDefinitionState view, final ComponentState form, final String[] args) {
        final Entity stateChangeEntity = ((FormComponent) form).getEntity();
        stateChangeEntity.setField(REASON_REQUIRED, false);

        final StateChangeContext stateContext = stateChangeContextBuilder.build(
                orderStateChangeService.getChangeEntityDescriber(), stateChangeEntity);
        stateContext.setStatus(StateChangeStatus.CANCELED);
        stateContext.save();

        orderStateChangeViewClient.showMessages(new ViewContextHolder(view, form), stateContext);
    }

    public void beforeRenderDialog(final ViewDefinitionState view) {
        final FieldComponent reasonTypeField = (FieldComponent) view.getComponentByReference(REASON_TYPES);
        final FieldComponent reasonRequiredField = (FieldComponent) view.getComponentByReference(REASON_REQUIRED);
        reasonRequiredField.setFieldValue(true);
        reasonTypeField.setRequired(true);
    }

    public void beforeRenderDetails(final ViewDefinitionState view) {
        final FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        final FieldComponent reasonTypeField = (FieldComponent) view.getComponentByReference(REASON_TYPES);
        reasonTypeField.setRequired(form.getEntity().getBooleanField(REASON_REQUIRED));
        Entity orderStateChange = form.getPersistedEntityWithIncludedFormValues();
        boolean visible = orderStateChange.getBooleanField(OrderStateChangeFields.DATES_CHANGED);
        for (String fieldName : DATE_FIELDS) {
            FieldComponent field = (FieldComponent) view.getComponentByReference(fieldName);
            field.setVisible(visible);
        }
    }

    public void onOrderStateChangeCreate(final DataDefinition dataDefinition, final Entity orderStateChange) {
        if (orderStateChange.getField(OrderStateChangeFields.DATES_CHANGED) == null) {
            orderStateChange.setField(OrderStateChangeFields.DATES_CHANGED, false);
        }
    }
}
