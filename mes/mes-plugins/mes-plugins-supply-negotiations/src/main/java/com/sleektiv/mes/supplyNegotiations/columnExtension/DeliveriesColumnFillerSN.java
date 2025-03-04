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
package com.sleektiv.mes.supplyNegotiations.columnExtension;

import static com.sleektiv.mes.supplyNegotiations.constants.OfferFields.NUMBER;
import static com.sleektiv.mes.supplyNegotiations.constants.OrderedProductFieldsSN.OFFER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.print.DeliveryColumnFiller;
import com.sleektiv.mes.deliveries.print.DeliveryProduct;
import com.sleektiv.mes.deliveries.print.OrderColumnFiller;
import com.sleektiv.model.api.Entity;

@Component
public class DeliveriesColumnFillerSN implements DeliveryColumnFiller, OrderColumnFiller {

    @Autowired
    private DeliveriesService deliveriesService;

    @Override
    public Map<DeliveryProduct, Map<String, String>> getDeliveryProductsColumnValues(final List<DeliveryProduct> deliveryProducts) {
        Map<DeliveryProduct, Map<String, String>> values = new HashMap<DeliveryProduct, Map<String, String>>();

        for (DeliveryProduct deliveryProduct : deliveryProducts) {

            if (!values.containsKey(deliveryProduct)) {
                values.put(deliveryProduct, new HashMap<String, String>());
            }

            fillOfferNumber(values, deliveryProduct);
        }

        return values;
    }

    @Override
    public Map<Entity, Map<String, String>> getOrderedProductsColumnValues(final List<Entity> orderedProducts) {
        Map<Entity, Map<String, String>> values = new HashMap<Entity, Map<String, String>>();

        for (Entity orderedProduct : orderedProducts) {
            if (!values.containsKey(orderedProduct)) {
                values.put(orderedProduct, new HashMap<String, String>());
            }

            fillOfferNumber(values, orderedProduct);
        }

        return values;
    }

    private void fillOfferNumber(final Map<DeliveryProduct, Map<String, String>> values, final DeliveryProduct deliveryProduct) {
        String offerNumber = null;

        if (deliveryProduct.getDeliveredProductId() != null) {
            Entity deliveredProduct = deliveriesService.getDeliveredProduct(deliveryProduct.getDeliveredProductId());

            if (deliveredProduct == null) {
                offerNumber = "";
            } else {
                Entity offer = deliveredProduct.getBelongsToField(OFFER);

                if (offer == null) {
                    offerNumber = "";
                } else {
                    offerNumber = offer.getStringField(NUMBER);
                }
            }
        } else if (deliveryProduct.getOrderedProductId() != null) {
            Entity orderedProduct = deliveriesService.getOrderedProduct(deliveryProduct.getOrderedProductId());

            if (orderedProduct == null) {
                offerNumber = "";
            } else {
                Entity offer = orderedProduct.getBelongsToField(OFFER);

                if (offer == null) {
                    offerNumber = "";
                } else {
                    offerNumber = offer.getStringField(NUMBER);
                }
            }
        } else {
            offerNumber = "";
        }

        values.get(deliveryProduct).put("offerNumber", offerNumber);
    }

    private void fillOfferNumber(final Map<Entity, Map<String, String>> values, final Entity orderedProduct) {
        String offerNumber = null;

        if (orderedProduct == null) {
            offerNumber = "";
        } else {
            Entity offer = orderedProduct.getBelongsToField(OFFER);

            if (offer == null) {
                offerNumber = "";
            } else {
                offerNumber = offer.getStringField(NUMBER);
            }
        }

        values.get(orderedProduct).put("offerNumber", offerNumber);
    }

}
