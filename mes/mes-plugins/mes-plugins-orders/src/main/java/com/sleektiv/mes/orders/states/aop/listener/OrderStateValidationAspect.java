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
package com.sleektiv.mes.orders.states.aop.listener;

import static com.sleektiv.mes.orders.states.constants.OrderStateStringValues.ACCEPTED;
import static com.sleektiv.mes.orders.states.constants.OrderStateStringValues.COMPLETED;
import static com.sleektiv.mes.orders.states.constants.OrderStateStringValues.IN_PROGRESS;
import static com.sleektiv.mes.states.aop.RunForStateTransitionAspect.WILDCARD_STATE;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.OrderStateValidationService;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateChangePhase;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(OrdersConstants.PLUGIN_IDENTIFIER)
public class OrderStateValidationAspect extends AbstractStateListenerAspect {

    @Autowired
    private OrderStateValidationService orderStateValidationService;

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(OrderStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = ACCEPTED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnAccept(final StateChangeContext stateChangeContext, final int phase) {
        orderStateValidationService.validationOnAccepted(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = IN_PROGRESS)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnInProgress(final StateChangeContext stateChangeContext, final int phase) {
        orderStateValidationService.validationOnInProgress(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = COMPLETED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnCompleted(final StateChangeContext stateChangeContext, final int phase) {
        orderStateValidationService.validationOnCompleted(stateChangeContext);
    }

}
