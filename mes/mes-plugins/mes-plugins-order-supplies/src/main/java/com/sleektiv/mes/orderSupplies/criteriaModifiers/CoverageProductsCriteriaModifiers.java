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
package com.sleektiv.mes.orderSupplies.criteriaModifiers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.orderSupplies.constants.CoverageProductGeneratedFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;

@Service
public class CoverageProductsCriteriaModifiers {

    @Autowired
    private DeliveriesService deliveriesService;

    public void restrictSuppliers(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long productId = filterValue.getLong(CoverageProductGeneratedFields.PRODUCT_ID);
        List<Long> ids = deliveriesService.getSuppliersWithIntegration(productId).stream().map(Entity::getId)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            scb.add(SearchRestrictions.idEq(0L));
        } else {
            scb.add(SearchRestrictions.in("id", ids));
        }
    }
}
