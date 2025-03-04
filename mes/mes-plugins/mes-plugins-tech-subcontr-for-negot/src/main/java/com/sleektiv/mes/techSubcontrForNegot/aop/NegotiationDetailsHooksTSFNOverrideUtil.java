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
package com.sleektiv.mes.techSubcontrForNegot.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.deliveries.states.constants.DeliveryStateStringValues;
import com.sleektiv.mes.supplyNegotiations.hooks.NegotiationDetailsHooks;
import com.sleektiv.mes.techSubcontrForNegot.constants.TechSubcontrForNegotConstants;
import com.sleektiv.plugin.api.PluginStateResolver;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class NegotiationDetailsHooksTSFNOverrideUtil {

    private static final String L_ORDERED_QUANTITY = "orderedQuantity";

    private static final String L_NEGOTIATION_PRODUCT_ID = "negotiationProductId";

    @Autowired
    private PluginStateResolver pluginStateResolver;

    @Autowired
    private NegotiationDetailsHooks negotiationDetailsHooks;

    public boolean shouldOverride() {
        return pluginStateResolver.isEnabled(TechSubcontrForNegotConstants.PLUGIN_IDENTIFIER);
    }

    public void changeApprovedNotApprovedLeftQuantity(final ViewDefinitionState view) {
        FormComponent negotiationForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Long negotiationId = negotiationForm.getEntityId();

        if (negotiationId != null) {
            String queryNotApprovedDeliveries = getNotApprovedDeliveriesQuery(negotiationId);
            String queryApprovedDeliveries = getApprovedDeliveriesQuery(negotiationId);

            negotiationDetailsHooks.changeApprovedNotApprovedLeftQuantity(negotiationId, queryNotApprovedDeliveries,
                    queryApprovedDeliveries);
        }
    }

    private String getNotApprovedDeliveriesQuery(final Long negotiationId) {
        String queryApprovedDeliveries = String.format("SELECT np.id AS " + L_NEGOTIATION_PRODUCT_ID
                + ", SUM(op.orderedQuantity) AS " + L_ORDERED_QUANTITY + " FROM #deliveries_orderedProduct op "
                + "JOIN op.offer.negotiation.negotiationProducts np WHERE op.offer.negotiation.id = %s "
                + "AND op.delivery.state IN ('" + DeliveryStateStringValues.DRAFT + "', '" + DeliveryStateStringValues.PREPARED
                + "', '" + DeliveryStateStringValues.DURING_CORRECTION + "') AND op.product.id = np.product.id "
                + "AND ((op.operation.id IS null AND np.operation.id IS null) OR op.operation.id = np.operation.id) "
                + "GROUP BY np.id", negotiationId);

        return queryApprovedDeliveries;
    }

    private String getApprovedDeliveriesQuery(final Long negotiationId) {
        String queryNotApprovedDeliveries = String.format("SELECT np.id AS " + L_NEGOTIATION_PRODUCT_ID
                + ", SUM(op.orderedQuantity) AS " + L_ORDERED_QUANTITY + " FROM #deliveries_orderedProduct op "
                + "JOIN op.offer.negotiation.negotiationProducts np WHERE op.offer.negotiation.id = %s "
                + "AND op.delivery.state IN ('" + DeliveryStateStringValues.APPROVED + "') AND op.product.id = np.product.id "
                + "AND ((op.operation.id IS null AND np.operation.id IS null) OR op.operation.id = np.operation.id) "
                + "GROUP BY np.id", negotiationId);

        return queryNotApprovedDeliveries;
    }

}
