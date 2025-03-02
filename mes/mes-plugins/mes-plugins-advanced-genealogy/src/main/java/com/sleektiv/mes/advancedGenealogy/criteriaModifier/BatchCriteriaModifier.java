/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.advancedGenealogy.criteriaModifier;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.advancedGenealogy.constants.BatchFields;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class BatchCriteriaModifier {

    private static final String PRODUCT_ID_FILTER_VAL_KEY = "productId";

    public void filterByProduct(final SearchCriteriaBuilder searchCriteriaBuilder, final FilterValueHolder filterValueHolder) {
        if (filterValueHolder.has(PRODUCT_ID_FILTER_VAL_KEY)) {
            Long productId = filterValueHolder.getLong(PRODUCT_ID_FILTER_VAL_KEY);

            searchCriteriaBuilder.add(SearchRestrictions.belongsTo(BatchFields.PRODUCT, BasicConstants.PLUGIN_IDENTIFIER,
                    BasicConstants.MODEL_PRODUCT, productId));
        }
    }

    public void filterByProductAndActive(final SearchCriteriaBuilder searchCriteriaBuilder,
            final FilterValueHolder filterValueHolder) {
        searchCriteriaBuilder.add(SearchRestrictions.eq("active", true));
        if (filterValueHolder.has(PRODUCT_ID_FILTER_VAL_KEY)) {
            Long productId = filterValueHolder.getLong(PRODUCT_ID_FILTER_VAL_KEY);

            searchCriteriaBuilder.add(SearchRestrictions.belongsTo(BatchFields.PRODUCT, BasicConstants.PLUGIN_IDENTIFIER,
                    BasicConstants.MODEL_PRODUCT, productId));
        } else {
            searchCriteriaBuilder.add(SearchRestrictions.idEq(-1L));
        }
    }

    public void putProductFilterValue(final LookupComponent batchLookup, final Entity product) {
        FilterValueHolder filterValueHolder = batchLookup.getFilterValue();

        if (Objects.nonNull(product)) {
            filterValueHolder.put(BatchCriteriaModifier.PRODUCT_ID_FILTER_VAL_KEY, product.getId());
        } else {
            if (filterValueHolder.has(BatchCriteriaModifier.PRODUCT_ID_FILTER_VAL_KEY)) {
                filterValueHolder.remove(BatchCriteriaModifier.PRODUCT_ID_FILTER_VAL_KEY);
            }
        }

        batchLookup.setFilterValue(filterValueHolder);
    }

}
