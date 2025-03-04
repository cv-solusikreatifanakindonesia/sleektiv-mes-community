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
package com.sleektiv.mes.catNumbersInNegot.hooks;

import static com.sleektiv.mes.catNumbersInNegot.contants.RequestForQuotationProductFieldsCNIN.PRODUCT_CATALOG_NUMBER;
import static com.sleektiv.mes.supplyNegotiations.constants.RequestForQuotationFields.SUPPLIER;
import static com.sleektiv.mes.supplyNegotiations.constants.RequestForQuotationProductFields.PRODUCT;
import static com.sleektiv.mes.supplyNegotiations.constants.RequestForQuotationProductFields.REQUEST_FOR_QUOTATION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.productCatalogNumbers.ProductCatalogNumbersService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class RequestForQuotationProductHooksCNIN {

    @Autowired
    private ProductCatalogNumbersService productCatalogNumbersService;

    public void updateRequestForQuotationProductCatalogNumber(final DataDefinition requestForQuotationProductDD,
            final Entity requestForQuotationProduct) {
        Entity requestForQuotation = requestForQuotationProduct.getBelongsToField(REQUEST_FOR_QUOTATION);
        Entity supplier = requestForQuotation.getBelongsToField(SUPPLIER);

        Entity product = requestForQuotationProduct.getBelongsToField(PRODUCT);

        Entity productCatalogNumber = productCatalogNumbersService.getProductCatalogNumber(product, supplier);

        if (productCatalogNumber != null) {
            requestForQuotationProduct.setField(PRODUCT_CATALOG_NUMBER, productCatalogNumber);
        }
    }

}
