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
package com.sleektiv.mes.orders.util;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.sleektiv.commons.dateTime.DateRange;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.dates.OrderDates;
import com.sleektiv.model.api.Entity;

@Service
public final class OrderDatesServiceImpl implements OrderDatesService {

    private static final List<String> START_DATE_FIELDS = Lists.newArrayList(OrderFields.EFFECTIVE_DATE_FROM,
            OrderFields.CORRECTED_DATE_FROM, OrderFields.DATE_FROM);

    private static final List<String> FINISH_DATE_FIELDS = Lists.newArrayList(OrderFields.EFFECTIVE_DATE_TO,
            OrderFields.CORRECTED_DATE_TO, OrderFields.DATE_TO);

    @Override
    public DateRange getCalculatedDates(final Entity order) {
        return new DateRange(getStartDate(order), getFinishDate(order));
    }

    private Date getStartDate(final Entity order) {
        return findFirstNonEmptyDate(order, START_DATE_FIELDS);
    }

    private Date getFinishDate(final Entity order) {
        return findFirstNonEmptyDate(order, FINISH_DATE_FIELDS);
    }

    private Date findFirstNonEmptyDate(final Entity entity, final Iterable<String> fieldNames) {
        for (String fieldName : fieldNames) {
            Date fieldValue = entity.getDateField(fieldName);
            if (fieldValue != null) {
                return fieldValue;
            }
        }
        return null;
    }

    private Date getDate(final Entity entity, final String fieldName) {
        Date fieldValue = entity.getDateField(fieldName);
        if (fieldValue != null) {
            return fieldValue;
        }

        return null;
    }

    @Override
    public DateRange getExistingDates(Entity order) {
        return new DateRange(getDate(order, OrderFields.START_DATE), getDate(order, OrderFields.FINISH_DATE));
    }

}
