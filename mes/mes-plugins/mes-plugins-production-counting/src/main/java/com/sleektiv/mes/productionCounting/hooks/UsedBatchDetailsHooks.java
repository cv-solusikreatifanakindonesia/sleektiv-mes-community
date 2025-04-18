package com.sleektiv.mes.productionCounting.hooks;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.productionCounting.constants.TrackingOperationProductInComponentFields;
import com.sleektiv.mes.productionCounting.constants.UsedBatchFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsedBatchDetailsHooks {

    private static final String L_UNIT = "unit";



    private static final String L_PRODUCT_ID = "productId";

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity usedBatch = form.getEntity();
        Entity trackingOperationProductInComponent = usedBatch.getBelongsToField(
                UsedBatchFields.TRACKING_OPERATION_PRODUCT_IN_COMPONENT);
        if (view.isViewAfterRedirect() && Objects.nonNull(trackingOperationProductInComponent)) {
            Entity product = trackingOperationProductInComponent
                    .getBelongsToField(TrackingOperationProductInComponentFields.PRODUCT);
            FieldComponent unitField = (FieldComponent) view.getComponentByReference(L_UNIT);
            unitField.setFieldValue(product.getStringField(ProductFields.UNIT));
            unitField.requestComponentUpdateState();

            LookupComponent batchLookup = (LookupComponent) view.getComponentByReference(UsedBatchFields.BATCH);
            FilterValueHolder batchFilterValueHolder = batchLookup.getFilterValue();
            batchFilterValueHolder.put(L_PRODUCT_ID, product.getId());
            batchLookup.setFilterValue(batchFilterValueHolder);
        }
    }

}
