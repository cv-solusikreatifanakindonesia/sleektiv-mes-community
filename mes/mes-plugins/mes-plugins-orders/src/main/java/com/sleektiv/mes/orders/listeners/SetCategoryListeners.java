package com.sleektiv.mes.orders.listeners;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class SetCategoryListeners {



    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void setCategory(final ViewDefinitionState view, final ComponentState state, final String[] args) throws JSONException {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        CheckBoxComponent generated = (CheckBoxComponent) view.getComponentByReference("generated");
        FieldComponent orderCategory = (FieldComponent) view.getComponentByReference("orderCategory");

        String value = (String) orderCategory.getFieldValue();
        JSONObject context = view.getJsonContext();
        Set<Long> ids = Arrays
                .stream(context.getString("window.mainTab.form.selectedEntities").replaceAll("[\\[\\]]", "").split(","))
                .map(Long::valueOf).collect(Collectors.toSet());

        ids.forEach(orderId -> {
            fillCategory(value, orderId);
        });

        generated.setChecked(true);
        view.addMessage("orders.order.setCategory.success", ComponentState.MessageType.SUCCESS);
    }

    private void fillCategory(String value, Long orderId) {
        Entity order = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).get(orderId);
        order.setField(OrderFields.ORDER_CATEGORY, value);
        order.getDataDefinition().save(order);
    }

    public void onOrderCategoryChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {

    }
}
