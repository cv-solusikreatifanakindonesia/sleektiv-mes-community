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

import static com.sleektiv.mes.deliveries.constants.DeliveryFields.DELIVERED_PRODUCTS;
import static com.sleektiv.mes.deliveries.constants.DeliveryFields.ORDERED_PRODUCTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.catNumbersInDeliveries.CatNumbersInDeliveriesService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class DeliveryHooksCNID {

    @Autowired
    private CatNumbersInDeliveriesService catNumbersInDeliveriesService;

    public void updateOrderedProductsCatalogNumbers(final DataDefinition deliveryDD, final Entity delivery) {
        catNumbersInDeliveriesService.updateProductsCatalogNumbers(delivery, ORDERED_PRODUCTS);
    }

    public void updateDeliveredProductsCatalogNumbers(final DataDefinition deliveryDD, final Entity delivery) {
        catNumbersInDeliveriesService.updateProductsCatalogNumbers(delivery, DELIVERED_PRODUCTS);
    }

}
