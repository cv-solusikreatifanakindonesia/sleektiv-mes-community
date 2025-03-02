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
package com.sleektiv.mes.masterOrders.imports.salesPlanProduct;

import static com.sleektiv.mes.basic.imports.dtos.CellBinder.required;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.basic.imports.dtos.CellBinderRegistry;
import com.sleektiv.mes.basic.imports.helpers.CellParser;
import com.sleektiv.mes.masterOrders.constants.SalesPlanProductFields;

@Component
public class SalesPlanProductCellBinderRegistry {

    private CellBinderRegistry cellBinderRegistry = new CellBinderRegistry();

    @Autowired
    private CellParser productCellParser;

    @Autowired
    private CellParser bigDecimalCellParser;

    @PostConstruct
    private void init() {
        cellBinderRegistry.setCellBinder(required(SalesPlanProductFields.PRODUCT, productCellParser));
        cellBinderRegistry.setCellBinder(required(SalesPlanProductFields.PLANNED_QUANTITY, bigDecimalCellParser));
    }

    public CellBinderRegistry getCellBinderRegistry() {
        return cellBinderRegistry;
    }

}
