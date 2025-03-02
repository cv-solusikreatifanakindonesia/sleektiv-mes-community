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

import com.sleektiv.mes.orders.TechnologyServiceO;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.client.OrderStateChangeViewClient;
import com.sleektiv.mes.states.service.client.util.ViewContextHolder;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.TechnologyStateChangeViewClient;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateStringValues;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersListListeners {

    @Autowired
    private OrderStateChangeViewClient orderStateChangeViewClient;

    @Autowired
    private TechnologyStateChangeViewClient technologyStateChangeViewClient;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private TechnologyServiceO technologyServiceO;

    public void changeState(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent gridComponent = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        for (Long orderId : gridComponent.getSelectedEntitiesIds()) {
            Entity order = getOrderDD().get(orderId);
            order = getOrderDD().save(order);
            if (order.isValid()) {
                Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);
                if (TechnologyStateStringValues.DRAFT.equals(technology.getStringField(TechnologyFields.STATE))) {
                    technologyStateChangeViewClient.changeState(new ViewContextHolder(view, state),
                            TechnologyStateStringValues.ACCEPTED, technology);
                } else if (TechnologyStateStringValues.CHECKED.equals(technology.getStringField(TechnologyFields.STATE))) {
                    technologyServiceO.changeTechnologyStateToAccepted(technology);
                }
                orderStateChangeViewClient.changeState(new ViewContextHolder(view, state), args[0], order);
            } else {
                view.addMessage("orders.ordersList.changeState.validationErrors", ComponentState.MessageType.FAILURE, false,
                        order.getStringField(OrderFields.NUMBER));
            }
        }
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }
}
