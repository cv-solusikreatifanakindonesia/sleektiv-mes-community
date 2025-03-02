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

import java.math.BigDecimal;
import java.util.Date;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.constants.ParameterFieldsO;
import com.sleektiv.mes.orders.states.OrderStateService;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateChangePhase;
import com.sleektiv.mes.orders.states.constants.OrderStateStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(OrdersConstants.PLUGIN_IDENTIFIER)
public class FillFieldsOnChangesStatesAspect extends AbstractStateListenerAspect {
    @Autowired
    private OrderStateService orderStateService;

    @Autowired
    private ParameterService parameterService;

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(sourceState = OrderStateStringValues.ACCEPTED, targetState = OrderStateStringValues.IN_PROGRESS)
    @After(PHASE_EXECUTION_POINTCUT)
    public void afterStartProgress(final StateChangeContext stateChangeContext, final int phase) {
        stateChangeContext.getOwner().setField(OrderFields.DONE_QUANTITY, BigDecimal.ZERO);
        if (parameterService.getParameter().getBooleanField(ParameterFieldsO.SET_EFFECTIVE_DATE_FROM_ON_IN_PROGRESS)) {
            stateChangeContext.getOwner().setField(OrderFields.EFFECTIVE_DATE_FROM, new Date());
        }
        orderStateService.checkOrderDates(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(targetState = OrderStateStringValues.COMPLETED)
    @After(PHASE_EXECUTION_POINTCUT)
    public void afterComplete(final StateChangeContext stateChangeContext, final int phase) {
        if (parameterService.getParameter().getBooleanField(ParameterFieldsO.SET_EFFECTIVE_DATE_TO_ON_COMPLETED)) {
            stateChangeContext.getOwner().setField(OrderFields.EFFECTIVE_DATE_TO, new Date());
        }
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(targetState = OrderStateStringValues.ABANDONED)
    @After(PHASE_EXECUTION_POINTCUT)
    public void afterAbandoning(final StateChangeContext stateChangeContext, final int phase) {
    }

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }
}
