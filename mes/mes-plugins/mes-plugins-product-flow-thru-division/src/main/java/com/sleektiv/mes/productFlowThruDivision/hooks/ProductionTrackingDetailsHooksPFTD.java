package com.sleektiv.mes.productFlowThruDivision.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.productionCounting.constants.OrderFieldsPC;
import com.sleektiv.mes.productionCounting.constants.ProductionTrackingFields;
import com.sleektiv.mes.productionCounting.states.constants.ProductionTrackingStateStringValues;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ProductionTrackingDetailsHooksPFTD {

    private static final String L_MATERIAL_FLOW = "materialFlow";

    private static final String L_COMPONENT_AVAILABILITY = "componentAvailability";

    public void onBeforeRender(final ViewDefinitionState view) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);

        RibbonGroup materialFlowRibbonGroup = (RibbonGroup) window.getRibbon().getGroupByName(L_MATERIAL_FLOW);
        RibbonActionItem componentAvailabilityRibbonActionItem = (RibbonActionItem) materialFlowRibbonGroup
                .getItemByName(L_COMPONENT_AVAILABILITY);

        FormComponent productionTrackingForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity productionTracking = productionTrackingForm.getEntity();

        boolean isDraft = ProductionTrackingStateStringValues.DRAFT
                .equals(productionTracking.getStringField(ProductionTrackingFields.STATE));

        Entity order = productionTracking.getBelongsToField(ProductionTrackingFields.ORDER);

        if (order == null) {
            return;
        }

        boolean registerQuantityInProduct = order.getBooleanField(OrderFieldsPC.REGISTER_QUANTITY_IN_PRODUCT);
        boolean registerQuantityOutProduct = order.getBooleanField(OrderFieldsPC.REGISTER_QUANTITY_OUT_PRODUCT);

        componentAvailabilityRibbonActionItem.setEnabled(isDraft && (registerQuantityInProduct || registerQuantityOutProduct));
        componentAvailabilityRibbonActionItem.requestUpdate(true);
    }

}
