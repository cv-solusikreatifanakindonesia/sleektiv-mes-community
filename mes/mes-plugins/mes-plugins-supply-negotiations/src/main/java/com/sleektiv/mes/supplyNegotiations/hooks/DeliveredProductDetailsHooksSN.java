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
package com.sleektiv.mes.supplyNegotiations.hooks;

import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.constants.DeliveredProductFields;
import com.sleektiv.mes.deliveries.constants.DeliveryFields;
import com.sleektiv.mes.supplyNegotiations.SupplyNegotiationsService;
import com.sleektiv.mes.supplyNegotiations.constants.OfferProductFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class DeliveredProductDetailsHooksSN {


    @Autowired
    private SupplyNegotiationsService supplyNegotiationsService;

    @Autowired
    private DeliveriesService deliveriesService;

    public void fillPricePerUnitAndOffer(final ViewDefinitionState view) {
        FormComponent deliveredProductForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        LookupComponent productLookup = (LookupComponent) view.getComponentByReference(OfferProductFields.PRODUCT);

        Entity deliveredProduct = deliveredProductForm.getEntity();
        Entity product = productLookup.getEntity();

        Entity delivery = deliveredProduct.getBelongsToField(DeliveredProductFields.DELIVERY);

        if (Objects.isNull(delivery) || Objects.isNull(product)) {
            return;
        }

        Entity supplier = delivery.getBelongsToField(DeliveryFields.SUPPLIER);
        Entity currency = delivery.getBelongsToField(DeliveryFields.CURRENCY);

        if (Objects.isNull(supplier) || Objects.isNull(currency)) {
            return;
        }

        Entity offerProduct = supplyNegotiationsService.getLastOfferProduct(supplier, currency, product);

        BigDecimal pricePerUnit = null;
        BigDecimal totalPrice = null;
        Entity offer = null;

        if (Objects.nonNull(offerProduct)) {
            pricePerUnit = offerProduct.getDecimalField(OfferProductFields.PRICE_PER_UNIT);
            offer = offerProduct.getBelongsToField(OfferProductFields.OFFER);
        }

        supplyNegotiationsService.fillPriceField(view, DeliveredProductFields.PRICE_PER_UNIT, pricePerUnit);
        supplyNegotiationsService.fillPriceField(view, DeliveredProductFields.TOTAL_PRICE, totalPrice);
        deliveriesService.recalculatePriceFromPricePerUnit(view, DeliveredProductFields.DELIVERED_QUANTITY);

        supplyNegotiationsService.fillOffer(view, offer);
    }

}
