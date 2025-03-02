/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.deliveries.hooks;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.deliveries.DeliveriesService;
import com.sleektiv.mes.deliveries.constants.DeliveredProductFields;
import com.sleektiv.mes.deliveries.constants.DeliveryFields;
import com.sleektiv.mes.deliveries.constants.ParameterFieldsD;
import com.sleektiv.mes.deliveries.states.constants.DeliveryStateChangeDescriber;
import com.sleektiv.mes.deliveries.states.constants.DeliveryStateStringValues;
import com.sleektiv.mes.deliveries.util.DeliveryPricesAndQuantities;
import com.sleektiv.mes.states.service.StateChangeEntityBuilder;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityList;
import com.sleektiv.model.api.NumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.sleektiv.mes.deliveries.states.constants.DeliveryState.DRAFT;

@Service
public class DeliveryHooks {

    @Autowired
    private DeliveriesService deliveriesService;

    @Autowired
    private StateChangeEntityBuilder stateChangeEntityBuilder;

    @Autowired
    private DeliveryStateChangeDescriber describer;

    @Autowired
    private NumberService numberService;

    @Autowired
    private ParameterService parameterService;

    public void onCreate(final DataDefinition deliveryDD, final Entity delivery) {
        setInitialState(delivery);
        setDeliveryAddressDefaultValue(delivery);
        setDescriptionDefaultValue(delivery);
        setLocationDefaultValue(deliveryDD, delivery);
    }

    public void onCopy(final DataDefinition deliveryDD, final Entity delivery) {
        setInitialState(delivery);
        clearFieldsOnCopy(delivery);
        delivery.setField(DeliveryFields.RELEASED_FOR_PAYMENT, false);
    }

    public void onView(final DataDefinition deliveryDD, final Entity delivery) {
        fillOrderedAndDeliveredCumulatedQuantityAndCumulatedTotalPrice(delivery);
    }

    public void onSave(final DataDefinition deliveryDD, final Entity delivery) {
        setStorageLocations(delivery);
        checkCurrency(delivery);
    }

    private void setInitialState(final Entity delivery) {
        stateChangeEntityBuilder.buildInitial(describer, delivery, DRAFT);
    }

    private void checkCurrency(final Entity delivery) {
        Long deliveryId = delivery.getId();
        Entity currency = delivery.getBelongsToField(DeliveryFields.CURRENCY);
        List<Entity> orderedProducts = delivery.getHasManyField(DeliveryFields.ORDERED_PRODUCTS);

        if (Objects.nonNull(deliveryId)) {
            Entity deliveryFromDB = deliveriesService.getDelivery(deliveryId);
            Entity currencyFromDB = deliveryFromDB.getBelongsToField(DeliveryFields.CURRENCY);

            if (Objects.nonNull(currencyFromDB) && !currencyFromDB.getId().equals(currency.getId()) && !orderedProducts.isEmpty()) {
                delivery.addGlobalMessage("deliveries.delivery.currencyChange.orderedProductsPriceVerificationRequired", false, false);
            }
        }
    }

    private void clearFieldsOnCopy(final Entity delivery) {
        delivery.setField(DeliveryFields.STATE, DeliveryStateStringValues.DRAFT);
        delivery.setField(DeliveryFields.EXTERNAL_NUMBER, null);
        delivery.setField(DeliveryFields.EXTERNAL_SYNCHRONIZED, true);
    }

    private void setDeliveryAddressDefaultValue(final Entity delivery) {
        String deliveryAddress = delivery.getStringField(DeliveryFields.DELIVERY_ADDRESS);

        if (Objects.isNull(deliveryAddress)) {
            delivery.setField(DeliveryFields.DELIVERY_ADDRESS, deliveriesService.getDeliveryAddressDefaultValue());
        }
    }

    private void setDescriptionDefaultValue(final Entity delivery) {
        String description = delivery.getStringField(DeliveryFields.DESCRIPTION);

        if (Objects.isNull(description)) {
            delivery.setField(DeliveryFields.DESCRIPTION, deliveriesService.getDescriptionDefaultValue());
        }
    }

    private void fillOrderedAndDeliveredCumulatedQuantityAndCumulatedTotalPrice(final Entity delivery) {
        DeliveryPricesAndQuantities deliveryPricesAndQuantities = new DeliveryPricesAndQuantities(delivery, numberService);

        delivery.setField(DeliveryFields.ORDERED_PRODUCTS_CUMULATED_QUANTITY, deliveryPricesAndQuantities.getOrderedCumulatedQuantity());
        delivery.setField(DeliveryFields.DELIVERED_PRODUCTS_CUMULATED_QUANTITY, deliveryPricesAndQuantities.getDeliveredCumulatedQuantity());
        delivery.setField(DeliveryFields.ORDERED_PRODUCTS_CUMULATED_TOTAL_PRICE, deliveryPricesAndQuantities.getOrderedTotalPrice());
        delivery.setField(DeliveryFields.DELIVERED_PRODUCTS_CUMULATED_TOTAL_PRICE, deliveryPricesAndQuantities.getDeliveredTotalPrice());
    }

    public void setLocationDefaultValue(final DataDefinition deliveryDD, final Entity delivery) {
        Entity location = delivery.getBelongsToField(DeliveryFields.LOCATION);

        if (Objects.isNull(location)) {
            delivery.setField(DeliveryFields.LOCATION, parameterService.getParameter().getBelongsToField(DeliveryFields.LOCATION));
        }
    }

    public boolean validate(final DataDefinition deliveryDD, final Entity delivery) {
        if (Objects.isNull(delivery.getBelongsToField(DeliveryFields.SUPPLIER))
                && parameterService.getParameter().getBooleanField(ParameterFieldsD.REQUIRE_SUPPLIER_IDENTYFICATION)) {
            delivery.addError(deliveryDD.getField(DeliveryFields.SUPPLIER), "sleektivView.validate.field.error.missing");
            return false;
        }

        return true;
    }

    private void setStorageLocations(final Entity delivery) {
        Entity location = delivery.getBelongsToField(DeliveryFields.LOCATION);

        if (Objects.isNull(location)) {
            clearStorageLocations(delivery);
        } else if (Objects.nonNull(delivery.getId())) {
            Entity locationFromDb = delivery.getDataDefinition().get(delivery.getId()).getBelongsToField(DeliveryFields.LOCATION);

            if (Objects.isNull(locationFromDb) || !Objects.equals(location.getId(), locationFromDb.getId())) {
                clearStorageLocations(delivery);
            }
        }
    }

    private void clearStorageLocations(final Entity delivery) {
        EntityList deliveredProducts = delivery.getHasManyField(DeliveryFields.DELIVERED_PRODUCTS);

        if (Objects.nonNull(deliveredProducts)) {
            for (Entity deliveryProduct : deliveredProducts) {
                deliveryProduct.setField(DeliveredProductFields.STORAGE_LOCATION, null);

                deliveryProduct.getDataDefinition().save(deliveryProduct);
            }
        }
    }

}
