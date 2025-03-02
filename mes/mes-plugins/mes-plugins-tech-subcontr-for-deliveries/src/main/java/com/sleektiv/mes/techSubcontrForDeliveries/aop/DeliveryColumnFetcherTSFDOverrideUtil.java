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
package com.sleektiv.mes.techSubcontrForDeliveries.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.constants.DeliveredProductFields;
import com.sleektiv.mes.deliveries.constants.OrderedProductFields;
import com.sleektiv.mes.deliveries.print.DeliveryProduct;
import com.sleektiv.mes.techSubcontrForDeliveries.constants.DeliveredProductFieldsTSFD;
import com.sleektiv.mes.techSubcontrForDeliveries.constants.OrderedProductFieldsTSFD;
import com.sleektiv.mes.techSubcontrForDeliveries.constants.TechSubcontrForDeliveriesConstants;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.PluginStateResolver;

@Service
public class DeliveryColumnFetcherTSFDOverrideUtil {

    @Autowired
    private PluginStateResolver pluginStateResolver;

    @Autowired
    private DeliveriesService deliveriesService;

    public boolean shouldOverride() {
        return pluginStateResolver.isEnabled(TechSubcontrForDeliveriesConstants.PLUGIN_IDENTIFIER);
    }

    public boolean compareProductsAndOperation(final DeliveryProduct deliveryProduct, final Entity deliveredProduct) {
        Entity operation = getOperation(deliveryProduct);
        Entity product = getProduct(deliveryProduct);
        Entity deliveredOperation = deliveredProduct.getBelongsToField(DeliveredProductFieldsTSFD.OPERATION);

        boolean haveEqualsProduct = deliveredProduct.getBelongsToField(DeliveredProductFields.PRODUCT).getId()
                .equals(product.getId());

        return haveEqualsProduct && checkIfHaveNotOperationOrHaveTheSameOperation(operation, deliveredOperation);
    }

    private boolean checkIfHaveNotOperationOrHaveTheSameOperation(final Entity operation, final Entity deliveredOperation) {
        return (operation == null && deliveredOperation == null)
                || ((operation != null) && (deliveredOperation != null) && operation.equals(deliveredOperation));
    }

    private Entity getOperation(final DeliveryProduct deliveryProduct) {
        if (deliveryProduct.getOrderedProductId() != null) {
            Entity orderedProduct = deliveriesService.getOrderedProduct(deliveryProduct.getOrderedProductId());
            return orderedProduct.getBelongsToField(OrderedProductFieldsTSFD.OPERATION);
        } else {
            Entity deliveredProductEntity = deliveriesService.getDeliveredProduct(deliveryProduct.getDeliveredProductId());
            return deliveredProductEntity.getBelongsToField(DeliveredProductFieldsTSFD.OPERATION);
        }
    }

    private Entity getProduct(final DeliveryProduct deliveryProduct) {
        if (deliveryProduct.getOrderedProductId() != null) {
            Entity orderedProduct = deliveriesService.getOrderedProduct(deliveryProduct.getOrderedProductId());
            return orderedProduct.getBelongsToField(OrderedProductFields.PRODUCT);
        } else {
            Entity deliveredProductEntity = deliveriesService.getDeliveredProduct(deliveryProduct.getDeliveredProductId());
            return deliveredProductEntity.getBelongsToField(DeliveredProductFields.PRODUCT);
        }
    }

}
