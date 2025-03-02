package com.sleektiv.mes.masterOrders.hooks;

import com.sleektiv.mes.masterOrders.constants.OutsourceProcessingComponentHelperFields;
import com.sleektiv.mes.masterOrders.criteriaModifier.TechnologyCriteriaModifiersMO;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class OutsourceProcessingComponentHooks {

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(OutsourceProcessingComponentHelperFields.PRODUCT);
        LookupComponent technologyLookup = (LookupComponent) view.getComponentByReference(OutsourceProcessingComponentHelperFields.TECHNOLOGY);

        JSONObject context = view.getJsonContext();

        Long productId = Long.valueOf(context.getString("window.mainTab.form.gridLayout.selectedEntity").replaceAll("[\\[\\]]", ""));

        setProductLookup(productLookup, productId);
        setTechnologyLookup(technologyLookup, productId);
    }

    private void setProductLookup(final LookupComponent productLookup, final Long productId) {
        productLookup.setFieldValue(productId);
        productLookup.requestComponentUpdateState();
    }

    private void setTechnologyLookup(final LookupComponent technologyLookup, final Long productId) {
        FilterValueHolder filterValueHolder = technologyLookup.getFilterValue();

        filterValueHolder.put(TechnologyCriteriaModifiersMO.PRODUCT_ID, productId);

        technologyLookup.setFilterValue(filterValueHolder);
        technologyLookup.requestComponentUpdateState();
    }

}
