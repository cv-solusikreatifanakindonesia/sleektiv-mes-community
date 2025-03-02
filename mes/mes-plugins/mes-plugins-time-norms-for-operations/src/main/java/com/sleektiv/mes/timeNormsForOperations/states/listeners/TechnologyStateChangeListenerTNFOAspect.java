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
package com.sleektiv.mes.timeNormsForOperations.states.listeners;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunForStateTransitions;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.mes.technologies.states.aop.TechnologyStateChangeAspect;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateChangePhase;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateStringValues;
import com.sleektiv.mes.timeNormsForOperations.constants.TimeNormsConstants;
import com.sleektiv.mes.timeNormsForOperations.states.TechnologyStateChangeListenerServiceTNFO;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(TimeNormsConstants.PLUGIN_IDENTIFIER)
public class TechnologyStateChangeListenerTNFOAspect extends AbstractStateListenerAspect {

    @Autowired
    private TechnologyStateChangeListenerServiceTNFO technologyStateChangeListenerTNFO;

    @Pointcut(TechnologyStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(TechnologyStateChangePhase.PRE_VALIDATION)
    @RunForStateTransitions({ @RunForStateTransition(targetState = TechnologyStateStringValues.ACCEPTED),
            @RunForStateTransition(targetState = TechnologyStateStringValues.CHECKED) })
    @After(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnAcceptingOrChecking(final StateChangeContext stateChangeContext, final int phase) {
        technologyStateChangeListenerTNFO.checkOperationOutputQuantities(stateChangeContext);
        technologyStateChangeListenerTNFO.checkIfAllOperationComponenthHaveTJSet(stateChangeContext);
    }

}
