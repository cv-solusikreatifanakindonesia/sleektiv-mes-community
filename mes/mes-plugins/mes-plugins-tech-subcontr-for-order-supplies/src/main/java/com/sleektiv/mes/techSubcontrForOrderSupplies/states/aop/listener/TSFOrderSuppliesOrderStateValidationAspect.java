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
package com.sleektiv.mes.techSubcontrForOrderSupplies.states.aop.listener;

import org.springframework.beans.factory.annotation.Autowired;

import com.sleektiv.mes.techSubcontrForOrderSupplies.states.TSFOrderSuppliesOrderStateValidationService;

//@Aspect
//@Configurable
//@RunIfEnabled(TechSubcontrForOrderSuppliesConstants.PLUGIN_IDENTIFIER)
public class TSFOrderSuppliesOrderStateValidationAspect {

    @Autowired
    private TSFOrderSuppliesOrderStateValidationService validationService;
/*
    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(OrderStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(sourceState = OrderStateStringValues.WILDCARD_STATE, targetState = OrderStateStringValues.ACCEPTED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnAccept(final StateChangeContext stateChangeContext, final int phase) {
        validationService.validationOnAccepted(stateChangeContext);
    }
*/
}
