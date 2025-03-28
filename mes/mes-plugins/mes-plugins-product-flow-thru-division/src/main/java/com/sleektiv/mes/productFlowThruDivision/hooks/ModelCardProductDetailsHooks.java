package com.sleektiv.mes.productFlowThruDivision.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.orders.criteriaModifiers.TechnologyCriteriaModifiersO;
import com.sleektiv.mes.productFlowThruDivision.constants.ModelCardProductFields;
import com.sleektiv.mes.productFlowThruDivision.criteriaModifiers.ProductCriteriaModifiersPFTD;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ModelCardProductDetailsHooks {

    private static final String L_QUANTITY_UNIT = "quantityUnit";

    public void onBeforeRender(final ViewDefinitionState view) {
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(ModelCardProductFields.PRODUCT);
        FilterValueHolder filterValueHolder = productLookup.getFilterValue();
        Entity product = productLookup.getEntity();

        String unit = "";

        if (product != null) {
            unit = product.getStringField(ProductFields.UNIT);
            LookupComponent technologyLookup = (LookupComponent) view.getComponentByReference(ModelCardProductFields.TECHNOLOGY);
            FilterValueHolder technologyFilterValueHolder = technologyLookup.getFilterValue();
            technologyFilterValueHolder.put(TechnologyCriteriaModifiersO.PRODUCT_PARAMETER, product.getId());
            technologyLookup.setFilterValue(technologyFilterValueHolder);
        }

        FieldComponent quantityUnit = (FieldComponent) view.getComponentByReference(L_QUANTITY_UNIT);
        quantityUnit.setFieldValue(unit);
        quantityUnit.requestComponentUpdateState();

        Entity modelCardProduct = ((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM)).getEntity();
        Long modelCardId = modelCardProduct.getBelongsToField(ModelCardProductFields.MODEL_CARD).getId();
        filterValueHolder.put(ProductCriteriaModifiersPFTD.L_MODEL_CARD_ID, modelCardId);

        Entity productFromDb = modelCardProduct.getBelongsToField(ModelCardProductFields.PRODUCT);
        if (productFromDb != null) {
            filterValueHolder.put(ProductCriteriaModifiersPFTD.L_PRODUCT_ID, productFromDb.getId());
        }

        productLookup.setFilterValue(filterValueHolder);
    }
}
