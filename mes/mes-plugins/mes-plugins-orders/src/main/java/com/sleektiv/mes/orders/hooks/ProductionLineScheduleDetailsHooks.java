package com.sleektiv.mes.orders.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.criteriaModifiers.ProductionLineScheduleOrderCriteriaModifiers;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ProductionLineScheduleDetailsHooks {

    private static final String L_ORDERS_LOOKUP = "ordersLookup";

    public void onBeforeRender(final ViewDefinitionState view) {
        setOrderLookupCriteriaModifier(view);
    }

    private void setOrderLookupCriteriaModifier(final ViewDefinitionState view) {
        FormComponent scheduleForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Long scheduleId = scheduleForm.getEntityId();
        LookupComponent orderLookup = (LookupComponent) view.getComponentByReference(L_ORDERS_LOOKUP);

        FilterValueHolder valueHolder = orderLookup.getFilterValue();
        valueHolder.put(ProductionLineScheduleOrderCriteriaModifiers.SCHEDULE_PARAMETER, scheduleId);
        orderLookup.setFilterValue(valueHolder);
    }

}
