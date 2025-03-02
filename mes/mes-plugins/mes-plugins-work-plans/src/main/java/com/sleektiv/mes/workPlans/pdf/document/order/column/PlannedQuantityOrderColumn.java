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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;

@Component("plannedQuantityOrderColumn")
public class PlannedQuantityOrderColumn extends AbstractOrderColumn {

    private NumberService numberService;

    @Autowired
    public PlannedQuantityOrderColumn(TranslationService translationService, NumberService numberService) {
        super(translationService);
        this.numberService = numberService;
    }

    @Override
    public String getIdentifier() {
        return "plannedQuantityOrderColumn";
    }

    @Override
    public String getColumnValue(Entity order) {
        return plannedQuantity(order) == null ? "-" : numberService.format(plannedQuantity(order)) + " " + productUnit(order);
    }

    private Object plannedQuantity(Entity order) {
        return order.getField(OrderFields.PLANNED_QUANTITY);
    }

    private String productUnit(Entity order) {
        return order.getBelongsToField(OrderFields.PRODUCT).getStringField(ProductFields.UNIT);
    }

}
