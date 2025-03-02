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
package com.sleektiv.mes.productionLines.listeners;

import com.sleektiv.mes.basic.LookupUtils;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.DivisionFields;
import com.sleektiv.mes.basic.constants.WorkstationFields;
import com.sleektiv.mes.productionLines.constants.WorkstationFieldsPL;
import com.sleektiv.mes.productionLines.factoryStructure.FactoryStructureGenerationService;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityTree;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductionLineDetailsListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private LookupUtils lookupUtils;

    @Autowired
    private FactoryStructureGenerationService factoryStructureGenerationService;

    public void onAddExistingEntity(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        if (args.length < 1) {
            return;
        }
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity productionLine = form.getPersistedEntityWithIncludedFormValues();
        List<Long> addedWorkstationIds = lookupUtils.parseIds(args[0]);
        for (Long addedWorkstationId : addedWorkstationIds) {
            Entity workstation = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_WORKSTATION)
                    .get(addedWorkstationId);
            workstation.setField(WorkstationFieldsPL.PRODUCTION_LINE, productionLine);
            workstation.getDataDefinition().save(workstation);
        }
    }

    public void onRemoveSelectedEntity(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent workstationsGrid = (GridComponent) view.getComponentByReference("workstations");
        List<Entity> workstationsToDelete = workstationsGrid.getSelectedEntities();
        for (Entity workstation : workstationsToDelete) {
            workstation.setField(WorkstationFieldsPL.PRODUCTION_LINE, null);
            workstation.setField(WorkstationFields.DIVISION, null);
            workstation.getDataDefinition().save(workstation);

        }
    }

    public void onRemoveSelectedDivisions(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent divisionsGrid = (GridComponent) view.getComponentByReference("divisions");
        List<Entity> divisionsToDelete = divisionsGrid.getSelectedEntities();
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Long productionLineId = form.getEntityId();
        for (Entity division : divisionsToDelete) {
            List<Entity> workstations = division.getHasManyField(DivisionFields.WORKSTATIONS);
            workstations
                    .stream()
                    .filter(workstation -> workstation.getBelongsToField(WorkstationFieldsPL.PRODUCTION_LINE).getId()
                            .equals(productionLineId)).forEach(workstation -> {
                        workstation.setField(WorkstationFieldsPL.PRODUCTION_LINE, null);
                        workstation.setField(WorkstationFields.DIVISION, null);
                        workstation.getDataDefinition().save(workstation);
                    });
        }
    }

    public void generateFactoryStructure(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity productionLine = form.getEntity();
        EntityTree structure = factoryStructureGenerationService.generateFactoryStructureForProductionLine(productionLine);
        productionLine.setField(WorkstationFieldsPL.FACTORY_STRUCTURE, structure);
        form.setEntity(productionLine);
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        window.setActiveTab("factoryStructureTab");
    }

}
