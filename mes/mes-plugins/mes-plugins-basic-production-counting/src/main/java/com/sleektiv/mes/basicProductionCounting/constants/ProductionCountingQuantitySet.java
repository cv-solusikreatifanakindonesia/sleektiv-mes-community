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
package com.sleektiv.mes.basicProductionCounting.constants;

public enum ProductionCountingQuantitySet {
    SET("01set"), INTERMEDIATE("02intermediate");

    private final String code;

    private ProductionCountingQuantitySet(final String code) {
        this.code = code;
    }

    public String getStringValue() {
        return code;
    }

    public static ProductionCountingQuantitySet parseString(final String string) {
        for (ProductionCountingQuantitySet role : values()) {
            if (role.getStringValue().equals(string)) {
                return role;
            }
        }
        throw new IllegalStateException("Unsupported ProductionCountingQuantitySet: " + string);
    }

}
