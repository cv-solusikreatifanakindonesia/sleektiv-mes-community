package com.sleektiv.mes.basic.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ModelFields;
import com.sleektiv.mes.basic.criteriaModifiers.ProductCriteriaModifiers;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ModelDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        fillCriteriaModifiers(view);
    }

    private void fillCriteriaModifiers(final ViewDefinitionState view) {
        FormComponent modelForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent assortmentLookup = (LookupComponent) view.getComponentByReference(ModelFields.ASSORTMENT);
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference("productLookup");

        Long modelId = modelForm.getEntityId();
        Entity assortment = assortmentLookup.getEntity();

        FilterValueHolder filterValueHolder = productLookup.getFilterValue();

        if (Objects.nonNull(modelId)) {
            filterValueHolder.put(ProductCriteriaModifiers.L_MODEL_ID, modelId);
        }
        if (Objects.isNull(assortment)) {
            if (filterValueHolder.has(ProductCriteriaModifiers.L_ASSORTMENT_ID)) {
                filterValueHolder.remove(ProductCriteriaModifiers.L_ASSORTMENT_ID);
            }
        } else {
            filterValueHolder.put(ProductCriteriaModifiers.L_ASSORTMENT_ID, assortment.getId());
        }

        productLookup.setFilterValue(filterValueHolder);
    }

}
