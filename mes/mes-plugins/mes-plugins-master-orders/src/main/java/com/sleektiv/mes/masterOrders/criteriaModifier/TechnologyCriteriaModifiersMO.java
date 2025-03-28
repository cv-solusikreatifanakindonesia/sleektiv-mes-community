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
package com.sleektiv.mes.masterOrders.criteriaModifier;

import com.beust.jcommander.internal.Sets;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentDtoFields;
import com.sleektiv.mes.technologies.constants.ProductFieldsT;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.states.constants.TechnologyStateStringValues;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TechnologyCriteriaModifiersMO {

    public static final String PRODUCT_ID = "productId";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void showAcceptedTechnologyForComponent(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        scb.add(SearchRestrictions.eq(TechnologyFields.STATE, TechnologyStateStringValues.ACCEPTED))
                .add(SearchRestrictions.eq(TechnologyFields.ACTIVE, true));

        Set<Long> technologyIds = Sets.newHashSet();

        if (filterValue.has(PRODUCT_ID)) {
            Long productId = filterValue.getLong(PRODUCT_ID);

            Entity product = getProductDD().get(productId);

            List<Entity> operationProductInProductsDto = product.getHasManyField(ProductFieldsT.OPERATION_PRODUCT_IN_PRODUCTS_DTO);

            technologyIds = operationProductInProductsDto.stream()
                    .map(operationProductInProductDto -> operationProductInProductDto.getIntegerField(OperationProductInComponentDtoFields.TECHNOLOGY_ID).longValue())
                    .collect(Collectors.toSet());
        }

        if (technologyIds.isEmpty()) {
            scb.add(SearchRestrictions.idEq(0L));
        } else {
            scb.add(SearchRestrictions.in("id", technologyIds));
        }
    }

    private DataDefinition getProductDD() {
        return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT);
    }

}
