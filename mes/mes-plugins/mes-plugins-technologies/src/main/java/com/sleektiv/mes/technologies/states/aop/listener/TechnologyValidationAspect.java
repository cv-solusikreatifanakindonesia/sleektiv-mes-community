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

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunForStateTransitions;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.mes.states.constants.StateChangeStatus;
import com.sleektiv.mes.technologies.constants.ParameterFieldsT;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.listeners.TechnologyDetailsListeners;
import com.sleektiv.mes.technologies.states.aop.TechnologyStateChangeAspect;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateChangeFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateChangePhase;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateStringValues;
import com.sleektiv.mes.technologies.states.listener.TechnologyValidationService;
import com.sleektiv.mes.technologies.validators.TechnologyTreeValidators;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.PluginUtils;
import com.sleektiv.plugin.api.RunIfEnabled;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
@RunIfEnabled(TechnologiesConstants.PLUGIN_IDENTIFIER)
public class TechnologyValidationAspect extends AbstractStateListenerAspect {

    @Autowired
    private TechnologyValidationService technologyValidationService;

    @Autowired
    private TechnologyTreeValidators technologyTreeValidators;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private TechnologyDetailsListeners technologyDetailsListeners;

    @Pointcut(TechnologyStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(TechnologyStateChangePhase.PRE_VALIDATION)
    @RunForStateTransitions({@RunForStateTransition(targetState = TechnologyStateStringValues.ACCEPTED),
            @RunForStateTransition(targetState = TechnologyStateStringValues.CHECKED)})
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnAcceptingOrChecking(final StateChangeContext stateChangeContext, final int phase) {
        if (!technologyValidationService.checkIfTechnologyTreeIsSet(stateChangeContext)) {
            return;
        }

        if (!technologyValidationService.checkIfEveryInComponentsHasQuantities(stateChangeContext)) {
            return;
        }

        if (!technologyValidationService.checkIfWasteProductsIsRightMarked(stateChangeContext)) {
            return;
        }

        if (technologyValidationService.checkIfRootOperationIsSubOrder(stateChangeContext)) {
            return;
        }

        Entity technology = stateChangeContext.getOwner();
        String targetState = stateChangeContext.getStateChangeEntity().getStringField(TechnologyStateChangeFields.TARGET_STATE);
        Entity parameter = parameterService.getParameter();

        if (parameter.getBooleanField(ParameterFieldsT.MOVE_PRODUCTS_TO_SUBSEQUENT_OPERATIONS)) {
            technologyDetailsListeners.fillProducts(technology);
        }

        if (TechnologyStateStringValues.ACCEPTED.equals(targetState) || (TechnologyStateStringValues.CHECKED.equals(targetState)
                && !parameter.getBooleanField(ParameterFieldsT.ALLOW_CHECKED_TECHNOLOGY_WITHOUT_IN_PRODUCTS))) {
            technologyValidationService.checkIfEveryOperationHasInComponents(stateChangeContext);
        }

        technologyValidationService.checkConsumingManyProductsFromOneSubOp(stateChangeContext);
        technologyTreeValidators.checkConsumingTheSameProductFromManySubOperations(technology.getDataDefinition(), technology,
                true);
        technologyValidationService.checkIfTechnologyHasAtLeastOneComponent(stateChangeContext);
        technologyValidationService.checkTopComponentsProducesProductForTechnology(stateChangeContext);
        technologyValidationService.checkIfOperationsUsesSubOperationsProds(stateChangeContext);
        technologyValidationService.checkIfOperationsUsesSubOperationsWasteProds(stateChangeContext);
        technologyValidationService.checkTechnologyCycles(stateChangeContext);

        if (TechnologyStateStringValues.ACCEPTED.equals(targetState)) {
            technologyValidationService.checkDifferentProductsInDifferentSizes(stateChangeContext);
        }

        if (PluginUtils.isEnabled("timeNormsForOperations")) {
            if (!StateChangeStatus.FAILURE.equals(stateChangeContext.getStatus())) {
                technologyValidationService.checkIfTreeOperationIsValid(stateChangeContext);
            }
        }

        if (parameter.getBooleanField(ParameterFieldsT.CHECK_FOR_THE_EXISTENCE_OF_INPUT_PRODUCT_PRICES)) {
            technologyValidationService.checkIfInputProductPricesSet(stateChangeContext);

        }

        if (parameter.getBooleanField(ParameterFieldsT.DIMENSION_CONTROL_OF_PRODUCTS)) {
            technologyValidationService.checkDimensionControlOfProducts(stateChangeContext);
        }
    }

    @RunInPhase(TechnologyStateChangePhase.PRE_VALIDATION)
    @RunForStateTransitions({@RunForStateTransition(targetState = TechnologyStateStringValues.OUTDATED),
            @RunForStateTransition(targetState = TechnologyStateStringValues.DECLINED)})
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnOutdatingOrDeclining(final StateChangeContext stateChangeContext, final int phase) {
        technologyValidationService.checkIfTechnologyIsNotUsedInActiveOrder(stateChangeContext);
    }

}
