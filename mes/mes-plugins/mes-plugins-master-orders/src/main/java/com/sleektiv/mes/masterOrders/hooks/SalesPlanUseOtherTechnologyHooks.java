package com.sleektiv.mes.masterOrders.hooks;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;
import com.sleektiv.mes.orders.criteriaModifiers.TechnologyCriteriaModifiersO;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class SalesPlanUseOtherTechnologyHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        String oldTechnologyId = view.getJsonContext().get("window.mainTab.salesPlanProduct.gridLayout.oldTechnologyId").toString();
        Entity technology = dataDefinitionService
                .get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGY)
                .get(Long.parseLong(oldTechnologyId));
        Entity product = technology.getBelongsToField(TechnologyFields.PRODUCT);

        FieldComponent oldTechnologyField = (FieldComponent) view.getComponentByReference("oldTechnology");
        oldTechnologyField.setFieldValue(technology.getStringField(TechnologyFields.NUMBER));
        LookupComponent technologyLookup = (LookupComponent) view.getComponentByReference(SalesPlanProductFields.TECHNOLOGY);
        FilterValueHolder technologyFilterValueHolder = technologyLookup.getFilterValue();
        technologyFilterValueHolder.put(TechnologyCriteriaModifiersO.PRODUCT_PARAMETER, product.getId());
        technologyLookup.setFilterValue(technologyFilterValueHolder);
        technologyLookup.setRequired(true);
    }
}
