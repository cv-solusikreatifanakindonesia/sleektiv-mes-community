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
package com.sleektiv.mes.technologies.states.aop.listener;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.mes.technologies.constants.ParameterFieldsT;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.aop.TechnologyStateChangeAspect;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateStringValues;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(TechnologiesConstants.PLUGIN_IDENTIFIER)
public class TechnologyMasterAcceptingAspect extends AbstractStateListenerAspect {

    @Autowired
    private ParameterService parameterService;

    @RunForStateTransition(targetState = TechnologyStateStringValues.ACCEPTED)
    @After(CHANGE_STATE_EXECUTION_POINTCUT)
    public void postHookOnAccepting(final StateChangeContext stateChangeContext) {
        Entity parameter = parameterService.getParameter();
        if (parameter.getBooleanField(ParameterFieldsT.ACCEPTED_TECHNOLOGY_MARKED_AS_DEFAULT)) {
            Entity owner = stateChangeContext.getOwner();
            owner.setField(TechnologyFields.MASTER, true);
            owner.getDataDefinition().save(owner);
        }
    }

    @Pointcut(TechnologyStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }
}
