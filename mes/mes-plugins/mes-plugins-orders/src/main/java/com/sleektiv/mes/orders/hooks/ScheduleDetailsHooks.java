package com.sleektiv.mes.orders.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.constants.ParameterFieldsO;
import com.sleektiv.mes.orders.constants.ScheduleFields;
import com.sleektiv.mes.orders.criteriaModifiers.ScheduleOrderCriteriaModifiers;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ScheduleDetailsHooks {

    private static final String L_ORDERS_LOOKUP = "ordersLookup";

    @Autowired
    private ParameterService parameterService;

    public void onBeforeRender(final ViewDefinitionState view) {
        fillFieldsFromParameters(view);
        setOrderLookupCriteriaModifier(view);
    }

    private void setOrderLookupCriteriaModifier(final ViewDefinitionState view) {
        FormComponent scheduleForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Long scheduleId = scheduleForm.getEntityId();
        LookupComponent orderLookup = (LookupComponent) view.getComponentByReference(L_ORDERS_LOOKUP);

        FilterValueHolder valueHolder = orderLookup.getFilterValue();
        valueHolder.put(ScheduleOrderCriteriaModifiers.SCHEDULE_PARAMETER, scheduleId);
        orderLookup.setFilterValue(valueHolder);
    }

    private void fillFieldsFromParameters(ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        if (form.getEntityId() == null) {
            CheckBoxComponent isSetFieldsFromParameter = (CheckBoxComponent) view
                    .getComponentByReference(ScheduleFields.IS_SET_FIELDS_FROM_PARAMETER);
            if (isSetFieldsFromParameter.isChecked()) {
                return;
            }
            Entity parameter = parameterService.getParameter();
            fillWithProperty(ScheduleFields.SORT_ORDER, parameter.getStringField(ParameterFieldsO.SCHEDULE_SORT_ORDER), view);
            fillWithProperty(ScheduleFields.WORKSTATION_ASSIGN_CRITERION,
                    parameter.getStringField(ScheduleFields.WORKSTATION_ASSIGN_CRITERION), view);
            fillWithProperty(ScheduleFields.WORKER_ASSIGN_CRITERION,
                    parameter.getStringField(ScheduleFields.WORKER_ASSIGN_CRITERION), view);
            fillCheckboxWithProperty(ScheduleFields.ADDITIONAL_TIME_EXTENDS_OPERATION,
                    parameter.getBooleanField(ScheduleFields.ADDITIONAL_TIME_EXTENDS_OPERATION), view);
            fillCheckboxWithProperty(ScheduleFields.INCLUDE_TPZ,
                    parameter.getBooleanField(ParameterFieldsO.INCLUDE_TPZ_S), view);

            isSetFieldsFromParameter.setFieldValue(true);
            isSetFieldsFromParameter.requestComponentUpdateState();
        }
    }

    private void fillCheckboxWithProperty(String componentName, boolean propertyValue, ViewDefinitionState view) {
        CheckBoxComponent component = (CheckBoxComponent) view.getComponentByReference(componentName);
        component.setFieldValue(propertyValue);
    }

    private void fillWithProperty(String componentName, String propertyValue, ViewDefinitionState view) {
        FieldComponent component = (FieldComponent) view.getComponentByReference(componentName);
        if (propertyValue != null) {
            component.setFieldValue(propertyValue);
        }
    }

}
