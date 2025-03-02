package com.sleektiv.mes.deliveries.hooks;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFamilyElementType;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.deliveries.constants.ProductFieldsD;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class ProductDetailsHooksD {

    public static final String L_PARENT_ID = "parentId";

    public void beforeRender(final ViewDefinitionState view) {
        updateParentCompaniesCriteriaModifiersState(view);
    }

    private void updateParentCompaniesCriteriaModifiersState(final ViewDefinitionState view) {
        FormComponent productForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent parentCompanies = (GridComponent) view.getComponentByReference("parentCompanies");

        Entity product = productForm.getEntity().getDataDefinition().get(productForm.getEntityId());
        Entity parent = product.getBelongsToField(ProductFields.PARENT);

        Long parentId = null;

        if (Objects.nonNull(parent)) {
            parentId = parent.getId();
        }

        FilterValueHolder filterValueHolder = parentCompanies.getFilterValue();
        filterValueHolder.put(L_PARENT_ID, parentId);

        parentCompanies.setFilterValue(filterValueHolder);

        parentCompanies.setVisible(!ProductFamilyElementType.from(product).equals(ProductFamilyElementType.PRODUCTS_FAMILY));
    }

}
