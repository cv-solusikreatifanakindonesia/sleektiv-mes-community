package com.sleektiv.mes.orders.states.aop.listener;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.OperationalTaskOrderStateService;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateChangePhase;
import com.sleektiv.mes.orders.states.constants.OrderStateStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.plugin.api.RunIfEnabled;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
@RunIfEnabled(OrdersConstants.PLUGIN_IDENTIFIER)
public class OperationalTaskOrderStateAspect extends AbstractStateListenerAspect {
    @Autowired
    private OperationalTaskOrderStateService operationalTaskOrderStateService;

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
    }

    @RunInPhase(OrderStateChangePhase.DEFAULT)
    @RunForStateTransition(sourceState = OrderStateStringValues.PENDING, targetState = OrderStateStringValues.ACCEPTED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void generateOperationalTasksForAccepted(final StateChangeContext stateChangeContext, final int phase) {
        operationalTaskOrderStateService.generateOperationalTasks(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.DEFAULT)
    @RunForStateTransition(sourceState = OrderStateStringValues.PENDING, targetState = OrderStateStringValues.IN_PROGRESS)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void generateOperationalTasksForInProgres(final StateChangeContext stateChangeContext, final int phase) {
        operationalTaskOrderStateService.generateOperationalTasks(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.DEFAULT)
    @RunForStateTransition(targetState = OrderStateStringValues.IN_PROGRESS)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void startOperationalTask(final StateChangeContext stateChangeContext, final int phase) {
        operationalTaskOrderStateService.startOperationalTask(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(targetState = OrderStateStringValues.DECLINED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void rejectOperationalTaskForDeclinedOrder(final StateChangeContext stateChangeContext, final int phase) {
        operationalTaskOrderStateService.rejectOperationalTask(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(targetState = OrderStateStringValues.ABANDONED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void rejectOperationalTaskForAbandonedOrder(final StateChangeContext stateChangeContext, final int phase) {
        operationalTaskOrderStateService.rejectOperationalTask(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.DEFAULT)
    @RunForStateTransition(targetState = OrderStateStringValues.COMPLETED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void finishOperationalTask(final StateChangeContext stateChangeContext, final int phase) {
        operationalTaskOrderStateService.finishOperationalTask(stateChangeContext);
    }
}
