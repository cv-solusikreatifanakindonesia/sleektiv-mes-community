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
package com.sleektiv.mes.deliveries.states.aop.listeners;

import com.sleektiv.mes.deliveries.constants.DeliveriesConstants;
import com.sleektiv.mes.deliveries.states.DeliveryStateValidationService;
import com.sleektiv.mes.deliveries.states.aop.DeliveryStateChangeAspect;
import com.sleektiv.mes.deliveries.states.constants.DeliveryStateChangePhase;
import com.sleektiv.mes.deliveries.states.constants.DeliveryStateStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunForStateTransitions;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
@RunIfEnabled(DeliveriesConstants.PLUGIN_IDENTIFIER)
public class DeliveryStateValidationAspect extends AbstractStateListenerAspect {

    @Autowired
    private DeliveryStateValidationService validationService;

    @Pointcut(DeliveryStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(DeliveryStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(targetState = DeliveryStateStringValues.APPROVED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnApproved(final StateChangeContext stateChangeContext, final int phase) {
        validationService.validationOnApproved(stateChangeContext);
    }

    @RunInPhase(DeliveryStateChangePhase.PRE_VALIDATION)
    @RunForStateTransitions({
            @RunForStateTransition(targetState = DeliveryStateStringValues.RECEIVED)})
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnReceived(final StateChangeContext stateChangeContext, final int phase) {
        validationService.validationOnReceived(stateChangeContext);
    }

}
