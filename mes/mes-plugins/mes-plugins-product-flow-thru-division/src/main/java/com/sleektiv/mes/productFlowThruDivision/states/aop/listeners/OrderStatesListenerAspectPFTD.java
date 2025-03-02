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
package com.sleektiv.mes.productFlowThruDivision.states.aop.listeners;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.aop.listener.OperationalTaskOrderStateAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateChangePhase;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.states.OrderStatesListenerServicePFTD;
import com.sleektiv.mes.productionCounting.constants.ParameterFieldsPC;
import com.sleektiv.mes.productionCounting.constants.ReleaseOfMaterials;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import static com.sleektiv.mes.orders.states.constants.OrderStateStringValues.*;
import static com.sleektiv.mes.states.aop.RunForStateTransitionAspect.WILDCARD_STATE;

@Aspect
@Configurable
@RunIfEnabled(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER)
public class OrderStatesListenerAspectPFTD extends AbstractStateListenerAspect {

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private OrderStatesListenerServicePFTD listenerService;

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = COMPLETED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onCompletedLast(final StateChangeContext stateChangeContext, final int phase) {
        Entity owner = stateChangeContext.getOwner();
        if(owner.isValid()) {
            listenerService.acceptInboundDocumentsForOrder(stateChangeContext);
            listenerService.clearReservations(stateChangeContext);
        }
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = ABANDONED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onAbandonedLast(final StateChangeContext stateChangeContext, final int phase) {
        listenerService.clearReservations(stateChangeContext);
    }


    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = DECLINED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onDeclinedLast(final StateChangeContext stateChangeContext, final int phase) {
        listenerService.clearReservations(stateChangeContext);
    }



    @RunInPhase(OrderStateChangePhase.EXT_SYNC)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = COMPLETED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onCompleted(final StateChangeContext stateChangeContext, final int phase) {
        String releaseOfMaterials = parameterService.getParameter().getStringField(ParameterFieldsPC.RELEASE_OF_MATERIALS);
        if (ReleaseOfMaterials.END_OF_THE_ORDER.getStringValue().equals(releaseOfMaterials)) {
            listenerService.createCumulatedInternalOutboundDocument(stateChangeContext);
        }
    }

    @RunInPhase(OrderStateChangePhase.EXT_SYNC)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = ABANDONED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onAbandoned(final StateChangeContext stateChangeContext, final int phase) {
        listenerService.acceptInboundDocumentsForOrder(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.DEFAULT)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = ACCEPTED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onAccepted(final StateChangeContext stateChangeContext, final int phase) {
        listenerService.checkMaterialAvailability(stateChangeContext);
        listenerService.checkOrderProductResourceReservationsInfo(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.DEFAULT)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = IN_PROGRESS)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onInProgress(final StateChangeContext stateChangeContext, final int phase) {
        listenerService.checkMaterialAvailability(stateChangeContext);
        listenerService.validateOrderProductResourceReservations(stateChangeContext);
    }

}
