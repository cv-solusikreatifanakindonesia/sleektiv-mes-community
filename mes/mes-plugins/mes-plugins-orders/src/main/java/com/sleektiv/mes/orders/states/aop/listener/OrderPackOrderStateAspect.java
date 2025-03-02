package com.sleektiv.mes.orders.states.aop.listener;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.OrderPackService;
import com.sleektiv.mes.orders.OrderTechnologicalProcessService;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.constants.ParameterFieldsO;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
@RunIfEnabled(OrdersConstants.PLUGIN_IDENTIFIER)
public class OrderPackOrderStateAspect extends AbstractStateListenerAspect {


    @Autowired
    private OrderPackService orderPackService;

    @Autowired
    private OrderTechnologicalProcessService orderTechnologicalProcessService;

    @Autowired
    private ParameterService parameterService;

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunForStateTransition(targetState = OrderStateStringValues.IN_PROGRESS)
    @After(CHANGE_STATE_EXECUTION_POINTCUT)
    public void generateOrderPacks(final StateChangeContext stateChangeContext) {
        orderPackService.generateOrderPacks(stateChangeContext.getOwner());

        if (parameterService.getParameter().getBooleanField(ParameterFieldsO.AUTOMATICALLY_GENERATE_PROCESSES_FOR_ORDER)) {
            orderTechnologicalProcessService.generateOrderTechnologicalProcesses(stateChangeContext.getOwner());
        }
    }
}

