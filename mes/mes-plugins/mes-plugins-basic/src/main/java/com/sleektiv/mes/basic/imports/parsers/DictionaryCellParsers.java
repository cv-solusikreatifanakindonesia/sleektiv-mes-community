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
package com.sleektiv.mes.basic.imports.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.mes.basic.imports.helpers.CellParser;
import com.sleektiv.model.api.DataDefinitionService;

@Component
public class DictionaryCellParsers {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public CellParser productCategory() {
        return new DictionaryCellParser(dataDefinitionService, "categories");
    }

    public CellParser units() {
        return new DictionaryCellParser(dataDefinitionService, "units");
    }

    public CellParser typeOfPallet() {
        return new DictionaryCellParser(dataDefinitionService, "typeOfPallet");
    }

    public CellParser typeOfProducts() {
        return new DictionaryCellParser(dataDefinitionService, "typeOfProducts");
    }

}
