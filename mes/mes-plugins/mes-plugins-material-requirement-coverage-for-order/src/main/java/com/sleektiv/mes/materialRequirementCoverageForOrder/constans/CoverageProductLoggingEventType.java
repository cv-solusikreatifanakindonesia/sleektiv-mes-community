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
package com.sleektiv.mes.materialRequirementCoverageForOrder.constans;

public enum CoverageProductLoggingEventType {
    WAREHOUSE_STATE("01warehouseState"), DELIVERY("02delivery"), OPERATION_INPUT("03operationInput"), ORDER_INPUT("04orderInput");

    private final String loggingEventType;

    private CoverageProductLoggingEventType(final String loggingEventType) {
        this.loggingEventType = loggingEventType;
    }

    public String getStringValue() {
        return loggingEventType;
    }

    public static CoverageProductLoggingEventType parseString(final String string) {
        if ("01warehouseState".equals(string)) {
            return WAREHOUSE_STATE;
        } else if ("02delivery".equals(string)) {
            return DELIVERY;
        } else if ("03operationInput".equals(string)) {
            return OPERATION_INPUT;
        } else if ("04orderInput".equals(string)) {
            return ORDER_INPUT;
        }

        throw new IllegalStateException("Unsupported coverageProductLoggingEventType: " + string);
    }

}
