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
package com.sleektiv.mes.basicProductionCounting.listeners;

import com.sleektiv.mes.basicProductionCounting.BasicProductionCountingService;
import com.sleektiv.mes.basicProductionCounting.constants.BasicProductionCountingFields;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BasicProductionCountingDetailsListeners {

    @Autowired
    private BasicProductionCountingService basicProductionCountingService;

    public void fillDoneQuantityField(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent producedQuantity = (FieldComponent) view
                .getComponentByReference(BasicProductionCountingFields.PRODUCED_QUANTITY);

        Long basicProductionCountingId = form.getEntityId();

        if (basicProductionCountingId != null) {
            Entity basicProductionCounting = basicProductionCountingService.getBasicProductionCounting(basicProductionCountingId);

            Entity order = basicProductionCounting.getBelongsToField(BasicProductionCountingFields.ORDER);
            Entity product = basicProductionCounting.getBelongsToField(BasicProductionCountingFields.PRODUCT);

            if (order.getBelongsToField(OrderFields.PRODUCT).getId().equals(product.getId())) {
                final String fieldValue = (String) producedQuantity.getFieldValue();
                if (fieldValue == null || fieldValue.isEmpty()) {
                    return;
                }
                try {
                    final BigDecimal doneQuantity = new BigDecimal(fieldValue.replace(",", ".").replace(" ", "")
                            .replace("\u00A0", ""));
                    order.setField(OrderFields.DONE_QUANTITY, doneQuantity);
                    order = order.getDataDefinition().save(order);
                } catch (NumberFormatException ex) {
                    return;
                }
                if (!order.isValid()) {
                    producedQuantity.addMessage(order.getError(OrderFields.DONE_QUANTITY));
                }
            }
        }
    }

}
