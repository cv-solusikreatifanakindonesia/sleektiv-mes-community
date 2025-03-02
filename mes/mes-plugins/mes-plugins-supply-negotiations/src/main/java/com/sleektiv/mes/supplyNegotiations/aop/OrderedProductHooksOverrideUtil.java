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
package com.sleektiv.mes.supplyNegotiations.aop;

import com.sleektiv.mes.deliveries.constants.OrderedProductFields;
import com.sleektiv.mes.supplyNegotiations.SupplyNegotiationsService;
import com.sleektiv.mes.supplyNegotiations.constants.OrderedProductFieldsSN;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class OrderedProductHooksOverrideUtil {

    @Autowired
    private SupplyNegotiationsService supplyNegotiationsService;

    @Autowired
    private NumberService numberService;

    public boolean shouldOverride(final Entity orderedProduct) {
        return Objects.nonNull(orderedProduct.getBelongsToField(OrderedProductFieldsSN.OFFER));
    }

    public void calculateOrderedProductPricePerUnit(final DataDefinition orderedProductDD, final Entity orderedProduct) {
        Entity offer = orderedProduct.getBelongsToField(OrderedProductFieldsSN.OFFER);
        Entity product = orderedProduct.getBelongsToField(OrderedProductFields.PRODUCT);

        BigDecimal quantity = orderedProduct.getDecimalField(OrderedProductFields.ORDERED_QUANTITY);
        BigDecimal pricePerUnit = supplyNegotiationsService.getPricePerUnit(offer, product);

        if (Objects.isNull(pricePerUnit)) {
            orderedProduct.setField(OrderedProductFields.PRICE_PER_UNIT, null);
            orderedProduct.setField(OrderedProductFields.TOTAL_PRICE, null);
        } else {
            if (Objects.isNull(quantity)) {
                orderedProduct.setField(OrderedProductFields.PRICE_PER_UNIT, numberService.setScaleWithDefaultMathContext(pricePerUnit));
                orderedProduct.setField(OrderedProductFields.TOTAL_PRICE, null);
            } else {
                BigDecimal totalPrice = quantity.multiply(pricePerUnit, numberService.getMathContext());

                orderedProduct.setField(OrderedProductFields.PRICE_PER_UNIT, numberService.setScaleWithDefaultMathContext(pricePerUnit));
                orderedProduct.setField(OrderedProductFields.TOTAL_PRICE, numberService.setScaleWithDefaultMathContext(totalPrice));
            }
        }
    }

}
