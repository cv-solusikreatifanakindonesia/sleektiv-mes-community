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
package com.sleektiv.mes.productFlowThruDivision.constants;

import org.apache.commons.lang3.StringUtils;

public enum AvailabilityOfMaterialAvailability {

    NONE("none"), PARTIAL("partial"), FULL("full");

    private final String strValue;

    private AvailabilityOfMaterialAvailability(final String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public static AvailabilityOfMaterialAvailability parseString(final String stringValue) {
        for (AvailabilityOfMaterialAvailability availabilityComponent : values()) {
            if (StringUtils.equalsIgnoreCase(stringValue, availabilityComponent.getStrValue())) {
                return availabilityComponent;
            }
        }
        throw new IllegalArgumentException(String.format("Can't parse AvailabilityOfMaterialAvailability enum instance from '%s'",
                stringValue));
    }
}
