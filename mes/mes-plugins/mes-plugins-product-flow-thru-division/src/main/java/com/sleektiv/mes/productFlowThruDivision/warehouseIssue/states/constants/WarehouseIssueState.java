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
package com.sleektiv.mes.productFlowThruDivision.warehouseIssue.states.constants;

import com.google.common.base.Preconditions;
import com.sleektiv.mes.states.StateEnum;

public enum WarehouseIssueState implements StateEnum {

    DRAFT(WarehouseIssueStringValues.DRAFT) {
        @Override
        public boolean canChangeTo(final StateEnum targetState) {

            return IN_PROGRESS.equals(targetState) || DISCARD.equals(targetState);

        }
    },
    IN_PROGRESS(WarehouseIssueStringValues.IN_PROGRESS) {
        @Override
        public boolean canChangeTo(final StateEnum targetState) {
            return IN_PROGRESS.equals(targetState) || COMPLETED.equals(targetState);
        }
    },
    DISCARD(WarehouseIssueStringValues.DISCARD) {
        @Override
        public boolean canChangeTo(final StateEnum targetState) {
            return false;
        }
    },
    COMPLETED(WarehouseIssueStringValues.COMPLETED) {
        @Override
        public boolean canChangeTo(final StateEnum targetState) {
            return false;
        }
    };

    private final String stringValue;

    private WarehouseIssueState(final String state) {
        this.stringValue = state;
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }

    public static WarehouseIssueState parseString(final String string) {
        WarehouseIssueState parsedStatus = null;
        for (WarehouseIssueState status : WarehouseIssueState.values()) {
            if (status.getStringValue().equals(string)) {
                parsedStatus = status;
                break;
            }
        }
        Preconditions.checkArgument(parsedStatus != null, "Couldn't parse WarehouseIssueState from string '" + string + "'");
        return parsedStatus;
    }

    public abstract boolean canChangeTo(final StateEnum targetState);
}
