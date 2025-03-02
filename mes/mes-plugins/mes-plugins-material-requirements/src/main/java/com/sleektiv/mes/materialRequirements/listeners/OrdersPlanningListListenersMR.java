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
package com.sleektiv.mes.materialRequirements.listeners;

import com.google.common.collect.Maps;
import com.lowagie.text.DocumentException;
import com.sleektiv.mes.materialRequirements.MaterialRequirementService;
import com.sleektiv.mes.materialRequirements.constants.MaterialRequirementFields;
import com.sleektiv.mes.materialRequirements.constants.MaterialRequirementsConstants;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.util.OrderReportService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.validators.ErrorMessage;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.utils.NumberGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class OrdersPlanningListListenersMR {

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @Autowired
    private MaterialRequirementService materialRequirementService;

    @Autowired
    private OrderReportService orderReportService;

    public void printMaterialRequirementForOrder(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        Entity materialRequirement = printMaterialRequirementForOrder(state);

        if (Objects.isNull(materialRequirement)) {
            return;
        }

        try {
            materialRequirementService.generateMaterialRequirementDocuments(state, materialRequirement);

            view.redirectTo(
                    "/generateSavedReport/" + MaterialRequirementsConstants.PLUGIN_IDENTIFIER + "/"
                            + MaterialRequirementsConstants.MODEL_MATERIAL_REQUIREMENT + "." + args[0] + "?id="
                            + materialRequirement.getId(), true, false);
        } catch (IOException | DocumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Entity printMaterialRequirementForOrder(final ComponentState state) {
        Map<String, Object> entityFieldsMap = Maps.newHashMap();

        entityFieldsMap.put(MaterialRequirementFields.NUMBER, numberGeneratorService.generateNumber(MaterialRequirementsConstants.PLUGIN_IDENTIFIER,
                MaterialRequirementsConstants.MODEL_MATERIAL_REQUIREMENT));
        entityFieldsMap.put(MaterialRequirementFields.MRP_ALGORITHM, materialRequirementService.getDefaultMrpAlgorithm().getStringValue());

        OrderReportService.OrderValidator orderValidator = order -> {
            if (Objects.isNull(order.getField(OrderFields.TECHNOLOGY))) {
                return new ErrorMessage("orders.validate.global.error.orderMustHaveTechnology",
                        order.getStringField(OrderFields.NUMBER));
            }

            return null;
        };

        return orderReportService.printForOrder(state, MaterialRequirementsConstants.PLUGIN_IDENTIFIER,
                MaterialRequirementsConstants.MODEL_MATERIAL_REQUIREMENT, entityFieldsMap, orderValidator);
    }

}
