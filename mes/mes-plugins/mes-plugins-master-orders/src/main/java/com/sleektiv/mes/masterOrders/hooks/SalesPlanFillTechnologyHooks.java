package com.sleektiv.mes.masterOrders.hooks;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;
import com.sleektiv.mes.orders.criteriaModifiers.TechnologyCriteriaModifiersO;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class SalesPlanFillTechnologyHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        String productFamilyId = view.getJsonContext().get("window.mainTab.salesPlanProduct.gridLayout.productFamilyId")
                .toString();
        Entity productFamily = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT)
                .get(Long.parseLong(productFamilyId));

        FieldComponent productFamilyField = (FieldComponent) view.getComponentByReference("productFamily");
        productFamilyField.setFieldValue(productFamily.getStringField(ProductFields.NUMBER));
        LookupComponent technologyLookup = (LookupComponent) view.getComponentByReference(SalesPlanProductFields.TECHNOLOGY);
        FilterValueHolder technologyFilterValueHolder = technologyLookup.getFilterValue();
        technologyFilterValueHolder.put(TechnologyCriteriaModifiersO.PRODUCT_PARAMETER, productFamily.getId());
        technologyLookup.setFilterValue(technologyFilterValueHolder);
        technologyLookup.setRequired(true);
    }
}
