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
package com.sleektiv.mes.workPlans.pdf.document.operation.product.column;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentFields;
import com.sleektiv.mes.workPlans.pdf.document.operation.product.ProductDirection;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("unitOperationProductColumn")
public class UnitOperationProductColumn extends AbstractOperationProductColumn {

    private DataDefinitionService dataDefinitionService;

    @Autowired
    public UnitOperationProductColumn(TranslationService translationService, DataDefinitionService dataDefinitionService) {
        super(translationService);
        this.dataDefinitionService = dataDefinitionService;
    }

    @Override
    public String getIdentifier() {
        return "unitOperationProductColumn";
    }

    @Override
    public String getColumnValue(Entity operationProduct) {
        Entity product = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT).get(
                operationProduct.getIntegerField("productId").longValue());
        return product.getStringField(ProductFields.UNIT);
    }

    @Override
    public String getColumnValueForOrder(Entity order, Entity operationProduct) {
        return "";
    }

    @Override
    public ProductDirection[] getDirection() {
        return ProductDirection.values();
    }

    private Entity product(Entity operationProduct) {
        return operationProduct.getBelongsToField(OperationProductInComponentFields.PRODUCT);
    }

}
