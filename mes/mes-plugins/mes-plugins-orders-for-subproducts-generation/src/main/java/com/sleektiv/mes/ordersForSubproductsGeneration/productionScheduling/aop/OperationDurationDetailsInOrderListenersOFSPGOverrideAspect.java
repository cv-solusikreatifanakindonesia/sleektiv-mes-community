/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.ordersForSubproductsGeneration.productionScheduling.aop;

import com.google.common.collect.Lists;
import com.sleektiv.mes.operationTimeCalculations.OperationWorkTime;
import com.sleektiv.mes.operationTimeCalculations.OperationWorkTimeService;
import com.sleektiv.mes.operationTimeCalculations.constants.OperCompTimeCalculationsFields;
import com.sleektiv.mes.operationTimeCalculations.constants.OperationTimeCalculationsConstants;
import com.sleektiv.mes.operationTimeCalculations.constants.OrderTimeCalculationFields;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.ordersForSubproductsGeneration.constants.OrdersForSubproductsGenerationConstans;
import com.sleektiv.mes.ordersForSubproductsGeneration.productionScheduling.OrdersByLevel;
import com.sleektiv.mes.ordersForSubproductsGeneration.productionScheduling.ProductionSchedulingForComponentsService;
import com.sleektiv.mes.productionLines.constants.ProductionLinesConstants;
import com.sleektiv.mes.productionScheduling.OrderRealizationTimeService;
import com.sleektiv.mes.productionScheduling.ProductionSchedulingService;
import com.sleektiv.mes.productionScheduling.constants.OrderFieldsPS;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.plugin.api.RunIfEnabled;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.Validate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Configurable
@RunIfEnabled(OrdersForSubproductsGenerationConstans.PLUGIN_IDENTIFIER)
public class OperationDurationDetailsInOrderListenersOFSPGOverrideAspect {

    private static final String L_START_TIME = "startTime";

    private static final String L_STOP_TIME = "stopTime";

    private static final String L_PRODUCTION_SCHEDULING_ERROR_FIELD_REQUIRED = "productionScheduling.error.fieldRequired";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private OrderRealizationTimeService orderRealizationTimeService;

    @Autowired
    private OperationWorkTimeService operationWorkTimeService;

    @Autowired
    private ProductionSchedulingForComponentsService productionSchedulingForComponentsService;

    @Autowired
    private ProductionSchedulingService productionSchedulingService;

    @Pointcut("execution(public void com.sleektiv.mes.productionScheduling.listeners.OperationDurationDetailsInOrderListeners.copyRealizationTime(..)) "
            + "&& args(viewDefinitionState, state, args)")
    public void copyRealizationTimeE(final ViewDefinitionState viewDefinitionState, final ComponentState state,
                                     final String[] args) {
    }

    @Around("copyRealizationTimeE(viewDefinitionState, state, args)")
    public void aroundCopyRealizationTime(final ProceedingJoinPoint pjp, final ViewDefinitionState viewDefinitionState,
                                          final ComponentState state, final String[] args) throws Throwable {
        CheckBoxComponent component = (CheckBoxComponent) viewDefinitionState
                .getComponentByReference("includeOrdersForComponent");

        if (!component.isChecked()) {
            pjp.proceed();

            return;
        }

        FormComponent orderForm = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity order = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER)
                .get(orderForm.getEntity().getId());

        Date startTimeOrder = findCalculatedStartAllOrders(order);

        FieldComponent startTimeField = (FieldComponent) viewDefinitionState.getComponentByReference(L_START_TIME);

        FieldComponent generatedEndDateField = (FieldComponent) viewDefinitionState
                .getComponentByReference("calculatedFinishAllOrders");
        FieldComponent stopTimeField = (FieldComponent) viewDefinitionState.getComponentByReference(L_STOP_TIME);

        startTimeField.setFieldValue(operationWorkTimeService.setDateToField(startTimeOrder));
        stopTimeField.setFieldValue(generatedEndDateField.getFieldValue());

