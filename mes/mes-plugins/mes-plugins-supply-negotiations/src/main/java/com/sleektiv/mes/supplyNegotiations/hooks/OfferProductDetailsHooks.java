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

import com.google.common.collect.Lists;
import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.supplyNegotiations.constants.OfferProductFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sleektiv.mes.supplyNegotiations.constants.OfferProductFields.PRODUCT;

@Service
public class OfferProductDetailsHooks {

    private static final String L_QUANTITY_UNIT = "quantityUnit";

    private static final String L_TOTAL_PRICE_CURRENCY = "totalPriceCurrency";

    private static final String L_PRICE_PER_UNIT_CURRENCY = "pricePerUnitCurrency";

    @Autowired
    private DeliveriesService deliveriesService;

    public void fillUnitFields(final ViewDefinitionState view) {
        List<String> referenceNames = Lists.newArrayList(L_QUANTITY_UNIT);

        deliveriesService.fillUnitFields(view, PRODUCT, referenceNames);
    }

    public void fillCurrencyFields(final ViewDefinitionState view) {
        FormComponent offerProductForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Entity offerProduct = offerProductForm.getEntity();
        Entity offer = offerProduct.getBelongsToField(OfferProductFields.OFFER);

        List<String> referenceNames = Lists.newArrayList(L_TOTAL_PRICE_CURRENCY, L_PRICE_PER_UNIT_CURRENCY);

        deliveriesService.fillCurrencyFieldsForDelivery(view, referenceNames, offer);
    }

}
