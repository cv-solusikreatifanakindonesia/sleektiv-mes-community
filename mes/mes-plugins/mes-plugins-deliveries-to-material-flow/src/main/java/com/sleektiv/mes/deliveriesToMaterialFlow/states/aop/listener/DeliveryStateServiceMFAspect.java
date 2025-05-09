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
package com.sleektiv.mes.deliveriesToMaterialFlow.states.aop.listener;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.deliveries.states.aop.DeliveryStateChangeAspect;
import com.sleektiv.mes.deliveries.states.constants.DeliveryStateChangePhase;
import com.sleektiv.mes.deliveries.states.constants.DeliveryStateStringValues;
import com.sleektiv.mes.deliveriesToMaterialFlow.constants.DeliveriesToMaterialFlowConstants;
import com.sleektiv.mes.deliveriesToMaterialFlow.states.DeliveryStateServiceMF;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(DeliveriesToMaterialFlowConstants.PLUGIN_IDENTIFIER)
public class DeliveryStateServiceMFAspect extends AbstractStateListenerAspect {

    @Autowired
    private DeliveryStateServiceMF deliveryStateServiceMF;

    @Pointcut(DeliveryStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {

    }

    @RunInPhase(DeliveryStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(targetState = DeliveryStateStringValues.RECEIVED)
    @After(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnReceivedDelivery(final StateChangeContext stateChangeContext, final int phase) {
        deliveryStateServiceMF.validateRequiredParameters(stateChangeContext);
        deliveryStateServiceMF.validateReceivedPackages(stateChangeContext);
    }

    @RunInPhase(DeliveryStateChangePhase.LAST)
    @RunForStateTransition(targetState = DeliveryStateStringValues.RECEIVED)
    @After(PHASE_EXECUTION_POINTCUT)
    public void createTransfersForTheReceivedProducts(final StateChangeContext stateChangeContext, final int phase) {
        deliveryStateServiceMF.createDocumentsForTheReceivedProducts(stateChangeContext);
        deliveryStateServiceMF.createDocumentsForTheReceivedPackages(stateChangeContext);
    }

}