        state.performEvent(viewDefinitionState, "save");
    }

    @Pointcut("execution(public void com.sleektiv.mes.productionScheduling.listeners.OperationDurationDetailsInOrderListeners.generateRealizationTime(..)) "
            + "&& args(viewDefinitionState, state, args)")
    public void generateRealizationTimeE(final ViewDefinitionState viewDefinitionState, final ComponentState state,
                                         final String[] args) {
    }

    @Around("generateRealizationTimeE(viewDefinitionState, state, args)")
    public void aroundGenerateRealizationTime(final ProceedingJoinPoint pjp,
                                              final ViewDefinitionState viewDefinitionState,
                                              final ComponentState state, final String[] args) throws Throwable {
        CheckBoxComponent component = (CheckBoxComponent) viewDefinitionState
                .getComponentByReference("includeOrdersForComponent");

        if (!component.isChecked()) {
            pjp.proceed();

            return;
        }

        FormComponent orderForm = (FormComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent startTimeField = (FieldComponent) viewDefinitionState.getComponentByReference(L_START_TIME);

        if (!StringUtils.hasText((String) startTimeField.getFieldValue())) {
            startTimeField.addMessage(L_PRODUCTION_SCHEDULING_ERROR_FIELD_REQUIRED, ComponentState.MessageType.FAILURE);
            return;
        }
        LookupComponent productionLineLookup = (LookupComponent) viewDefinitionState
                .getComponentByReference(OrderFields.PRODUCTION_LINE);
        if (productionLineLookup.isEmpty()) {
            productionLineLookup.addMessage(L_PRODUCTION_SCHEDULING_ERROR_FIELD_REQUIRED, ComponentState.MessageType.FAILURE);
            return;
        }

        FieldComponent generatedEndDateField = (FieldComponent) viewDefinitionState
                .getComponentByReference(OrderFieldsPS.GENERATED_END_DATE);
        FieldComponent includeTpzField = (FieldComponent) viewDefinitionState.getComponentByReference(OrderFieldsPS.INCLUDE_TPZ);
        FieldComponent includeAdditionalTimeField = (FieldComponent) viewDefinitionState
                .getComponentByReference(OrderFieldsPS.INCLUDE_ADDITIONAL_TIME);

        boolean isGenerated = false;

        Entity productionLine = dataDefinitionService
                .get(ProductionLinesConstants.PLUGIN_IDENTIFIER, ProductionLinesConstants.MODEL_PRODUCTION_LINE)
                .get((Long) productionLineLookup.getFieldValue());

        Entity order = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER)
                .get(orderForm.getEntity().getId());

        Entity technology = order.getBelongsToField(OrderFields.TECHNOLOGY);
        Validate.notNull(technology, "technology is null");

        // Included in work time
        boolean includeTpz = "1".equals(includeTpzField.getFieldValue());
        boolean includeAdditionalTime = "1".equals(includeAdditionalTimeField.getFieldValue());

        operationWorkTimeService.deleteOperCompTimeCalculations(order);

        OperationWorkTime workTime = orderRealizationTimeService.estimateTotalWorkTimeForOrder(order, includeTpz,
                includeAdditionalTime, true);

        List<Entity> orders = getOrderAndSubOrders(order.getId());

        Map<Integer, OrdersByLevel> ordersByLevel = productionSchedulingForComponentsService.mapToOrdersByLevel(orders);

        List<Integer> keys = Lists.newArrayList(ordersByLevel.keySet());
        keys.sort(Collections.reverseOrder());

        Date orderStartDate = order.getDateField(OrderFields.START_DATE);
        Date lastDateTo = orderStartDate;

        boolean dateFromSetToFirstPossible = false;
        for (Integer key : keys) {
            OrdersByLevel ords = ordersByLevel.get(key);
            Date currentDateTo = lastDateTo;

            for (Entity o : ords.getOrders()) {
                Entity t = o.getBelongsToField(OrderFields.TECHNOLOGY);

                orderRealizationTimeService.estimateTotalWorkTimeForOrder(o, includeTpz, includeAdditionalTime, true);

                int maxPathTime = orderRealizationTimeService.estimateOperationTimeConsumption(null, o,
                        t.getTreeField(TechnologyFields.OPERATION_COMPONENTS).getRoot(), includeTpz,
                        includeAdditionalTime, true, productionLine, Optional.empty());

                if (maxPathTime > OrderRealizationTimeService.MAX_REALIZATION_TIME) {
                    state.addMessage("orders.validate.global.error.RealizationTimeIsToLong", ComponentState.MessageType.FAILURE);

                    if (o.getId().equals(order.getId())) {
                        generatedEndDateField.setFieldValue(null);
                    }
                } else {
                    o.setField(OrderFieldsPS.REALIZATION_TIME, maxPathTime);

                    Date startTime = o.getDateField(OrderFields.DATE_FROM);
                    if (Objects.isNull(startTime)) {
                        startTime = orderStartDate;
                    }

                    if (startTime == null) {
                        startTimeField.addMessage("orders.validate.global.error.dateFromIsNull",
                                ComponentState.MessageType.FAILURE);
                    } else {
                        if (maxPathTime == 0) {
                            orderForm.addMessage("productionScheduling.timenorms.isZero", ComponentState.MessageType.FAILURE,
                                    false);

                            if (o.getId().equals(order.getId())) {
                                generatedEndDateField.setFieldValue(null);
                            }
                        } else {

                            lastDateTo = scheduleOperationsInOrder(o, currentDateTo);

                            isGenerated = true;
                        }

                        dateFromSetToFirstPossible = true;
                    }

                    o.getDataDefinition().save(o);
                }
            }
        }

        addDateFromSetToFirstPossibleMessage(orderForm, dateFromSetToFirstPossible);

        fillWorkTimeFields(viewDefinitionState, workTime);

        generatedEndDateField.requestComponentUpdateState();

        setDates(viewDefinitionState, state, orderForm, generatedEndDateField, isGenerated, order);
    }

    private void addDateFromSetToFirstPossibleMessage(FormComponent orderForm, boolean dateFromSetToFirstPossible) {
        if (dateFromSetToFirstPossible) {
            orderForm.addMessage("orders.dateFrom.info.dateFromSetToFirstPossible", ComponentState.MessageType.INFO,
                    false);
        }
    }

    private void setDates(ViewDefinitionState viewDefinitionState, ComponentState state, FormComponent orderForm,
                          FieldComponent generatedEndDateField, boolean isGenerated, Entity order) {
        if (isGenerated) {
            order = getActualOrderWithChanges(order);
            Entity orderTimeCalculation = dataDefinitionService
                    .get(OperationTimeCalculationsConstants.PLUGIN_PRODUCTION_SCHEDULING_IDENTIFIER,
                            OperationTimeCalculationsConstants.MODEL_ORDER_TIME_CALCULATION)
                    .find().add(SearchRestrictions.belongsTo(OrderTimeCalculationFields.ORDER, order)).setMaxResults(1).uniqueResult();

            Date startTimeOrders = findCalculatedStartAllOrders(order);
            order.setField("calculatedStartAllOrders", operationWorkTimeService.setDateToField(startTimeOrders));
            FieldComponent calculatedStartAllOrdersField = (FieldComponent) viewDefinitionState
                    .getComponentByReference("calculatedStartAllOrders");
            calculatedStartAllOrdersField.setFieldValue(operationWorkTimeService.setDateToField(startTimeOrders));

            Date finishDate = orderTimeCalculation.getDateField(OrderTimeCalculationFields.EFFECTIVE_DATE_TO);
            generatedEndDateField.setFieldValue(operationWorkTimeService.setDateToField(finishDate));
            FieldComponent calculatedFinishAllOrdersField = (FieldComponent) viewDefinitionState
                    .getComponentByReference("calculatedFinishAllOrders");
            calculatedFinishAllOrdersField.setFieldValue(operationWorkTimeService.setDateToField(finishDate));

            order.setField("calculatedFinishAllOrders", operationWorkTimeService.setDateToField(finishDate));

            order = order.getDataDefinition().save(order);
            orderForm.setEntity(order);
            orderForm.addMessage("productionScheduling.info.calculationGenerated", ComponentState.MessageType.SUCCESS);
            state.performEvent(viewDefinitionState, "reset");
        }
    }

    private Date findCalculatedStartAllOrders(final Entity order) {
        List<Entity> ordersTimeCalculations = dataDefinitionService
                .get(OperationTimeCalculationsConstants.PLUGIN_PRODUCTION_SCHEDULING_IDENTIFIER, OperationTimeCalculationsConstants.MODEL_ORDER_TIME_CALCULATION)
                .find().createAlias("order", "ord", JoinType.LEFT)
                .add(SearchRestrictions.in("ord.id",
                        getOrderAndSubOrders(order.getId()).stream().map(Entity::getId).collect(Collectors.toList())))
                .list().getEntities();

        return ordersTimeCalculations.stream().map(e -> e.getDateField(OrderTimeCalculationFields.EFFECTIVE_DATE_FROM))
                .min(Comparator.naturalOrder()).get();
    }

    private void fillWorkTimeFields(final ViewDefinitionState view, final OperationWorkTime workTime) {
        FieldComponent laborWorkTimeField = (FieldComponent) view.getComponentByReference(OrderFieldsPS.LABOR_WORK_TIME);
        FieldComponent machineWorkTimeField = (FieldComponent) view.getComponentByReference(OrderFieldsPS.MACHINE_WORK_TIME);

        laborWorkTimeField.setFieldValue(workTime.getLaborWorkTime());
        machineWorkTimeField.setFieldValue(workTime.getMachineWorkTime());

        laborWorkTimeField.requestComponentUpdateState();
        machineWorkTimeField.requestComponentUpdateState();
    }

    private Entity getActualOrderWithChanges(final Entity order) {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).get(order.getId());
    }

    private Date scheduleOperationsInOrder(final Entity order, final Date currentDateTo) {
        List<Date> operationStartDates = Lists.newArrayList();
        List<Date> operationEndDates = Lists.newArrayList();

        Date orderFinishDate = currentDateTo;

        List<Entity> operCompTimeCalculations = productionSchedulingService.getOperCompTimeCalculations(order);

        for (Entity operCompTimeCalculation : operCompTimeCalculations) {
            orderFinishDate = calculateDates(order, currentDateTo, operationStartDates, operationEndDates, orderFinishDate, operCompTimeCalculation);
        }

        Entity orderTimeCalculation = dataDefinitionService
                .get(OperationTimeCalculationsConstants.PLUGIN_PRODUCTION_SCHEDULING_IDENTIFIER, OperationTimeCalculationsConstants.MODEL_ORDER_TIME_CALCULATION)
                .find().add(SearchRestrictions.belongsTo(OrderTimeCalculationFields.ORDER, order)).setMaxResults(1).uniqueResult();
        orderTimeCalculation.setField(OrderTimeCalculationFields.EFFECTIVE_DATE_FROM,
                operationStartDates.stream().min(Comparator.naturalOrder()).get());
        orderTimeCalculation.setField(OrderTimeCalculationFields.EFFECTIVE_DATE_TO,
                operationEndDates.stream().max(Comparator.naturalOrder()).get());
        orderTimeCalculation.getDataDefinition().save(orderTimeCalculation);
        order.setField(OrderFieldsPS.GENERATED_END_DATE, operationWorkTimeService
                .setDateToField(orderTimeCalculation.getDateField(OrderTimeCalculationFields.EFFECTIVE_DATE_TO)));

        return orderFinishDate;
    }

    private Date calculateDates(Entity order, Date currentDateTo, List<Date> operationStartDates,
                                List<Date> operationEndDates,
                                Date orderFinishDate, Entity operCompTimeCalculation) {
        Integer offset = operCompTimeCalculation.getIntegerField(OperCompTimeCalculationsFields.OPERATION_OFF_SET);
        Integer duration = operCompTimeCalculation
                .getIntegerField(OperCompTimeCalculationsFields.EFFECTIVE_OPERATION_REALIZATION_TIME);

        operCompTimeCalculation.setField(OperCompTimeCalculationsFields.EFFECTIVE_DATE_FROM, null);
        operCompTimeCalculation.setField(OperCompTimeCalculationsFields.EFFECTIVE_DATE_TO, null);

        if (offset == null || duration == null) {
            return orderFinishDate;
        }

        if (duration.equals(0)) {
            duration = duration + 1;
        }

        Date dateFrom = productionSchedulingService.getStartDate(order.getBelongsToField(OrderFields.PRODUCTION_LINE), currentDateTo, offset);

        if (dateFrom == null) {
            return orderFinishDate;
        }

        Date dateTo = productionSchedulingService.getFinishDate(order.getBelongsToField(OrderFields.PRODUCTION_LINE), currentDateTo, (long) offset + duration);
        Date childrenEndTime = productionSchedulingService.getChildrenMaxEndTime(order, operCompTimeCalculation.getBelongsToField(OperCompTimeCalculationsFields.TECHNOLOGY_OPERATION_COMPONENT));
        if (!Objects.isNull(childrenEndTime) && childrenEndTime.after(dateTo)) {
            dateTo = childrenEndTime;
        }

        operCompTimeCalculation.setField(OperCompTimeCalculationsFields.EFFECTIVE_DATE_FROM, dateFrom);
        operCompTimeCalculation.setField(OperCompTimeCalculationsFields.EFFECTIVE_DATE_TO, dateTo);

        operationStartDates.add(dateFrom);
        operationEndDates.add(dateTo);

        operCompTimeCalculation.getDataDefinition().save(operCompTimeCalculation);

        if (Objects.isNull(orderFinishDate) || dateTo.after(orderFinishDate)) {
            return dateTo;
        }
        return orderFinishDate;
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }

    private List<Entity> getOrderAndSubOrders(final Long orderID) {
        String sql = "SELECT o FROM #orders_order AS o WHERE o.root = :orderID OR o.id = :orderID";

        return getOrderDD().find(sql).setLong("orderID", orderID).list().getEntities();
    }

}
