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
package com.sleektiv.mes.workPlans.pdf.document.order.column;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;

@Component("plannedEndDateOrderColumn")
public class PlannedEndDateOrderColumn extends AbstractOrderColumn {

    @Autowired
    public PlannedEndDateOrderColumn(TranslationService translationService, NumberService numberService) {
        super(translationService);
    }

    @Override
    public String getIdentifier() {
        return "plannedEndDateOrderColumn";
    }

    @Override
    public String getColumnValue(Entity order) {
        return dateTo(order) == null ? "-" : dateToFormatted(order);
    }

    private synchronized String dateToFormatted(Entity order) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.L_DATE_FORMAT, LocaleContextHolder.getLocale());
        return dateFormat.format((Date) dateTo(order));
    }

    private Object dateTo(Entity order) {
        return order.getField(OrderFields.DATE_TO);
    }

}
