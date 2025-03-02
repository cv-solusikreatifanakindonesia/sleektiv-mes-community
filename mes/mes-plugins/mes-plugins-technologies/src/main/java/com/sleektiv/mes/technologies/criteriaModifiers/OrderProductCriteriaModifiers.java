package com.sleektiv.mes.technologies.criteriaModifiers;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class OrderProductCriteriaModifiers {

    public static final String L_PRODUCT_FAMILY_ID = "productFamilyId";

    public void showOnlyProductsBelongsToFamily(final SearchCriteriaBuilder searchCriteriaBuilder,
            final FilterValueHolder filterValue) {
        long productFamilyId = 0;

        if (filterValue.has(L_PRODUCT_FAMILY_ID)) {
            productFamilyId = filterValue.getLong(L_PRODUCT_FAMILY_ID);
        }

        searchCriteriaBuilder.add(SearchRestrictions.belongsTo(ProductFields.PARENT, BasicConstants.PLUGIN_IDENTIFIER,
                BasicConstants.MODEL_PRODUCT, productFamilyId));
    }
}
