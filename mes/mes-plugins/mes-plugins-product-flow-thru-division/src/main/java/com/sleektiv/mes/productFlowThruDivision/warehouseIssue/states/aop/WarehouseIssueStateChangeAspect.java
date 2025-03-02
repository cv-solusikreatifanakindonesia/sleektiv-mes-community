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
package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.aop;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueStateChangeDescriber;
import com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants.WarehouseIssueStateChangePhase;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.StateChangeEntityDescriber;
import com.sleektiv.mes.states.aop.AbstractStateChangeAspect;

@Aspect
@Service
public class WarehouseIssueStateChangeAspect extends AbstractStateChangeAspect {

    @Autowired
    private WarehouseIssueStateChangeDescriber describer;

    public static final String SELECTOR_POINTCUT = "this(com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.aop.WarehouseIssueStateChangeAspect)";

    @Override
    public StateChangeEntityDescriber getChangeEntityDescriber() {
        return describer;
    }

    @Override
    protected void changeStatePhase(final StateChangeContext stateChangeContext, final int phaseNumber) {
    }

    @Override
    protected int getNumOfPhases() {
        return WarehouseIssueStateChangePhase.getNumOfPhases();
    }

}
