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
package com.sleektiv.mes.deliveriesMinState;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.deliveriesMinState.constants.DeliveriesMinStateConstants;
import com.sleektiv.plugin.api.RunIfEnabled;
import com.sleektiv.tenant.api.MultiTenantService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RunIfEnabled(DeliveriesMinStateConstants.PLUGIN_IDENTIFIER)
public class DeliveriesMinStateService {

    @Autowired
    private MultiTenantService multiTenantService;

    @Autowired
    private DeliveriesMinStateHelper deliveriesMinStateHelper;

    @Autowired
    private ParameterService parameterService;

    public void automaticDeliveriesMinStateTrigger() {
        multiTenantService.doInMultiTenantContext(() -> {
            if (parameterService.getParameter().getBooleanField("automaticDeliveriesMinState")) {
                deliveriesMinStateHelper.createDeliveriesFromMinimalState();
            }
        });
    }

    public void triggerDeliveriesMinState(final ViewDefinitionState view, final ComponentState componentState, final String[] args) {
        deliveriesMinStateHelper.createDeliveriesFromMinimalState();
        componentState.addMessage("deliveriesMinState.createDeliveries.info", ComponentState.MessageType.SUCCESS);
    }

}
