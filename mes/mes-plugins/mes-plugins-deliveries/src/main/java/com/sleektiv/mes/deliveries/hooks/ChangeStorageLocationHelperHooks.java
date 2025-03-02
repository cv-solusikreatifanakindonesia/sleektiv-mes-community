package com.sleektiv.mes.deliveries.hooks;

import com.sleektiv.mes.deliveries.constants.DeliveredProductFields;
import com.sleektiv.mes.deliveries.constants.DeliveryFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ChangeStorageLocationHelperHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent deliveredProductForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity deliveredProduct = deliveredProductForm.getEntity();

        Entity delivery = deliveredProduct.getBelongsToField(DeliveredProductFields.DELIVERY);
        Entity location = delivery.getBelongsToField(DeliveryFields.LOCATION);

        if (Objects.nonNull(location)) {
            LookupComponent storageLocationLookup = (LookupComponent) view.getComponentByReference(DeliveredProductFields.STORAGE_LOCATION);

            FilterValueHolder filter = storageLocationLookup.getFilterValue();

            filter.put("location", location.getId());

            storageLocationLookup.setFilterValue(filter);
        }
    }

}
