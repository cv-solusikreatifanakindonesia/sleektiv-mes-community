package com.sleektiv.mes.costCalculation.states;

import static com.sleektiv.mes.orders.states.constants.OrderStateStringValues.COMPLETED;
import static com.sleektiv.mes.states.aop.RunForStateTransitionAspect.WILDCARD_STATE;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.costCalculation.constants.CostCalculationConstants;
import com.sleektiv.mes.costCalculation.constants.OrderAdditionalDirectCostFields;
import com.sleektiv.mes.costCalculation.constants.OrderFieldsCC;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateChangePhase;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(CostCalculationConstants.PLUGIN_IDENTIFIER)
public class OrderStatesAspectCC extends AbstractStateListenerAspect {

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(OrderStateChangePhase.PRE_VALIDATION)
    @RunForStateTransition(sourceState = WILDCARD_STATE, targetState = COMPLETED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void preValidationOnCompleted(final StateChangeContext stateChangeContext, final int phase) {
        if (stateChangeContext.getOwner().getHasManyField(OrderFieldsCC.ORDER_ADDITIONAL_DIRECT_COSTS)
                .stream().anyMatch(e -> e.getDecimalField(OrderAdditionalDirectCostFields.ACTUAL_COST) == null)) {
            stateChangeContext.addValidationError("orders.order.orderStates.orderAdditionalDirectCostsShouldHaveCost");
        }
    }
}
