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
package com.sleektiv.mes.supplyNegotiations.states.constants;

import com.google.common.base.Preconditions;
import com.sleektiv.mes.states.StateEnum;

public enum RequestForQuotationState implements StateEnum {

    DRAFT(RequestForQuotationStateStringValues.DRAFT) {

        @Override
        public boolean canChangeTo(final StateEnum targetState) {
            return ACCEPTED.equals(targetState) || DECLINED.equals(targetState);
        }
    },
    ACCEPTED(RequestForQuotationStateStringValues.ACCEPTED) {

        @Override
        public boolean canChangeTo(final StateEnum targetState) {
            return DECLINED.equals(targetState);
        }
    },
    DECLINED(RequestForQuotationStateStringValues.DECLINED) {

        @Override
        public boolean canChangeTo(final StateEnum targetState) {
            return false;
        }
    };

    private final String stringValue;

    private RequestForQuotationState(final String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public abstract boolean canChangeTo(final StateEnum targetState);

    public static RequestForQuotationState parseString(final String string) {
        RequestForQuotationState parsedStatus = null;
        for (RequestForQuotationState status : RequestForQuotationState.values()) {
            if (status.getStringValue().equals(string)) {
                parsedStatus = status;
                break;
            }
        }
        Preconditions.checkArgument(parsedStatus != null, "Couldn't parse '" + string + "'");
        return parsedStatus;
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }

}
