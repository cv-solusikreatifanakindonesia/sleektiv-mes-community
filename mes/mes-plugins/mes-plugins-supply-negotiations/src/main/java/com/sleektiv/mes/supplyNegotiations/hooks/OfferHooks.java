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

import com.sleektiv.mes.states.service.StateChangeEntityBuilder;
import com.sleektiv.mes.supplyNegotiations.SupplyNegotiationsService;
import com.sleektiv.mes.supplyNegotiations.constants.OfferFields;
import com.sleektiv.mes.supplyNegotiations.states.constants.OfferState;
import com.sleektiv.mes.supplyNegotiations.states.constants.OfferStateChangeDescriber;
import com.sleektiv.mes.supplyNegotiations.states.constants.OfferStateStringValues;
import com.sleektiv.mes.supplyNegotiations.util.OfferPricesAndQuantities;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class OfferHooks {

    @Autowired
    private StateChangeEntityBuilder stateChangeEntityBuilder;

    @Autowired
    private OfferStateChangeDescriber describer;

    @Autowired
    private NumberService numberService;

    @Autowired
    private SupplyNegotiationsService supplyNegotiationsService;

    public void onCreate(final DataDefinition offerDD, final Entity offer) {
        setInitialState(offer);
    }

    public void onCopy(final DataDefinition offerDD, final Entity offer) {
        setInitialState(offer);
        clearStateFieldOnCopy(offer);
    }

    public void onView(final DataDefinition offerDD, final Entity offer) {
        fillCumulatedQuantityAndTotalPrice(offer);
    }

    private void setInitialState(final Entity offer) {
        stateChangeEntityBuilder.buildInitial(describer, offer, OfferState.DRAFT);
    }

    private void clearStateFieldOnCopy(final Entity offer) {
        offer.setField(OfferFields.STATE, OfferStateStringValues.DRAFT);
    }

    private void fillCumulatedQuantityAndTotalPrice(final Entity offer) {
        OfferPricesAndQuantities offerPricesAndQuantities = new OfferPricesAndQuantities(offer, numberService);

        offer.setField(OfferFields.OFFER_PRODUCTS_CUMULATED_QUANTITY, offerPricesAndQuantities.getOfferCumulatedQuantity());
        offer.setField(OfferFields.OFFER_PRODUCTS_CUMULATED_TOTAL_PRICE, offerPricesAndQuantities.getOfferTotalPrice());
    }

    public void onSave(final DataDefinition offerDD, final Entity offer) {
        checkCurrency(offer);
    }

    private void checkCurrency(final Entity offer) {
        Long offerId = offer.getId();
        Entity currency = offer.getBelongsToField(OfferFields.CURRENCY);
        List<Entity> offerProducts = offer.getHasManyField(OfferFields.OFFER_PRODUCTS);

        if (Objects.nonNull(offerId)) {
            Entity offerFromDB = supplyNegotiationsService.getOffer(offerId);
            Entity currencyFromDB = offerFromDB.getBelongsToField(OfferFields.CURRENCY);

            if (Objects.nonNull(currencyFromDB) && !currencyFromDB.getId().equals(currency.getId()) && !offerProducts.isEmpty()) {
                offer.addGlobalMessage("supplyNegotiations.offer.currencyChange.offerProductsPriceVerificationRequired", false, false);
            }
        }
    }

}
