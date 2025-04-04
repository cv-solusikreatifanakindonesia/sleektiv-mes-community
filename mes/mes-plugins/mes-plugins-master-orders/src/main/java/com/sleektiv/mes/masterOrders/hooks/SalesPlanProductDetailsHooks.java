package com.sleektiv.mes.masterOrders.hooks;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;
import com.sleektiv.mes.masterOrders.criteriaModifier.ProductCriteriaModifiersMO;
import com.sleektiv.mes.orders.criteriaModifiers.TechnologyCriteriaModifiersO;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class SalesPlanProductDetailsHooks {

    private static final String L_PLANNED_QUANTITY_UNIT = "plannedQuantityUnit";

    private static final String L_ORDERED_QUANTITY_UNIT = "orderedQuantityUnit";

    private static final String ORDERED_TO_WAREHOUSE_UNIT = "orderedToWarehouseUnit";

    private static final String L_SURPLUS_FROM_PLAN_UNIT = "surplusFromPlanUnit";

    public void onBeforeRender(final ViewDefinitionState view) {
        List<String> referenceNames = Lists.newArrayList(L_PLANNED_QUANTITY_UNIT, L_ORDERED_QUANTITY_UNIT, ORDERED_TO_WAREHOUSE_UNIT,
                L_SURPLUS_FROM_PLAN_UNIT);

        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(SalesPlanProductFields.PRODUCT);
        FilterValueHolder filterValueHolder = productLookup.getFilterValue();
        Entity product = productLookup.getEntity();

        String unit = "";

        if (product != null) {
            unit = product.getStringField(ProductFields.UNIT);
            LookupComponent technologyLookup = (LookupComponent) view.getComponentByReference(SalesPlanProductFields.TECHNOLOGY);
            FilterValueHolder technologyFilterValueHolder = technologyLookup.getFilterValue();
            technologyFilterValueHolder.put(TechnologyCriteriaModifiersO.PRODUCT_PARAMETER, product.getId());
            technologyLookup.setFilterValue(technologyFilterValueHolder);
        }

        for (String referenceName : referenceNames) {
            FieldComponent field = (FieldComponent) view.getComponentByReference(referenceName);
            field.setFieldValue(unit);
            field.requestComponentUpdateState();
        }

        Entity salesPlanProduct = ((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM)).getEntity();
        Long salesPlanId = salesPlanProduct.getBelongsToField(SalesPlanProductFields.SALES_PLAN).getId();
        filterValueHolder.put(ProductCriteriaModifiersMO.L_SALES_PLAN_ID, salesPlanId);

        Entity productFromDb = salesPlanProduct.getBelongsToField(SalesPlanProductFields.PRODUCT);
        if (productFromDb != null) {
            filterValueHolder.put(ProductCriteriaModifiersMO.L_PRODUCT_ID, productFromDb.getId());
        }

        productLookup.setFilterValue(filterValueHolder);
    }
}
