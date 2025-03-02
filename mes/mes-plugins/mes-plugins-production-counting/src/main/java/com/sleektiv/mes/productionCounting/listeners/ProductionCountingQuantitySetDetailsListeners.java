package com.sleektiv.mes.productionCounting.listeners;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.LookupComponent;

@Service
public class ProductionCountingQuantitySetDetailsListeners {

    private static final String L_PRODUCT_UNIT = "productUnit";

    public void fillUnitFields(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        Entity product = ((LookupComponent) state).getEntity();

        String unit = "";

        if (product != null) {
            unit = product.getStringField(ProductFields.UNIT);
        }

        FieldComponent field = (FieldComponent) view.getComponentByReference(L_PRODUCT_UNIT);
        field.setFieldValue(unit);
        field.requestComponentUpdateState();
    }
}
