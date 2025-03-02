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
package com.sleektiv.mes.masterOrders.validators;

import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchProjections;
import com.sleektiv.model.api.search.SearchRestrictions;
import org.springframework.stereotype.Service;

@Service
public class SalesPlanProductValidators {

    public boolean onValidate(final DataDefinition masterOrderProductDD, final Entity masterOrderProduct) {

        return checkIfEntityAlreadyExistsForProductAndSalesPlan(masterOrderProductDD, masterOrderProduct);
    }

    private boolean checkIfEntityAlreadyExistsForProductAndSalesPlan(final DataDefinition salesPlanDD,
                                                                     final Entity salesPlanProduct) {
        SearchCriteriaBuilder searchCriteriaBuilder = salesPlanDD.find()
                .add(SearchRestrictions.belongsTo(SalesPlanProductFields.SALES_PLAN,
                        salesPlanProduct.getBelongsToField(SalesPlanProductFields.SALES_PLAN)))
                .add(SearchRestrictions.belongsTo(SalesPlanProductFields.PRODUCT,
                        salesPlanProduct.getBelongsToField(SalesPlanProductFields.PRODUCT)));

        // It decreases unnecessary mapping overhead
        searchCriteriaBuilder.setProjection(SearchProjections.alias(SearchProjections.id(), "id"));

        Long salesPlanProductId = salesPlanProduct.getId();

        if (salesPlanProductId != null) {
            searchCriteriaBuilder.add(SearchRestrictions.ne("id", salesPlanProductId));
        }

        if (searchCriteriaBuilder.setMaxResults(1).uniqueResult() == null) {
            return true;
        }

        salesPlanProduct.addError(salesPlanDD.getField(SalesPlanProductFields.PRODUCT),
                "masterOrders.salesPlanProduct.alreadyExistsForProductAndSalesPlan");

        return false;
    }

}
