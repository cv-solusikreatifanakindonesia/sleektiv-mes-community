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
package com.sleektiv.mes.orderSupplies;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.columnExtension.ColumnExtensionService;
import com.sleektiv.mes.columnExtension.constants.OperationType;
import com.sleektiv.mes.orderSupplies.constants.OrderSuppliesConstants;

@Service
public class OrderSuppliesColumnLoaderServiceImpl implements OrderSuppliesColumnLoaderService {

    private static final String L_COLUMN_FOR_COVERAGES = "columnForCoverages";

    @Autowired
    private ColumnExtensionService columnExtensionService;

    @Override
    public void fillColumnsForCoverages(final String plugin) {
        Map<Integer, Map<String, String>> columnsAttributes = columnExtensionService.getColumnsAttributesFromXML(plugin,
                L_COLUMN_FOR_COVERAGES);

        for (Map<String, String> columnAttributes : columnsAttributes.values()) {
            readData(L_COLUMN_FOR_COVERAGES, OperationType.ADD, columnAttributes);
        }
    }

    @Override
    public void clearColumnsForCoverages(final String plugin) {
        Map<Integer, Map<String, String>> columnsAttributes = columnExtensionService.getColumnsAttributesFromXML(plugin,
                L_COLUMN_FOR_COVERAGES);

        for (Map<String, String> columnAttributes : columnsAttributes.values()) {
            readData(L_COLUMN_FOR_COVERAGES, OperationType.DELETE, columnAttributes);
        }
    }

    private void readData(final String type, final OperationType operation, final Map<String, String> values) {
        if (L_COLUMN_FOR_COVERAGES.equals(type)) {
            if (OperationType.ADD.equals(operation)) {
                addColumnForCoverages(values);
            } else if (OperationType.DELETE.equals(operation)) {
                deleteColumnForCoverages(values);
            }
        }
    }

    private void addColumnForCoverages(final Map<String, String> columnAttributes) {
        columnExtensionService.addColumn(OrderSuppliesConstants.PLUGIN_IDENTIFIER,
                OrderSuppliesConstants.MODEL_COLUMN_FOR_COVERAGES, columnAttributes);
    }

    private void deleteColumnForCoverages(final Map<String, String> columnAttributes) {
        columnExtensionService.deleteColumn(OrderSuppliesConstants.PLUGIN_IDENTIFIER,
                OrderSuppliesConstants.MODEL_COLUMN_FOR_COVERAGES, columnAttributes);
    }

    public boolean isColumnsForCoveragesEmpty() {
        return columnExtensionService.isColumnsEmpty(OrderSuppliesConstants.PLUGIN_IDENTIFIER,
                OrderSuppliesConstants.MODEL_COLUMN_FOR_COVERAGES);
    }

}
