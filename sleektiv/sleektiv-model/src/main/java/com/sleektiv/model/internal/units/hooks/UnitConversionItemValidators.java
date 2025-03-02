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
package com.sleektiv.model.internal.units.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.constants.UnitConversionItemFields;

@Service
public class UnitConversionItemValidators {

    public boolean validateUnits(final DataDefinition dataDefinition, final Entity unitConversionItem) {
        String unitFrom = unitConversionItem.getStringField(UnitConversionItemFields.UNIT_FROM);
        String unitTo = unitConversionItem.getStringField(UnitConversionItemFields.UNIT_TO);
        if (unitFrom.equals(unitTo)) {
            unitConversionItem.addError(dataDefinition.getField(UnitConversionItemFields.UNIT_TO),
                    "sleektivUnitConversions.unitConversionItem.validateError.theSameUnit");
            return false;
        }
        return true;
    }

}
