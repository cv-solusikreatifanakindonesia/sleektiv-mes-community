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
package com.sleektiv.mes.technologiesGenerator.customization.product;

import static com.sleektiv.model.api.search.SearchOrders.desc;
import static com.sleektiv.model.api.search.SearchRestrictions.belongsTo;
import static com.sleektiv.model.api.search.SearchRestrictions.eq;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;

@Service
class CustomizedProductDataProvider {

    public Optional<Entity> tryFind(final Entity product, final String numberWithSuffix) {
        SearchCriteriaBuilder scb = product.getDataDefinition().find();
        scb.add(belongsTo(ProductFields.PARENT, product));
        scb.add(eq(ProductFields.NUMBER, numberWithSuffix));
        scb.addOrder(desc("id"));
        return Optional.ofNullable(scb.setMaxResults(1).uniqueResult());
    }

}
