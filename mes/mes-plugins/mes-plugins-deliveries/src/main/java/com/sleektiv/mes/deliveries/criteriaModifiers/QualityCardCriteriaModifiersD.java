package com.sleektiv.mes.deliveries.criteriaModifiers;

import com.sleektiv.mes.basic.constants.QualityCardFields;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

import org.springframework.stereotype.Service;

@Service
public class QualityCardCriteriaModifiersD {

    public static final String L_PRODUCT_ID = "productId";

    public void showQualityCardsForProduct(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        scb.add(SearchRestrictions.eq(QualityCardFields.STATE, "02accepted"));
        if (filterValue.has(L_PRODUCT_ID)) {
            scb.createAlias(QualityCardFields.PRODUCTS, QualityCardFields.PRODUCTS, JoinType.INNER)
                    .add(SearchRestrictions.eq(QualityCardFields.PRODUCTS + ".id", filterValue.getLong(L_PRODUCT_ID)));
        } else {
            scb.add(SearchRestrictions.idEq(-1L));
        }
    }
}
