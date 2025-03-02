package com.sleektiv.mes.masterOrders.criteriaModifier;

import com.sleektiv.mes.basic.constants.AttributeValueFields;
import com.sleektiv.model.api.search.JoinType;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.stereotype.Service;

import static com.sleektiv.mes.masterOrders.hooks.PricesListDetailsHooks.ATTRIBUTE_1_ID;
import static com.sleektiv.mes.masterOrders.hooks.PricesListDetailsHooks.ATTRIBUTE_2_ID;

@Service
public class PricesListAttributeValueCriteriaModifiers {

    public void filter1(final SearchCriteriaBuilder scb, final FilterValueHolder filter) {
        if (filter.has(ATTRIBUTE_1_ID)) {
            scb.createAlias(AttributeValueFields.ATTRIBUTE, AttributeValueFields.ATTRIBUTE, JoinType.LEFT);

            scb.add(SearchRestrictions.eq(AttributeValueFields.ATTRIBUTE + ".id", filter.getLong(ATTRIBUTE_1_ID)));
        } else {
            scb.add(SearchRestrictions.idEq(-1L));
        }
    }

    public void filter2(final SearchCriteriaBuilder scb, final FilterValueHolder filter) {
        if (filter.has(ATTRIBUTE_2_ID)) {
            scb.createAlias(AttributeValueFields.ATTRIBUTE, AttributeValueFields.ATTRIBUTE, JoinType.LEFT);

            scb.add(SearchRestrictions.eq(AttributeValueFields.ATTRIBUTE + ".id", filter.getLong(ATTRIBUTE_2_ID)));
        } else {
            scb.add(SearchRestrictions.idEq(-1L));
        }
    }

}
