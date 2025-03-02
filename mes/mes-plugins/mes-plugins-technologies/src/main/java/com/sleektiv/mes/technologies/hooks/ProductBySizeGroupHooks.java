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
package com.sleektiv.mes.technologies.hooks;

import com.sleektiv.mes.basic.constants.UnitConversionItemFieldsB;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.mes.technologies.constants.ProductBySizeGroupFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.units.PossibleUnitConversions;
import com.sleektiv.model.api.units.UnitConversionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductBySizeGroupHooks {

    @Autowired
    private UnitConversionService unitConversionService;

    public boolean validatesWith(final DataDefinition productBySizeGroupDD, final Entity productBySizeGroup) {
        boolean isValid = true;
        isValid = isValid && checkIfUnitOrConverterIsCompatible(productBySizeGroupDD, productBySizeGroup);
        return isValid;
    }

    private boolean checkIfUnitOrConverterIsCompatible(final DataDefinition productBySizeGroupDD, final Entity productBySizeGroup) {
        Entity opic = productBySizeGroup.getBelongsToField(ProductBySizeGroupFields.OPERATION_PRODUCT_IN_COMPONENT);

        if(!opic.getBooleanField(OperationProductInComponentFields.VARIOUS_QUANTITIES_IN_PRODUCTS_BY_SIZE)) {

            String opicUnit = opic.getStringField(OperationProductInComponentFields.GIVEN_UNIT);
            String productBySizeGroupUnit = productBySizeGroup.getStringField(ProductBySizeGroupFields.GIVEN_UNIT);

            if(opicUnit.equals(productBySizeGroupUnit)) {
                return true;
            }

            PossibleUnitConversions unitConversions = unitConversionService.getPossibleConversions(productBySizeGroupUnit,
                    searchCriteriaBuilder -> searchCriteriaBuilder
                            .add(SearchRestrictions.belongsTo(UnitConversionItemFieldsB.PRODUCT, productBySizeGroup.getBelongsToField(ProductBySizeGroupFields.PRODUCT))));
            if (unitConversions.isDefinedFor(opicUnit)) {
                return true;
            } else {
                productBySizeGroup.addGlobalError("technologies.productBySizeGroup.error.wrongUnitOrConversion");
                return false;
            }

        }
        return true;
    }

}
