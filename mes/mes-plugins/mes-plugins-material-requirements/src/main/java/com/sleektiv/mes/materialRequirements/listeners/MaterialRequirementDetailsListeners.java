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

import com.lowagie.text.DocumentException;
import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.materialRequirements.MaterialRequirementService;
import com.sleektiv.mes.materialRequirements.constants.MaterialRequirementFields;
import com.sleektiv.mes.materialRequirements.constants.MaterialRequirementsConstants;
import com.sleektiv.mes.orders.util.OrderHelperService;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.report.api.ReportService;
import com.sleektiv.security.api.SecurityService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MaterialRequirementDetailsListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MaterialRequirementService materialRequirementService;

    @Autowired
    private OrderHelperService orderHelperService;

    public void printMaterialRequirement(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        reportService.printGeneratedReport(view, state, new String[]{args[0], MaterialRequirementsConstants.PLUGIN_IDENTIFIER,
                MaterialRequirementsConstants.MODEL_MATERIAL_REQUIREMENT});
    }

    @Transactional
    public void generateMaterialRequirement(final ViewDefinitionState view, final ComponentState state,
                                            final String[] args) {
        if (state instanceof FormComponent) {
            FormComponent materialRequirementForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
            FieldComponent generatedField = (FieldComponent) view.getComponentByReference(MaterialRequirementFields.GENERATED);
            FieldComponent dateField = (FieldComponent) view.getComponentByReference(MaterialRequirementFields.DATE);
            FieldComponent workerField = (FieldComponent) view.getComponentByReference(MaterialRequirementFields.WORKER);

            Entity materialRequirement = getMaterialRequirementDD().get((Long) state.getFieldValue());

            if (Objects.isNull(materialRequirement)) {
                state.addMessage("sleektivView.message.entityNotFound", MessageType.FAILURE);

                return;
            } else if (StringUtils.isNotBlank(materialRequirement.getStringField("fileName"))) {
                state.addMessage(
                        "materialRequirements.materialRequirementDetails.window.materialRequirement.documentsWasGenerated",
                        MessageType.FAILURE);

                return;
            }

            List<Entity> orders = materialRequirement.getManyToManyField(MaterialRequirementFields.ORDERS);

            if (orders.isEmpty()) {
                state.addMessage(
                        "materialRequirements.materialRequirementDetails.window.materialRequirement.missingAssociatedOrders",
                        MessageType.FAILURE);

                return;
            }

            List<String> numbersOfOrdersWithoutTechnology = orderHelperService.getOrdersWithoutTechnology(orders);

            if (!numbersOfOrdersWithoutTechnology.isEmpty()) {
                state.addMessage(
                        "materialRequirements.materialRequirementDetails.window.materialRequirement.missingTechnologyInOrders",
                        MessageType.FAILURE, StringUtils.join(numbersOfOrdersWithoutTechnology, ",<br>"));

                return;
            }

            if ("0".equals(generatedField.getFieldValue())) {
                generatedField.setFieldValue("1");
                dateField.setFieldValue(DateUtils.toDateTimeString(new Date()));
                workerField.setFieldValue(securityService.getCurrentUserName());
            }

            state.performEvent(view, "save");

            if (Objects.isNull(state.getFieldValue()) || !((FormComponent) state).isValid()) {
                generatedField.setFieldValue("0");
                dateField.setFieldValue(null);
                workerField.setFieldValue(null);

                return;
            }

            materialRequirement = getMaterialRequirementDD().get(materialRequirementForm.getEntityId());

            try {
                materialRequirementService.generateMaterialRequirementDocuments(state, materialRequirement);

                materialRequirementForm.performEvent(view, "reset");
            } catch (IOException | DocumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    private DataDefinition getMaterialRequirementDD() {
        return dataDefinitionService.get(MaterialRequirementsConstants.PLUGIN_IDENTIFIER,
                MaterialRequirementsConstants.MODEL_MATERIAL_REQUIREMENT);
    }

}
