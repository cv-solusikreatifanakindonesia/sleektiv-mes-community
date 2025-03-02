package com.sleektiv.mes.basicProductionCounting.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.mes.orders.constants.OperationalTaskFields;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class OperationalTaskDetailsListenersBPC {

    private static final String L_GRID_OPTIONS = "grid.options";

    private static final String L_FILTERS = "filters";

    public void showProductionCounting(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity operationalTask = form.getEntity();
        Entity order = operationalTask.getBelongsToField(OperationalTaskFields.ORDER);
        Entity operation = operationalTask.getBelongsToField(OperationalTaskFields.TECHNOLOGY_OPERATION_COMPONENT);

        if (Objects.isNull(order) || Objects.isNull(operation)) {
            return;
        }
        Map<String, String> filters = Maps.newHashMap();
        filters.put("nodeNumber", applyInOperator(operation.getStringField(TechnologyOperationComponentFields.NODE_NUMBER)));

        Map<String, Object> gridOptions = Maps.newHashMap();
        gridOptions.put(L_FILTERS, filters);

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put(L_GRID_OPTIONS, gridOptions);
        parameters.put("order.id", order.getId());

        String url = "/page/basicProductionCounting/detailedProductionCountingAndProgressList.html";
        view.redirectTo(url, false, true, parameters);
    }

    private String applyInOperator(final String value){
        return "[" + value + "]";
    }
}
