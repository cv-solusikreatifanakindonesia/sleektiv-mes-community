package com.sleektiv.mes.stoppage.hooks;

import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.stoppage.constants.StoppageFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StoppageForOrderFormHooks {

    private static final String L_ORDER = "order";

    private static final String L_PRODUCTION_TRACKING = "productionTracking";

    

    private static final String L_CONTEXT_KEY_PRODUCTION_TRACKING = "window.mainTab.form.productionTracking";

    private static final String L_CONTEXT_KEY_ORDER = "window.mainTab.form.order";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public final void onRibbonBeforeRender(final ViewDefinitionState view) throws JSONException {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();

        RibbonGroup actionsRibbonGroup = ribbon.getGroupByName("actions");

        RibbonActionItem addNew = actionsRibbonGroup.getItemByName("new");
        RibbonActionItem copy = actionsRibbonGroup.getItemByName("copy");
        JSONObject context = view.getJsonContext();
        if (context.has("window.mainTab.stoppage.forOrder")) {
            Entity order = dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER).get(context.getLong("window.mainTab.stoppage.forOrder"));
            if(OrderState.DECLINED.getStringValue().equals(order.getStringField(OrderFields.STATE))){
                addNew.setEnabled(false);
                addNew.requestUpdate(true);
                copy.setEnabled(false);
                copy.requestUpdate(true);
            }
        }
    }

    public final void onBeforeRender(final ViewDefinitionState view) throws JSONException {


        LookupComponent orderLookupComponent = (LookupComponent) view.getComponentByReference(L_ORDER);
        orderLookupComponent.setEnabled(false);

        if (Objects.isNull(((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM)).getEntityId())) {
            JSONObject context = view.getJsonContext();
            if (view.isViewAfterRedirect() && context.has(L_CONTEXT_KEY_ORDER)) {
                Long orderId = context.getLong(L_CONTEXT_KEY_ORDER);
                orderLookupComponent.setFieldValue(orderId);
                orderLookupComponent.requestComponentUpdateState();
                LookupComponent productionTrackingComponent = (LookupComponent) view.getComponentByReference(L_PRODUCTION_TRACKING);
                FilterValueHolder holder = productionTrackingComponent.getFilterValue();
                holder.put(StoppageFields.ORDER, orderId);
                productionTrackingComponent.setFilterValue(holder);
            }
        } else {
            LookupComponent productionTrackingComponent = (LookupComponent) view.getComponentByReference(L_PRODUCTION_TRACKING);
            Entity order = orderLookupComponent.getEntity();

            if (order != null) {
                FilterValueHolder holder = productionTrackingComponent.getFilterValue();

                holder.put(StoppageFields.ORDER, order.getId());

                productionTrackingComponent.setFilterValue(holder);
            }
        }
    }
}
