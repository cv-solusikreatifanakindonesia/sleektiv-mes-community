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
package com.sleektiv.mes.productionCounting.listeners;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.productionCounting.constants.StaffWorkTimeFields;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.stereotype.Service;

@Service
public class StaffWorkTimeDetailsListeners {

    public void calculateLaborTime(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FieldComponent startDateFieldComponent = (FieldComponent) view
                .getComponentByReference(StaffWorkTimeFields.EFFECTIVE_EXECUTION_TIME_START);
        FieldComponent endDateFieldComponent = (FieldComponent) view
                .getComponentByReference(StaffWorkTimeFields.EFFECTIVE_EXECUTION_TIME_END);
        FieldComponent laborTimeFieldComponent = (FieldComponent) view
                .getComponentByReference(StaffWorkTimeFields.LABOR_TIME);

        Date start = DateUtils.parseDate(startDateFieldComponent.getFieldValue());
        Date end = DateUtils.parseDate(endDateFieldComponent.getFieldValue());

        if(start != null && end != null) {
            if (start.before(end)) {
                Seconds seconds = Seconds.secondsBetween(new DateTime(start), new DateTime(end));
                laborTimeFieldComponent.setFieldValue(Integer.valueOf(seconds.getSeconds()));
            }
            laborTimeFieldComponent.requestComponentUpdateState();
        } else {
            laborTimeFieldComponent.setFieldValue(null);
            laborTimeFieldComponent.requestComponentUpdateState();
        }
    }
}
