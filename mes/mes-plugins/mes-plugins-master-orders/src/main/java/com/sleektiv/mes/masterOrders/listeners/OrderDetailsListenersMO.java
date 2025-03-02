package com.sleektiv.mes.masterOrders.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.masterOrders.constants.MasterOrderFields;
import com.sleektiv.mes.masterOrders.constants.OrderFieldsMO;
import com.sleektiv.mes.masterOrders.constants.ParameterFieldsMO;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderDetailsListenersMO {

    @Autowired
    private ParameterService parameterService;

    public void updateDescription(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        if (parameterService.getParameter().getBooleanField(ParameterFieldsMO.COPY_DESCRIPTION)) {
            FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
            Entity order = form.getEntity();
            Entity masterOrder = order.getBelongsToField(OrderFieldsMO.MASTER_ORDER);
            if (masterOrder != null) {
                order.setField(OrderFields.DESCRIPTION, masterOrder.getStringField(MasterOrderFields.DESCRIPTION));
                form.setEntity(order);
            }
        }

    }
}
