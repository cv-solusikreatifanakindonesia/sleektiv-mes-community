package com.sleektiv.mes.masterOrders.listeners;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.masterOrders.constants.MasterOrdersConstants;
import com.sleektiv.mes.masterOrders.constants.ProductsBySizeEntryHelperFields;
import com.sleektiv.mes.masterOrders.constants.ProductsBySizeHelperFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductsBySizeListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void changeProductFamily(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(ProductsBySizeHelperFields.PRODUCT);

        Entity product = productLookup.getEntity();

        if (Objects.nonNull(product)) {
            clearProductsList(view);
            prepareProductsList(view, product);
        } else {
            clearProductsList(view);
        }
    }

    private void clearProductsList(final ViewDefinitionState view) {
        FormComponent productsBySizeHelperForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity productBySizeHelper = getProductsBySizeHelperDD().get(productsBySizeHelperForm.getEntityId());

        for (Entity productsBySizeEntryHelper : productBySizeHelper.getHasManyField(ProductsBySizeHelperFields.PRODUCTS_BY_SIZE_ENTRY_HELPERS)) {
            productsBySizeEntryHelper.getDataDefinition().delete(productsBySizeEntryHelper.getId());
        }
    }


    private void prepareProductsList(final ViewDefinitionState view, final Entity product) {
        FormComponent productsBySizeHelperForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        List<Entity> children = product.getHasManyField(ProductFields.CHILDREN);

        for (Entity child : children) {
            if (Objects.nonNull(child.getBelongsToField(ProductFields.SIZE))) {
                Entity productsBySizeEntryHelper = getProductsBySizeEntryHelperDD().create();

                productsBySizeEntryHelper.setField(ProductsBySizeEntryHelperFields.PRODUCTS_BY_SIZE_HELPER, productsBySizeHelperForm.getEntityId());
                productsBySizeEntryHelper.setField(ProductsBySizeEntryHelperFields.PRODUCT, child.getId());

                productsBySizeEntryHelper.getDataDefinition().save(productsBySizeEntryHelper);
            }
        }
    }

    public void addPositionsToOrder(final ViewDefinitionState view, final ComponentState state, final String[] args) {

    }

    private DataDefinition getProductsBySizeHelperDD() {
        return dataDefinitionService
                .get(MasterOrdersConstants.PLUGIN_IDENTIFIER, MasterOrdersConstants.MODEL_PRODUCTS_BY_SIZE_HELPER);
    }

    private DataDefinition getProductsBySizeEntryHelperDD() {
        return dataDefinitionService
                .get(MasterOrdersConstants.PLUGIN_IDENTIFIER, MasterOrdersConstants.MODEL_PRODUCTS_BY_SIZE_ENTRY_HELPER);
    }

}
