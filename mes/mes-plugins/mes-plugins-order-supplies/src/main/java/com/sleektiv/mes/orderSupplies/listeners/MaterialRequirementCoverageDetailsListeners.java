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
package com.sleektiv.mes.orderSupplies.listeners;

import com.google.common.collect.Lists;
import com.sleektiv.mes.orderSupplies.OrderSuppliesService;
import com.sleektiv.mes.orderSupplies.constants.OrderSuppliesConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.report.api.ReportService;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ComponentState.MessageType;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MaterialRequirementCoverageDetailsListeners {

    @Autowired private OrderSuppliesService orderSuppliesService;

    @Autowired private ReportService reportService;

    @Autowired DataDefinitionService dataDefinitionService;

    public final void printMaterialRequirementCoverage(final ViewDefinitionState view, final ComponentState state,
            final String[] args) {
        if (state instanceof FormComponent) {
            state.performEvent(view, "save", args);

            if (!state.isHasError()) {
                FormComponent materialRequirementCoverageForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
                Long materialRequirementCoverageId = materialRequirementCoverageForm.getEntityId();

                boolean saved = orderSuppliesService.checkIfMaterialRequirementCoverageIsSaved(materialRequirementCoverageId);

                if (saved) {
                    reportService.printGeneratedReport(view, state,
                            new String[] { args[0], OrderSuppliesConstants.PLUGIN_IDENTIFIER,
                                    OrderSuppliesConstants.MODEL_MATERIAL_REQUIREMENT_COVERAGE });
                } else {
                    view.redirectTo(
                            "/orderSupplies/materialRequirementCoverageReport." + args[0] + "?id=" + state.getFieldValue(), true,
                            false);
                }
            }
        } else {
            state.addMessage("orderSupplies.materialRequirementCoverage.report.componentFormError", MessageType.FAILURE);
        }
    }

    public final void deleteCoverage(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Set<Long> ids = grid.getSelectedEntitiesIds();
        orderSuppliesService.deleteMaterialRequirementCoverageAndReferences(Lists.newArrayList(ids));

        if (ids.size() == 1) {
            state.addMessage("sleektivView.message.deleteMessage", MessageType.SUCCESS);
        } else {
            state.addMessage("sleektivView.message.deleteMessages", MessageType.SUCCESS,String.valueOf(ids.size()));
        }
    }
}
