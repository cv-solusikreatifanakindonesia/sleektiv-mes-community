package com.sleektiv.mes.masterOrders.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;
import com.sleektiv.mes.orders.TechnologyServiceO;
import com.sleektiv.mes.orders.util.AdditionalUnitService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class SalesPlanProductDetailsListeners {

    @Autowired
    private TechnologyServiceO technologyServiceO;

    @Autowired
    private AdditionalUnitService additionalUnitService;

    public void fillDefaultTechnology(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(SalesPlanProductFields.PRODUCT);
        Entity product = productLookup.getEntity();
        LookupComponent technologyLookup = (LookupComponent) view.getComponentByReference(SalesPlanProductFields.TECHNOLOGY);
        if (product != null) {
            Entity defaultTechnology = technologyServiceO.getDefaultTechnology(product);
            if (defaultTechnology != null) {
                technologyLookup.setFieldValue(defaultTechnology.getId());
            } else {
                Entity productFamily = product.getBelongsToField(ProductFields.PARENT);
                if (productFamily != null) {
                    defaultTechnology = technologyServiceO.getDefaultTechnology(productFamily);
                    if (defaultTechnology != null) {
                        technologyLookup.setFieldValue(defaultTechnology.getId());
                    } else {
                        technologyLookup.setFieldValue(null);
                    }
                } else {
                    technologyLookup.setFieldValue(null);
                }
            }
        } else {
            technologyLookup.setFieldValue(null);
        }
        technologyLookup.requestComponentUpdateState();
    }

    public void onPlannedQuantityChange(final ViewDefinitionState view, final ComponentState componentState,
            final String[] args) {
        if (!additionalUnitService.isValidDecimalFieldWithoutMsg(view,
                Lists.newArrayList(SalesPlanProductFields.PLANNED_QUANTITY, SalesPlanProductFields.ORDERED_QUANTITY))) {
            return;
        }
        Entity salesPlanProduct = ((FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM)).getEntity();
        FieldComponent surplusFromPlan = (FieldComponent) view.getComponentByReference(SalesPlanProductFields.SURPLUS_FROM_PLAN);
        surplusFromPlan.setFieldValue(salesPlanProduct.getDecimalField(SalesPlanProductFields.PLANNED_QUANTITY)
                .subtract(salesPlanProduct.getDecimalField(SalesPlanProductFields.ORDERED_QUANTITY)));
        surplusFromPlan.requestComponentUpdateState();
    }
}
