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
package com.sleektiv.mes.deliveries.hooks;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.constants.ParameterFieldsD;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sleektiv.mes.deliveries.constants.DeliveryFields.RELATED_DELIVERIES;

@Service
public class DeliveriesListHooks {

    private static final String L_DELIVERY = "delivery";

    @Autowired
    private DeliveriesService deliveriesService;

    @Autowired
    private ParameterService parameterService;

    public void fillGridWithRelatedDeliveries(final ViewDefinitionState view) {
        GridComponent deliveriesGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        FormComponent deliveryForm = (FormComponent) view.getComponentByReference(L_DELIVERY);

        Long deliveryId = deliveryForm.getEntityId();

        if (deliveryId == null) {
            return;
        }

        Entity delivery = deliveriesService.getDelivery(deliveryId);

        deliveriesGrid.setEntities(getRelatedDeliveries(delivery));
    }

    public void disableSendEmailButton(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup email = window.getRibbon().getGroupByName("email");
        RibbonActionItem sendEmail = email.getItemByName("sendEmail");
        if (!parameterService.getParameter().getBooleanField(ParameterFieldsD.SEND_EMAIL_TO_SUPPLIER)) {
            sendEmail.setEnabled(false);
            sendEmail.requestUpdate(true);
        }
    }

    private List<Entity> getRelatedDeliveries(final Entity delivery) {
        List<Entity> relatedDeliveries = Lists.newArrayList();

        List<Entity> deliveryRelatedDeliveries = delivery.getHasManyField(RELATED_DELIVERIES);

        for (Entity relatedDelivery : deliveryRelatedDeliveries) {
            relatedDeliveries.add(relatedDelivery);

            relatedDeliveries.addAll(getRelatedDeliveries(relatedDelivery));
        }

        return relatedDeliveries;
    }

}
