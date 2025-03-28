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
package com.sleektiv.mes.basicProductionCounting.aop;

import com.sleektiv.mes.basicProductionCounting.BpcOrderStateListenerService;
import com.sleektiv.mes.basicProductionCounting.constants.BasicProductionCountingConstants;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.mes.states.aop.RunForStateTransitionAspect;
import com.sleektiv.plugin.api.RunIfEnabled;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import static com.sleektiv.mes.orders.states.constants.OrderStateChangePhase.LAST;

@Aspect
@Configurable
@RunIfEnabled(BasicProductionCountingConstants.PLUGIN_IDENTIFIER)
public class BpcOrderStateListenerAspect extends AbstractStateListenerAspect {

    @Autowired
    private BpcOrderStateListenerService listenerService;

    @RunInPhase(LAST)
    @RunForStateTransition(sourceState = RunForStateTransitionAspect.WILDCARD_STATE, targetState = OrderStateStringValues.ACCEPTED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onAccept(final StateChangeContext stateChangeContext, final int phase) {
        listenerService.onAccept(stateChangeContext);
    }

    @RunInPhase(LAST)
    @RunForStateTransition(sourceState = OrderStateStringValues.PENDING, targetState = OrderStateStringValues.IN_PROGRESS)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onStartFromPending(final StateChangeContext stateChangeContext, final int phase) {
        listenerService.onAccept(stateChangeContext);
    }

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

}
