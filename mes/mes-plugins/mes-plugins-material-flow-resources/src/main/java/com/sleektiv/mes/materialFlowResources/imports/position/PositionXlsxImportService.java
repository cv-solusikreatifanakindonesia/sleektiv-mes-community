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
package com.sleektiv.mes.materialFlowResources.imports.position;

import com.sleektiv.mes.basic.constants.PalletNumberFields;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.imports.services.XlsxImportService;
import com.sleektiv.mes.materialFlowResources.constants.DocumentFields;
import com.sleektiv.mes.materialFlowResources.constants.LocationFieldsMFR;
import com.sleektiv.mes.materialFlowResources.constants.PositionFields;
import com.sleektiv.mes.materialFlowResources.constants.StorageLocationFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Service
public class PositionXlsxImportService extends XlsxImportService {

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING = "sleektivView.validate.field.error.missing";

    private static final String L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM = "sleektivView.validate.field.error.custom";

    @Autowired
    private NumberService numberService;

    @Override
    public void validateEntity(final Entity position, final DataDefinition positionDD) {
        Entity document = position.getBelongsToField(PositionFields.DOCUMENT);
        Entity locationTo = document.getBelongsToField(DocumentFields.LOCATION_TO);

        validateQuantitiesAndUnits(position, positionDD);
        validateRequiredFields(position, positionDD, locationTo);
        validateStorageLocation(position, positionDD, locationTo);
        validatePalletNumber(position, positionDD);
    }

    private void validateQuantitiesAndUnits(final Entity position, final DataDefinition positionDD) {
        BigDecimal quantity = position.getDecimalField(PositionFields.QUANTITY);
        BigDecimal givenQuantity = position.getDecimalField(PositionFields.GIVEN_QUANTITY);
        BigDecimal conversion = position.getDecimalField(PositionFields.CONVERSION);
        Entity product = position.getBelongsToField(PositionFields.PRODUCT);

        if (Objects.nonNull(product)) {
            String unit = product.getStringField(ProductFields.UNIT);
            String additionalUnit = product.getStringField(ProductFields.ADDITIONAL_UNIT);

            boolean quantitiesNonNull = Objects.nonNull(givenQuantity) && Objects.nonNull(conversion);

            if (Objects.nonNull(additionalUnit)) {
                position.setField(PositionFields.GIVEN_UNIT, additionalUnit);

                if (Objects.isNull(givenQuantity)) {
                    position.addError(positionDD.getField(PositionFields.GIVEN_QUANTITY),
                            L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
                }
                if (Objects.isNull(conversion)) {
                    position.addError(positionDD.getField(PositionFields.CONVERSION), L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
                }

                if (quantitiesNonNull) {
                    BigDecimal multipliedQuantity = quantity.multiply(conversion, numberService.getMathContext());

                    if (multipliedQuantity.compareTo(givenQuantity) != 0) {
                        position.addError(positionDD.getField(PositionFields.GIVEN_QUANTITY),
                                L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM);
                    }
                }
            } else {
                position.setField(PositionFields.GIVEN_QUANTITY, quantity);
                position.setField(PositionFields.GIVEN_UNIT, unit);
                position.setField(PositionFields.CONVERSION, BigDecimal.ONE);
            }
        }
    }

    private void validateRequiredFields(final Entity position, final DataDefinition positionDD, final Entity locationTo) {
        BigDecimal price = position.getDecimalField(PositionFields.PRICE);
        Entity batch = position.getBelongsToField(PositionFields.BATCH);
        Date productionDate = position.getDateField(PositionFields.PRODUCTION_DATE);
        Date expirationDate = position.getDateField(PositionFields.EXPIRATION_DATE);

        if (Objects.nonNull(locationTo)) {
            boolean requirePrice = locationTo.getBooleanField(LocationFieldsMFR.REQUIRE_PRICE);
            boolean requireBatch = locationTo.getBooleanField(LocationFieldsMFR.REQUIRE_BATCH);
            boolean requireProductionDate = locationTo.getBooleanField(LocationFieldsMFR.REQUIRE_PRODUCTION_DATE);
            boolean requireExpirationDate = locationTo.getBooleanField(LocationFieldsMFR.REQUIRE_EXPIRATION_DATE);

            if (requirePrice && Objects.isNull(price)) {
                position.addError(positionDD.getField(PositionFields.PRICE), L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
            }
            if (requireBatch && Objects.isNull(batch)) {
                position.addError(positionDD.getField(PositionFields.BATCH), L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
            }
            if (requireProductionDate && Objects.isNull(productionDate)) {
                position.addError(positionDD.getField(PositionFields.PRODUCTION_DATE),
                        L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
            }
            if (requireExpirationDate && Objects.isNull(expirationDate)) {
                position.addError(positionDD.getField(PositionFields.EXPIRATION_DATE),
                        L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_MISSING);
            }
        }
    }

    private void validateStorageLocation(final Entity position, final DataDefinition positionDD, final Entity locationTo) {
        Entity storageLocation = position.getBelongsToField(PositionFields.STORAGE_LOCATION);
        Entity product = position.getBelongsToField(PositionFields.PRODUCT);

        if (Objects.nonNull(product) && Objects.nonNull(locationTo) && Objects.nonNull(storageLocation)) {
            Entity storageLocationLocation = storageLocation.getBelongsToField(StorageLocationFields.LOCATION);

            if (!storageLocation.isActive()
                    || (Objects.nonNull(storageLocationLocation) && !locationTo.equals(storageLocationLocation)))
                position.addError(positionDD.getField(PositionFields.STORAGE_LOCATION),
                        L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM);
        }
    }

    private void validatePalletNumber(final Entity position, final DataDefinition positionDD) {
        Entity palletNumber = position.getBelongsToField(PositionFields.PALLET_NUMBER);

        if (Objects.nonNull(palletNumber)) {
            Date issueDateTime = palletNumber.getDateField(PalletNumberFields.ISSUE_DATE_TIME);

            if (!palletNumber.isActive() || Objects.nonNull(issueDateTime)) {
                position.addError(positionDD.getField(PositionFields.PALLET_NUMBER), L_SLEEKTIV_VIEW_VALIDATE_FIELD_ERROR_CUSTOM);
            }
        }
    }

}
