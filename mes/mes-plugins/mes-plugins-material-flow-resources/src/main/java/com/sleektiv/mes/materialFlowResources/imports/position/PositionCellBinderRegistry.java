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
package com.sleektiv.mes.materialFlowResources.imports.position;

import com.sleektiv.mes.basic.imports.dtos.CellBinderRegistry;
import com.sleektiv.mes.basic.imports.helpers.CellParser;
import com.sleektiv.mes.basic.imports.parsers.DictionaryCellParsers;
import com.sleektiv.mes.materialFlowResources.constants.PositionFields;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.sleektiv.mes.basic.imports.dtos.CellBinder.optional;
import static com.sleektiv.mes.basic.imports.dtos.CellBinder.required;

@Component
public class PositionCellBinderRegistry {

    private CellBinderRegistry cellBinderRegistry = new CellBinderRegistry();

    @Autowired
    private CellParser productCellParser;

    @Autowired
    private CellParser bigDecimalCellParser;

    @Autowired
    private CellParser dateCellParser;

    @Autowired
    private CellParser storageLocationCellParser;

    @Autowired
    private CellParser palletNumberCellParser;

    @Autowired
    private CellParser batchCellParser;

    @Autowired
    private DictionaryCellParsers dictionaryCellParsers;

    @PostConstruct
    private void init() {
        cellBinderRegistry.setCellBinder(required(PositionFields.PRODUCT, productCellParser));
        cellBinderRegistry.setCellBinder(required(PositionFields.QUANTITY, bigDecimalCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.GIVEN_QUANTITY, bigDecimalCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.CONVERSION, bigDecimalCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.PRICE, bigDecimalCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.BATCH, PositionFields.PRODUCT, batchCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.PRODUCTION_DATE, dateCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.EXPIRATION_DATE, dateCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.STORAGE_LOCATION, storageLocationCellParser));
        cellBinderRegistry.setCellBinder(optional(PositionFields.TYPE_OF_PALLET, dictionaryCellParsers.typeOfPallet()));
        cellBinderRegistry.setCellBinder(optional(PositionFields.PALLET_NUMBER, palletNumberCellParser));
    }

    public CellBinderRegistry getCellBinderRegistry() {
        return cellBinderRegistry;
    }

}
