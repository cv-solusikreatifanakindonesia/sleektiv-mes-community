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
package com.sleektiv.mes.catNumbersInDeliveries.hooks;

import com.sleektiv.mes.productCatalogNumbers.ProductCatalogNumbersService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sleektiv.mes.basic.constants.CompanyFields.NAME;
import static com.sleektiv.mes.catNumbersInDeliveries.contants.OrderedProductFieldsCNID.PRODUCT_CATALOG_NUMBER;
import static com.sleektiv.mes.deliveries.constants.DeliveryFields.SUPPLIER;
import static com.sleektiv.mes.deliveries.constants.OrderedProductFields.DELIVERY;
import static com.sleektiv.mes.deliveries.constants.OrderedProductFields.PRODUCT;
import static com.sleektiv.mes.productCatalogNumbers.constants.ProductCatalogNumberFields.CATALOG_NUMBER;

@Service
public class OrderedProductDetailsHooksCNID {

    

    @Autowired
    private ProductCatalogNumbersService productCatalogNumbersService;

    public void setCatalogProductNumber(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity orderedProduct = form.getEntity();
        if (orderedProduct.getId() != null) {
            orderedProduct = orderedProduct.getDataDefinition().get(orderedProduct.getId());
        }
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(PRODUCT);
        Entity product = productLookup.getEntity();

        if (product != null) {
            Entity delivery = orderedProduct.getBelongsToField(DELIVERY);
            Entity supplier = delivery.getBelongsToField(SUPPLIER);

            Entity productCatalogNumber = productCatalogNumbersService.getProductCatalogNumber(product, supplier);

            if (productCatalogNumber != null) {
                FieldComponent productCatalogNumberField = (FieldComponent) view.getComponentByReference(PRODUCT_CATALOG_NUMBER);
                FieldComponent supplierField = (FieldComponent) view.getComponentByReference(SUPPLIER);

                supplierField.setFieldValue(supplier.getStringField(NAME));
                supplierField.requestComponentUpdateState();

                productCatalogNumberField.setFieldValue(productCatalogNumber.getStringField(CATALOG_NUMBER));
                productCatalogNumberField.requestComponentUpdateState();
            }
        }
    }

}
