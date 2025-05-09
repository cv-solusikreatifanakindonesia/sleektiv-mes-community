/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv. Project: Sleektiv Framework Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * ***************************************************************************
 */
package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.aop.listeners;

import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.reservation.ReservationsServiceForProductsToIssue;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.constans.WarehouseIssueFields;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.WarehouseIssueStateService;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.aop.WarehouseIssueStateChangeAspect;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueStateChangePhase;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityList;
import com.sleektiv.plugin.api.RunIfEnabled;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
@RunIfEnabled(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER)
public class WarehouseIssueListenerAspect extends AbstractStateListenerAspect {

    @Autowired
    private WarehouseIssueStateService issueStateService;

    @Autowired
    private ReservationsServiceForProductsToIssue reservationsServiceForProductsToIssue;

    @RunInPhase(WarehouseIssueStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(targetState = WarehouseIssueStringValues.IN_PROGRESS)
    @After(PHASE_EXECUTION_POINTCUT)
    public void onIssueValidate(final StateChangeContext stateChangeContext, final int phase) {
        issueStateService.onIssueValidate(stateChangeContext);
    }  
    
    @RunInPhase(WarehouseIssueStateChangePhase.DEFAULT)
    @RunForStateTransition(targetState = WarehouseIssueStringValues.IN_PROGRESS)
    @After(PHASE_EXECUTION_POINTCUT)
    public void onIssue(final StateChangeContext stateChangeContext, final int phase) {
        issueStateService.onIssue(stateChangeContext);
    }

    @RunInPhase(WarehouseIssueStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(sourceState = WarehouseIssueStringValues.IN_PROGRESS, targetState = WarehouseIssueStringValues.COMPLETED)
    @After(PHASE_EXECUTION_POINTCUT)
    public void onCompleted(final StateChangeContext stateChangeContext, final int phase) {
        if (issueStateService.checkIfAnyNotIssuedPositionsExists(stateChangeContext)) {
            return;
        }

        Entity warehouseIssue = stateChangeContext.getOwner();
        EntityList productsToIssue = warehouseIssue.getHasManyField(WarehouseIssueFields.PRODUCTS_TO_ISSUES);
        productsToIssue.forEach(productToIssue -> {
            reservationsServiceForProductsToIssue.deleteReservationFromProductToIssue(productToIssue);
        });
    }

    @RunInPhase(WarehouseIssueStateChangePhase.LAST)
    @RunForStateTransition(sourceState = WarehouseIssueStringValues.DRAFT, targetState = WarehouseIssueStringValues.DISCARD)
    @After(PHASE_EXECUTION_POINTCUT)
    public void onDiscard(final StateChangeContext stateChangeContext, final int phase) {
        Entity warehouseIssue = stateChangeContext.getOwner();
        EntityList productsToIssue = warehouseIssue.getHasManyField(WarehouseIssueFields.PRODUCTS_TO_ISSUES);
        productsToIssue.forEach(productToIssue -> {
            reservationsServiceForProductsToIssue.deleteReservationFromProductToIssue(productToIssue);
        });
    }

    @Pointcut(WarehouseIssueStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

}
