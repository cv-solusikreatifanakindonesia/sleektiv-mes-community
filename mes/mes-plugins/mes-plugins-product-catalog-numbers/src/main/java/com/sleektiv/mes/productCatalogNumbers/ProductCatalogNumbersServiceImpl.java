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
package com.sleektiv.mes.productCatalogNumbers;

import static com.sleektiv.mes.productCatalogNumbers.constants.ProductCatalogNumberFields.COMPANY;
import static com.sleektiv.mes.productCatalogNumbers.constants.ProductCatalogNumberFields.PRODUCT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productCatalogNumbers.constants.ProductCatalogNumbersConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class ProductCatalogNumbersServiceImpl implements ProductCatalogNumbersService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public Entity getProductCatalogNumber(final Entity product, final Entity supplier) {
        return dataDefinitionService
                .get(ProductCatalogNumbersConstants.PLUGIN_IDENTIFIER,
                        ProductCatalogNumbersConstants.MODEL_PRODUCT_CATALOG_NUMBERS).find()
                .add(SearchRestrictions.belongsTo(PRODUCT, product)).add(SearchRestrictions.belongsTo(COMPANY, supplier))
                .setMaxResults(1).uniqueResult();
    }

}
