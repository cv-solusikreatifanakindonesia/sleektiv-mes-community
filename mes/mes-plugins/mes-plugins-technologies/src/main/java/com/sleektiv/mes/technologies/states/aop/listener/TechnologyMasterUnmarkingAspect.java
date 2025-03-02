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

import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunForStateTransitions;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.aop.TechnologyStateChangeAspect;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateChangePhase;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateStringValues;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.plugin.api.RunIfEnabled;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Objects;

@Aspect
@Configurable
@RunIfEnabled(TechnologiesConstants.PLUGIN_IDENTIFIER)
public class TechnologyMasterUnmarkingAspect extends AbstractStateListenerAspect {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @RunInPhase(TechnologyStateChangePhase.LAST)
    @RunForStateTransitions({@RunForStateTransition(targetState = TechnologyStateStringValues.OUTDATED),
            @RunForStateTransition(targetState = TechnologyStateStringValues.DECLINED)})
    @After(PHASE_EXECUTION_POINTCUT)
    public void postHookOnOutdatingOrDeclining(final StateChangeContext stateChangeContext, final int phase) {
        Entity technology = stateChangeContext.getOwner();

        Entity product = technology.getBelongsToField(TechnologyFields.PRODUCT);
        boolean master = technology.getBooleanField(TechnologyFields.MASTER);

        if (master) {
            setTechnologyMaster(technology, false);
            setTechnologyMaster(findTechnologyForProduct(product), true);
        }
    }

    private void setTechnologyMaster(final Entity technology, final boolean master) {
        if (Objects.nonNull(technology)) {
            technology.setField(TechnologyFields.MASTER, master);

            if (master) {
                technology.getDataDefinition().fastSave(technology);
            }
        }
    }

    @Pointcut(TechnologyStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    private Entity findTechnologyForProduct(final Entity product) {
        return getTechnologyDD().find()
                .createAlias(TechnologyFields.PRODUCT, TechnologyFields.PRODUCT, JoinType.LEFT)
                .add(SearchRestrictions.eq(TechnologyFields.ACTIVE, true))
                .add(SearchRestrictions.eq(TechnologyFields.MASTER, false))
                .add(SearchRestrictions.eq(TechnologyFields.PRODUCT + "." + "id", product.getId()))
                .add(SearchRestrictions.eq(TechnologyFields.STATE, TechnologyStateStringValues.ACCEPTED))
                .addOrder(SearchOrders.desc(TechnologyFields.NUMBER))
                .setMaxResults(1).uniqueResult();
    }

    private DataDefinition getTechnologyDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGY);
    }

}
