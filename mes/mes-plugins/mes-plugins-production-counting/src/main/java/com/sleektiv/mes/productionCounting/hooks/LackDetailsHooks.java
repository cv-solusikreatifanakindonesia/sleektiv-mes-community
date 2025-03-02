package com.sleektiv.mes.productionCounting.hooks;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.productionCounting.constants.ProductionTrackingFields;
import com.sleektiv.mes.productionCounting.constants.TrackingOperationProductOutComponentFields;
import com.sleektiv.mes.productionCounting.states.constants.ProductionTrackingStateStringValues;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

@Service
public class LackDetailsHooks {

    public static final String L_UNIT = "unit";
    public static final String TRACKING_OPERATION_PRODUCT_OUT_COMPONENT = "trackingOperationProductOutComponent";
    public static final String L_LACK_REASONS = "lackReasons";

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity lack = form.getEntity();
        Entity tooc = lack.getBelongsToField(TRACKING_OPERATION_PRODUCT_OUT_COMPONENT);
        if(view.isViewAfterRedirect()) {
            FieldComponent unitField = (FieldComponent) view.getComponentByReference(L_UNIT);
            Entity product = tooc.getBelongsToField(TrackingOperationProductOutComponentFields.PRODUCT);
            String unit = product.getStringField(ProductFields.UNIT);
            unitField.setFieldValue(unit);
            unitField.requestComponentUpdateState();
        }

        Entity productionTracking = tooc.getBelongsToField(TrackingOperationProductOutComponentFields.PRODUCTION_TRACKING);
        if(!productionTracking.getStringField(ProductionTrackingFields.STATE).equals(ProductionTrackingStateStringValues.DRAFT)) {
            AwesomeDynamicListComponent lackReasons = (AwesomeDynamicListComponent) view.getComponentByReference(L_LACK_REASONS);
            lackReasons.setEnabled(false);
            for (FormComponent formLackReason : lackReasons.getFormComponents()) {
                formLackReason.setFormEnabled(false);
            }
        }
    }
}
