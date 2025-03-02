package com.sleektiv.mes.orders.states;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.ShiftsService;
import com.sleektiv.mes.newstates.StateExecutorService;
import com.sleektiv.mes.orders.constants.*;
import com.sleektiv.mes.orders.hooks.OperationalTaskHooks;
import com.sleektiv.mes.orders.listeners.OrderDetailsListeners;
import com.sleektiv.mes.orders.states.constants.OperationalTaskStateStringValues;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.constants.StateChangeStatus;
import com.sleektiv.mes.states.messages.constants.StateMessageType;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.security.api.SecurityService;
import org.apache.commons.beanutils.MethodUtils;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.util.FieldUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OperationalTaskOrderStateService {

    private static final Logger LOG = LoggerFactory.getLogger(OperationalTaskOrderStateService.class);

    private static final String L_FOR_EACH = "03forEach";

    private static final String L_TYPE_OF_PRODUCTION_RECORDING = "typeOfProductionRecording";

    private static final String L_ORDER_ID = "orderId";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private StateExecutorService stateExecutorService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private ShiftsService shiftsService;

    @Autowired
    private OperationalTaskStateChangeDescriber operationalTaskStateChangeDescriber;

    @Autowired
    private OperationalTaskHooks operationalTaskHooks;

    @Autowired
    private OrderDetailsListeners orderDetailsListeners;

    public void startOperationalTask(final StateChangeContext stateChangeContext) {
        try {
            changeOperationalTaskState(stateChangeContext,
                    "UPDATE com.sleektiv.model.beans.orders.OrdersOperationalTask SET state = '02started' WHERE order_id = :orderId");
        } catch (Exception exc) {
            stateChangeContext.addMessage("orders.operationalTask.error.startOperationalTask", StateMessageType.FAILURE);
            stateChangeContext.setStatus(StateChangeStatus.FAILURE);

            LOG.error("Error when start operational task.", exc);
        }
    }

    public void rejectOperationalTask(final StateChangeContext stateChangeContext) {
        try {
            Entity order = stateChangeContext.getOwner();

            List<Entity> operationalTasks = order.getHasManyField(OrderFields.OPERATIONAL_TASKS);

            operationalTasks.forEach(this::resetWorkstationChangeoverForOperationalTasks);

            changeOperationalTaskState(stateChangeContext,
                    "UPDATE com.sleektiv.model.beans.orders.OrdersOperationalTask SET state = '04rejected' WHERE order_id = :orderId");
        } catch (Exception exc) {
            stateChangeContext.addMessage("orders.operationalTask.error.rejectOperationalTask", StateMessageType.FAILURE);
            stateChangeContext.setStatus(StateChangeStatus.FAILURE);

            LOG.error("Error when reject operational task.", exc);
        }
    }

    public void finishOperationalTask(final StateChangeContext stateChangeContext) {
        try {
            changeOperationalTaskState(stateChangeContext,
                    "UPDATE com.sleektiv.model.beans.orders.OrdersOperationalTask SET state = '03finished' WHERE order_id = :orderId AND state = '02started'");
        } catch (Exception exc) {
            stateChangeContext.addMessage("orders.operationalTask.error.finishOperationalTask", StateMessageType.FAILURE);
            stateChangeContext.setStatus(StateChangeStatus.FAILURE);

            LOG.error("Error when finish operational task.", exc);
        }
    }

    private void changeOperationalTaskState(final StateChangeContext stateChangeContext, final String updateOTHQL) {
        Entity order = stateChangeContext.getOwner();

        Session currentSession = getCurrentSession();

        Query updateOTQuery = currentSession.createQuery(updateOTHQL);

        updateOTQuery.setLong(L_ORDER_ID, order.getId());

        updateOTQuery.executeUpdate();
    }

    private Session getCurrentSession() {
        DataDefinition dataDefinition = getOperationalTaskDD();

        Object dataAccessService = FieldUtils.getProtectedFieldValue("dataAccessService", dataDefinition);
        Object hibernateService = FieldUtils.getProtectedFieldValue("hibernateService", dataAccessService);

        try {
            return (Session) MethodUtils.invokeExactMethod(hibernateService, "getCurrentSession", new Object[0]);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void generateOperationalTasks(StateChangeContext stateChangeContext) {
        if (parameterService.getParameter().getBooleanField(ParameterFieldsO.AUTOMATICALLY_GENERATE_TASKS_FOR_ORDER)) {
            Entity order = stateChangeContext.getOwner();

            if (L_FOR_EACH.equals(order.getStringField(L_TYPE_OF_PRODUCTION_RECORDING))
                    && order.getHasManyField(OrderFields.OPERATIONAL_TASKS).isEmpty()) {
                orderDetailsListeners.createOperationalTasksForOrder(order, true);
            }
        }
    }

    public void rejectOperationalTasksForSchedule(final Entity schedule) {
        String userLogin = securityService.getCurrentUserName();
        Entity shift = shiftsService.getShiftFromDateWithTime(new Date());

        List<Entity> positions = schedule.getHasManyField(ScheduleFields.POSITIONS);

        try {
            DataDefinition operationalTaskDD = getOperationalTaskDD();

            for (Entity position : positions) {
                Entity operationalTask = operationalTaskDD.find()
                        .createAlias(OperationalTaskFields.SCHEDULE_POSITION, OperationalTaskFields.SCHEDULE_POSITION, JoinType.LEFT)
                        .add(SearchRestrictions.eq(OperationalTaskFields.SCHEDULE_POSITION + "." + "id", position.getId()))
                        .setMaxResults(1).uniqueResult();

                if (Objects.nonNull(operationalTask)) {
                    Entity operationalTaskStateChange = stateExecutorService.buildStateChangeEntity(operationalTaskStateChangeDescriber,
                            operationalTask, userLogin, operationalTask.getStringField(OperationalTaskFields.STATE),
                            OperationalTaskStateStringValues.REJECTED, shift);

                    operationalTaskStateChange.setField(OperationalTaskStateChangeFields.STATUS, StateChangeStatus.SUCCESSFUL.getStringValue());
                    operationalTaskStateChange.getDataDefinition().fastSave(operationalTaskStateChange);

                    operationalTask.setField(OperationalTaskFields.STATE, OperationalTaskStateStringValues.REJECTED);
                    operationalTask.getDataDefinition().fastSave(operationalTask);

                    resetWorkstationChangeoverForOperationalTasks(operationalTask);
                }
            }
        } catch (Exception exc) {
            schedule.addGlobalError("orders.operationalTask.error.rejectOperationalTask");

            LOG.error("Error when reject operational task.", exc);
        }
    }

    private void resetWorkstationChangeoverForOperationalTasks(final Entity operationalTask) {
        operationalTaskHooks.deleteWorkstationChangeoverForOperationalTasks(operationalTask);
        operationalTaskHooks.setPreviousWorkstationChangeoverForOperationalTasks(operationalTask, true);
    }

    private DataDefinition getOperationalTaskDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_OPERATIONAL_TASK);
    }

}
