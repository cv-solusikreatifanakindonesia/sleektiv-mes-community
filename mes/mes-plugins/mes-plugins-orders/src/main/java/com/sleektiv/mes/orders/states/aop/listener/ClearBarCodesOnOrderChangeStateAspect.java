package com.sleektiv.mes.orders.states.aop.listener;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderStateChangePhase;
import com.sleektiv.mes.orders.states.constants.OrderStateStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.annotation.RunForStateTransition;
import com.sleektiv.mes.states.annotation.RunInPhase;
import com.sleektiv.mes.states.aop.AbstractStateListenerAspect;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(OrdersConstants.PLUGIN_IDENTIFIER)
public class ClearBarCodesOnOrderChangeStateAspect extends AbstractStateListenerAspect {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Pointcut(OrderStateChangeAspect.SELECTOR_POINTCUT)
    protected void targetServicePointcut() {
        // empty by design
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(targetState = OrderStateStringValues.COMPLETED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void afterComplete(final StateChangeContext stateChangeContext, final int phase) {
        clearAssociatedBarcodeOperationComponents(stateChangeContext);
    }

    @RunInPhase(OrderStateChangePhase.LAST)
    @RunForStateTransition(targetState = OrderStateStringValues.ABANDONED)
    @Before(PHASE_EXECUTION_POINTCUT)
    public void afterAbandoning(final StateChangeContext stateChangeContext, final int phase) {
        clearAssociatedBarcodeOperationComponents(stateChangeContext);
    }

    private void clearAssociatedBarcodeOperationComponents(final StateChangeContext stateChangeContext) {
        final Entity order = stateChangeContext.getOwner();
        if (order != null && order.getId() != null) {
            String query = "DELETE FROM technologies_barcodeoperationcomponent WHERE order_id = :id";
            jdbcTemplate.update(query, new MapSqlParameterSource("id", order.getId()));
        }
    }

}