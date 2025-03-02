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
package com.sleektiv.mes.technologies.listeners;

import com.sleektiv.mes.technologies.constants.ProductDataFields;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.report.api.ReportService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class ProductDatasListListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ReportService reportService;

    public void printReport(final ViewDefinitionState view, final ComponentState state, final String args[]) {
        GridComponent productDatasGrid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);

        productDatasGrid.getSelectedEntitiesIds().forEach(productDataId -> {
            Entity productData = getProductDataDD().get(productDataId);

            if (Objects.isNull(productData)) {
                state.addMessage("sleektivView.message.entityNotFound", MessageType.FAILURE);
            } else if (StringUtils.hasText(productData.getStringField(ProductDataFields.FILE_NAME))) {
                StringBuilder urlBuilder = new StringBuilder();

                urlBuilder.append("/generateSavedReport/").append(TechnologiesConstants.PLUGIN_IDENTIFIER);
                urlBuilder.append("/").append(TechnologiesConstants.MODEL_PRODUCT_DATA).append(".");
                urlBuilder.append(args[0]).append("?id=").append(productDataId);

                view.redirectTo(urlBuilder.toString(), true, false);
            } else {
                state.addMessage("sleektivReport.errorMessage.documentsWasNotGenerated", MessageType.FAILURE);
            }
        });
    }

    private DataDefinition getProductDataDD() {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_PRODUCT_DATA);
    }

}
