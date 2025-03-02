/*
 * **************************************************************************
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
 * **************************************************************************
 */
package com.sleektiv.mes.basic.imports.model;

import static com.sleektiv.mes.basic.imports.dtos.CellBinder.optional;
import static com.sleektiv.mes.basic.imports.dtos.CellBinder.required;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.basic.constants.ModelFields;
import com.sleektiv.mes.basic.imports.dtos.CellBinderRegistry;
import com.sleektiv.mes.basic.imports.helpers.CellParser;
import com.sleektiv.mes.basic.imports.parsers.DictionaryCellParsers;

@Component
public class ModelCellBinderRegistry {

    private CellBinderRegistry cellBinderRegistry = new CellBinderRegistry();

    @Autowired
    private DictionaryCellParsers dictionaryCellParsers;

    @Autowired
    private CellParser assortmentCellParser;

    @Autowired
    private CellParser formsCellParser;

    @Autowired
    private CellParser labelCellParser;

    @PostConstruct
    private void init() {
        cellBinderRegistry.setCellBinder(required(ModelFields.NAME));
        cellBinderRegistry.setCellBinder(optional(ModelFields.ASSORTMENT, assortmentCellParser));
        cellBinderRegistry.setCellBinder(optional(ModelFields.FORMS, formsCellParser));
        cellBinderRegistry.setCellBinder(optional(ModelFields.TYPE_OF_PRODUCT, dictionaryCellParsers.typeOfProducts()));
        cellBinderRegistry.setCellBinder(optional(ModelFields.LABEL, labelCellParser));
    }

    public CellBinderRegistry getCellBinderRegistry() {
        return cellBinderRegistry;
    }

}
