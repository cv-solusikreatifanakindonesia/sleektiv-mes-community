/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
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
package com.sleektiv.mes.materialFlowResources.criteriaModifiers;

import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.mes.materialFlowResources.constants.StorageLocationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.search.*;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductsCriteriaModifiers {

    public static final String L_LOCATION_ID = "locationId";

    private static final String L_DOT = ".";

    private static final String L_ID = "id";

    private static final String L_THIS_ID = "this.id";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void filterProducts(final SearchCriteriaBuilder searchCriteriaBuilder, final FilterValueHolder filterValueHolder) {
    }

    public void filterProductsInPosition(final SearchCriteriaBuilder searchCriteriaBuilder) {
    }

    public void showNotAssignedProducts(final SearchCriteriaBuilder searchCriteriaBuilder, final FilterValueHolder filterValue) {
        if (filterValue.has(L_LOCATION_ID)) {
            long locationId = filterValue.getLong(L_LOCATION_ID);

            SearchCriteriaBuilder subCriteria = getStorageLocationDD().findWithAlias(MaterialFlowResourcesConstants.MODEL_STORAGE_LOCATION)
                    .createAlias(StorageLocationFields.LOCATION, StorageLocationFields.LOCATION, JoinType.LEFT)
                    .createAlias(StorageLocationFields.PRODUCTS, StorageLocationFields.PRODUCTS, JoinType.INNER)
                    .add(SearchRestrictions.eqField(StorageLocationFields.PRODUCTS + L_DOT + L_ID, L_THIS_ID))
                    .add(SearchRestrictions.eq(StorageLocationFields.LOCATION + L_DOT + L_ID, locationId))
                    .setProjection(SearchProjections.id());

            searchCriteriaBuilder.add(SearchSubqueries.notExists(subCriteria));
        }
    }

    private DataDefinition getStorageLocationDD() {
        return dataDefinitionService
                .get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER, MaterialFlowResourcesConstants.MODEL_STORAGE_LOCATION);
    }

}
