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
package com.sleektiv.mes.productionPerShift.listeners;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.sleektiv.commons.functional.LazyStream;
import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.basic.ShiftsService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.shift.Shift;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.dates.OrderDates;
import com.sleektiv.mes.productionPerShift.PPSHelper;
import com.sleektiv.mes.productionPerShift.PpsTimeHelper;
import com.sleektiv.mes.productionPerShift.constants.ProductionPerShiftFields;
import com.sleektiv.mes.productionPerShift.constants.ProgressForDayFields;
import com.sleektiv.mes.productionPerShift.constants.ProgressType;
import com.sleektiv.mes.productionPerShift.dataProvider.ProgressForDayDataProvider;
import com.sleektiv.mes.productionPerShift.dates.OrderRealizationDay;
import com.sleektiv.mes.productionPerShift.dates.OrderRealizationDaysResolver;
import com.sleektiv.mes.productionPerShift.domain.PpsMessage;
import com.sleektiv.mes.productionPerShift.domain.ProgressForDaysContainer;
import com.sleektiv.mes.productionPerShift.hooks.ProductionPerShiftDetailsHooks;
import com.sleektiv.mes.productionPerShift.services.AutomaticPpsExecutorService;
import com.sleektiv.mes.productionPerShift.services.AutomaticPpsParametersService;
import com.sleektiv.mes.productionPerShift.util.NonWorkingShiftsNotifier;
import com.sleektiv.mes.productionPerShift.util.ProgressPerShiftViewSaver;
import com.sleektiv.mes.productionPerShift.util.ProgressQuantitiesDeviationNotifier;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.IntegerUtils;
import com.sleektiv.model.api.utils.EntityUtils;
import com.sleektiv.model.api.validators.ErrorMessage;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.*;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ProductionPerShiftListeners {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionPerShiftListeners.class);

    private static final String VIEW_IS_INITIALIZED_CHECKBOX_REF = "viewIsInitialized";

    private static final String ORDER_LOOKUP_REF = "order";

    private static final String PROGRESS_ADL_REF = "progressForDays";

    private static final String DAY_NUMBER_INPUT_REF = "day";

    private static final String DATE_INPUT_REF = "date";



    private static final String UNIT_COMPONENT_NAME = "unit";

    private static final String DAILY_PROGRESS_ADL_REF = "dailyProgress";

    private static final Function<LookupComponent, Entity> GET_LOOKUP_ENTITY = new Function<LookupComponent, Entity>() {

        @Override
        public Entity apply(final LookupComponent lookup) {
            return lookup.getEntity();
        }
    };

    @Autowired
    private OrderRealizationDaysResolver orderRealizationDaysResolver;

    @Autowired
    private PPSHelper ppsHelper;

    @Autowired
    private ProductionPerShiftDetailsHooks detailsHooks;

    @Autowired
    private ShiftsService shiftsService;

    @Autowired
    private ProgressForDayDataProvider progressForDayDataProvider;

    @Autowired
    private ProgressPerShiftViewSaver progressPerShiftViewSaver;

    @Autowired
    private ProgressQuantitiesDeviationNotifier progressQuantitiesDeviationNotifier;

    @Autowired
    private NonWorkingShiftsNotifier nonWorkingShiftsNotifier;

    @Autowired
    private AutomaticPpsExecutorService automaticPpsExecutorService;

    @Autowired
    private PpsTimeHelper ppsTimeHelper;

    @Autowired
    private AutomaticPpsParametersService automaticPpsParametersService;

    public void generateProgressForDays(final ViewDefinitionState view, final ComponentState componentState, final String[] args) {
        if (!automaticPpsParametersService.isAutomaticPlanForShiftOn()) {
            view.addMessage(new ErrorMessage("productionPerShift.automaticAlgorithm.error.ppsOff", false));
            return;
        }
        FormComponent productionPerShiftForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        AwesomeDynamicListComponent progressForDaysComponent = (AwesomeDynamicListComponent) view
                .getComponentByReference("progressForDays");

        Entity productionPerShift = productionPerShiftForm.getPersistedEntityWithIncludedFormValues();

        ProgressForDaysContainer progressForDaysContainer = new ProgressForDaysContainer();
        try {
            automaticPpsExecutorService.generateProgressForDays(progressForDaysContainer, productionPerShift);
        } catch (Exception ex) {
            for (ErrorMessage errorMessage : progressForDaysContainer.getErrors()) {
                view.addMessage(errorMessage.getMessage(), ComponentState.MessageType.FAILURE, false, errorMessage.getVars());
            }
            if (progressForDaysContainer.getErrors().isEmpty()) {
                LOG.error("PPS generation error ", ex);
                throw new IllegalStateException();
            }
            return;
        }
        List<Entity> progressForDays = progressForDaysContainer.getProgressForDays();
        Entity order = productionPerShift.getBelongsToField(ProductionPerShiftFields.ORDER);
        Entity product = order.getBelongsToField(OrderFields.PRODUCT);

        if (progressForDaysContainer.isCalculationError()) {
            productionPerShift.getGlobalErrors().forEach(
                    error -> view.addMessage(error.getMessage(), ComponentState.MessageType.FAILURE, false, error.getVars()));
        } else if (progressForDaysContainer.isPartCalculation()) {

            productionPerShiftForm.setEntity(productionPerShift);
            String unit = null;
            if (product != null) {
                unit = product.getStringField(ProductFields.UNIT);
            }
            progressForDaysComponent.setFieldValue(progressForDays);
            fillUnit(progressForDaysComponent, unit);
            for (PpsMessage message : progressForDaysContainer.getMessages()) {
                view.addMessage(message.getMessage(), message.getType(), false, message.getVars());
            }
            updateProgressForDays(view, componentState, args);
        } else {
            Date orderFinishDate = ppsTimeHelper.calculateOrderFinishDate(order, progressForDays);

            productionPerShift.setField(ProductionPerShiftFields.ORDER_FINISH_DATE, orderFinishDate);
            productionPerShiftForm.setEntity(productionPerShift);
            String unit = null;
            if (product != null) {
                unit = product.getStringField(ProductFields.UNIT);
            }
            progressForDaysComponent.setFieldValue(progressForDays);
            fillUnit(progressForDaysComponent, unit);

            updateProgressForDays(view, componentState, args);
        }
    }

    private void fillUnit(AwesomeDynamicListComponent progressForDaysComponent, String unit) {
        for (FormComponent progressForDayForm : progressForDaysComponent.getFormComponents()) {
            AwesomeDynamicListComponent dailyProgressADL = (AwesomeDynamicListComponent) progressForDayForm
                    .findFieldComponentByName(DAILY_PROGRESS_ADL_REF);
            for (FormComponent dailyProgressForm : dailyProgressADL.getFormComponents()) {
                FieldComponent unitField = dailyProgressForm.findFieldComponentByName(UNIT_COMPONENT_NAME);
                unitField.setFieldValue(unit);
                unitField.requestComponentUpdateState();
            }
            dailyProgressADL.requestComponentUpdateState();
        }
        progressForDaysComponent.requestComponentUpdateState();
    }

    public void onTechnologyOperationChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        detailsHooks.setProductAndFillProgressForDays(view);
    }

    public void savePlan(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        boolean saveWasSuccessful = progressPerShiftViewSaver.save(view);
        if (saveWasSuccessful) {
            FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
            form.addMessage("sleektivView.message.saveMessage", ComponentState.MessageType.SUCCESS);
            detailsHooks.setProductAndFillProgressForDays(view);
            showNotifications(view);
        }
    }

    private void showNotifications(final ViewDefinitionState view) {
        FormComponent productionPerShiftForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity pps = productionPerShiftForm.getPersistedEntityWithIncludedFormValues();
        for (Entity order : getEntityFromLookup(view, ORDER_LOOKUP_REF).asSet()) {
            progressQuantitiesDeviationNotifier.compareAndNotify(view, pps, detailsHooks.isCorrectedPlan(view));
            for (OrderDates orderDates : OrderDates.of(order).map(Collections::singleton).orElse(Collections.emptySet())) {
                nonWorkingShiftsNotifier.checkAndNotify(view, orderDates.getStart().effectiveWithFallback(), pps,
                        detailsHooks.resolveProgressType(view));
            }
        }

    }

    public void refreshView(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        markViewAsUninitialized(view);
        Optional<FormComponent> maybeForm = view.tryFindComponentByReference(SleektivViewConstants.L_FORM);
        for (FormComponent form : maybeForm.asSet()) {
            form.performEvent(view, "reset");
        }
    }

    private void markViewAsUninitialized(final ViewDefinitionState view) {
        Optional<CheckBoxComponent> maybeCheckbox = view.tryFindComponentByReference(VIEW_IS_INITIALIZED_CHECKBOX_REF);
        for (CheckBoxComponent checkbox : maybeCheckbox.asSet()) {
            checkbox.setChecked(false);
            checkbox.requestComponentUpdateState();
        }
    }

    private Optional<Entity> getEntityFromLookup(final ViewDefinitionState view, final String referenceName) {
        return Optional.fromNullable((LookupComponent) view.getComponentByReference(referenceName)).transform(GET_LOOKUP_ENTITY);
    }

    public void changeView(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        detailsHooks.disableReasonOfCorrection(view);
        detailsHooks.setProductAndFillProgressForDays(view);
        showNotifications(view);
    }

    public void copyFromPlanned(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent productionPerShiftForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity pps = productionPerShiftForm.getPersistedEntityWithIncludedFormValues();
        copyPlannedProgressesAndMarkAsCorrected(pps);
        detailsHooks.setProductAndFillProgressForDays(view);
    }

    @Transactional
    private void copyPlannedProgressesAndMarkAsCorrected(final Entity pps) {
        progressForDayDataProvider.delete(FluentIterable.from(progressForDayDataProvider.findForPps(pps, ProgressType.CORRECTED))
                .transform(EntityUtils.getIdExtractor()));
        for (Entity progressForDay : progressForDayDataProvider.findForPps(pps, ProgressType.PLANNED)) {
            for (Entity progressForDayCopy : progressForDay.getDataDefinition().copy(progressForDay.getId())) {
                progressForDayCopy.setField(ProgressForDayFields.CORRECTED, true);
                save(progressForDayCopy);
            }
        }
    }

    private Entity save(final Entity entity) {
        return entity.getDataDefinition().save(entity);
    }

    public void deleteProgressForDays(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent productionPerShiftForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity pps = productionPerShiftForm.getPersistedEntityWithIncludedFormValues();
        deleteCorrectedProgressForDays(pps, detailsHooks.resolveProgressType(view));
        detailsHooks.setProductAndFillProgressForDays(view);

    }

    private void deleteCorrectedProgressForDays(final Entity pps, final ProgressType progressType) {
        List<Entity> correctedProgresses = progressForDayDataProvider.findForPps(pps, progressType);
        Iterable<Long> correctedProgressIds = FluentIterable.from(correctedProgresses).transform(EntityUtils.getIdExtractor());
        progressForDayDataProvider.delete(correctedProgressIds);
    }

    public void updateProgressForDays(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        ProgressType progressType = detailsHooks.resolveProgressType(view);
        Entity order = getEntityFromLookup(view, ORDER_LOOKUP_REF).get();
        Optional<OrderDates> maybeOrderDates = resolveOrderDates(order);
        if (!maybeOrderDates.isPresent()) {
            return;
        }

        int lastDay = -1;
        List<Shift> shifts = shiftsService.findAll(order.getBelongsToField(OrderFields.PRODUCTION_LINE));

        LazyStream<OrderRealizationDay> realizationDaysStream = orderRealizationDaysResolver.asStreamFrom(
                progressType.extractStartDateTimeFrom(maybeOrderDates.get()), shifts);
        AwesomeDynamicListComponent progressForDaysADL = (AwesomeDynamicListComponent) view
                .getComponentByReference(PROGRESS_ADL_REF);
        for (FormComponent progressForDayForm : progressForDaysADL.getFormComponents()) {
            FieldComponent dayField = progressForDayForm.findFieldComponentByName(DAY_NUMBER_INPUT_REF);
            Integer dayNum = IntegerUtils.parse((String) dayField.getFieldValue());

            if (dayNum == null) {
                final int maxDayNum = lastDay;
                if (realizationDaysStream != null) {
                    realizationDaysStream = realizationDaysStream.dropWhile(new Predicate<OrderRealizationDay>() {

                        @Override
                        public boolean apply(final OrderRealizationDay input) {
                            return input.getRealizationDayNumber() > maxDayNum;
                        }
                    });
                    OrderRealizationDay realizationDay = realizationDaysStream.head();
                    setUpProgressForDayRow(progressForDayForm, realizationDay);
                    lastDay = realizationDay.getRealizationDayNumber();
                }
            } else {
                lastDay = dayNum;
            }
        }
    }

    private Optional<OrderDates> resolveOrderDates(final Entity order) {
        Date plannedStart = order.getDateField(OrderFields.DATE_FROM);
        if (plannedStart == null) {
            return Optional.absent();
        }
        // Order realization end time is not required, thus I've passed some arbitrary values here.
        return Optional.of(OrderDates.of(order, new DateTime(plannedStart), new DateTime(plannedStart).plusWeeks(1)));
    }

    private void setUpProgressForDayRow(final FormComponent progressForDayRowForm, final OrderRealizationDay realizationDay) {
        FieldComponent dayField = progressForDayRowForm.findFieldComponentByName(DAY_NUMBER_INPUT_REF);
        FieldComponent dateField = progressForDayRowForm.findFieldComponentByName(DATE_INPUT_REF);
        dayField.setFieldValue(realizationDay.getRealizationDayNumber());
        dateField.setFieldValue(DateUtils.toDateString(realizationDay.getDate().toDate()));

        AwesomeDynamicListComponent dailyProgressADL = (AwesomeDynamicListComponent) progressForDayRowForm
                .findFieldComponentByName(DAILY_PROGRESS_ADL_REF);
        dailyProgressADL.setFieldValue(fillDailyProgressWithShifts(realizationDay.getWorkingShifts()));
    }

    private List<Entity> fillDailyProgressWithShifts(final List<Shift> shifts) {
        return FluentIterable.from(shifts).transform(new Function<Shift, Entity>() {

            @Override
            public Entity apply(final Shift shift) {
                return ppsHelper.createDailyProgressWithShift(shift.getEntity());
            }
        }).toList();
    }

}
