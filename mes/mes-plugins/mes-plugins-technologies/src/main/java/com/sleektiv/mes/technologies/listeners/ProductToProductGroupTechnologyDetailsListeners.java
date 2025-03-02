package com.sleektiv.mes.technologies.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.technologies.constants.ProductToProductGroupFields;
import com.sleektiv.mes.technologies.hooks.ProductToProductGroupTechnologyDetailsHooks;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.LookupComponent;

@Service
public class ProductToProductGroupTechnologyDetailsListeners {

    @Autowired
    private ProductToProductGroupTechnologyDetailsHooks productToProductGroupTechnologyDetailsHooks;

    public void setCriteriaModifierParameters(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        LookupComponent productFamilyLookup = (LookupComponent) view
                .getComponentByReference(ProductToProductGroupFields.PRODUCT_FAMILY);
        LookupComponent orderProductLookup = (LookupComponent) view
                .getComponentByReference(ProductToProductGroupFields.ORDER_PRODUCT);
        productToProductGroupTechnologyDetailsHooks.toggleOrderProductEditable(productFamilyLookup, orderProductLookup);

        productToProductGroupTechnologyDetailsHooks.setCriteriaModifierParameters(productFamilyLookup, orderProductLookup);
    }
}
