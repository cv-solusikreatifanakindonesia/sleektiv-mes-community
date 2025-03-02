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
package com.sleektiv.mes.advancedGenealogy.states.aop.listener;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.advancedGenealogy.constants.AdvancedGenealogyConstants;
import com.sleektiv.mes.advancedGenealogy.states.aop.BatchStateChangeAspect;
import com.sleektiv.mes.advancedGenealogy.states.constants.BatchStateChangePhase;
import com.sleektiv.mes.advancedGenealogy.states.constants.BatchStateStringValues;
import com.sleektiv.mes.advancedGenealogy.states.listener.BatchBasicStateListenerService;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(AdvancedGenealogyConstants.PLUGIN_IDENTIFIER)
public class BatchBasicStateListenerAspect extends AbstractStateListenerAspect {

    @Autowired
    private BatchBasicStateListenerService basicStateListenerService;

    @Pointcut(BatchStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(BatchStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(targetState = BatchStateStringValues.BLOCKED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void onBlock(final StateChangeContext stateChangeContext, final int phase) {
        basicStateListenerService.checkIfOccursAsProducedBatch(stateChangeContext);
        basicStateListenerService.checkIfOccursAsUsedBatch(stateChangeContext);
    }

}
