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
package com.sleektiv.mes.materialRequirementCoverageForOrder.hooks;

import com.sleektiv.mes.materialRequirementCoverageForOrder.MaterialRequirementCoverageForOrderService;
import com.sleektiv.mes.materialRequirementCoverageForOrder.constans.CoverageForOrderFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.model.api.search.SearchResult;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.AwesomeDynamicListComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MRCForOrderDetailsHooks {

    



    private static final String L_COVERAGE = "coverage";

    private static final String L_PRINT_MATERIAL_REQUIREMENT_COVERAGE = "printMaterialRequirementCoverage";

    @Autowired
    private MaterialRequirementCoverageForOrderService materialRequirementCoverageForOrderService;

    public final void onBeforeRender(final ViewDefinitionState view) {
        updateFormState(view);
        updateRibbonState(view);
    }

    public void updateFormState(final ViewDefinitionState view) {
        FormComponent materialRequirementCoverageForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        AwesomeDynamicListComponent adlc = (AwesomeDynamicListComponent) view
                .getComponentByReference(CoverageForOrderFields.COVERAGE_LOCATIONS);

        Long materialRequirementCoverageId = materialRequirementCoverageForm.getEntityId();

        boolean saved = checkIfMaterialRequirementCoverageIsSaved(materialRequirementCoverageId);

        materialRequirementCoverageForm.setFormEnabled(!saved);

        List<FormComponent> formComponents = adlc.getFormComponents();

        for (FormComponent formComponent : formComponents) {
            formComponent.setFormEnabled(!saved);
        }
    }

    public boolean checkIfMaterialRequirementCoverageIsSaved(final Long materialRequirementCoverageId) {
        if (materialRequirementCoverageId != null) {
            Entity materialRequirementCoverage = materialRequirementCoverageForOrderService
                    .getMRCForOrder(materialRequirementCoverageId);

            if (materialRequirementCoverage != null) {
                return materialRequirementCoverage.getBooleanField(CoverageForOrderFields.SAVED);
            }
        }

        return false;
    }

    private boolean checkIfThereAreSavedMaterialRequirementCoverages() {
        SearchResult searchResult = materialRequirementCoverageForOrderService.getMRCDD().find()
                .add(SearchRestrictions.eq(CoverageForOrderFields.SAVED, true)).list();

        return !searchResult.getEntities().isEmpty();
    }

    public void updateRibbonState(final ViewDefinitionState view) {
        FieldComponent generatedField = (FieldComponent) view
                .getComponentByReference(CoverageForOrderFields.GENERATED);
        boolean generated = "1".equals(generatedField.getFieldValue());

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup coverage = (RibbonGroup) window.getRibbon().getGroupByName(L_COVERAGE);

        RibbonActionItem printMaterialRequirementCoverage = (RibbonActionItem) coverage
                .getItemByName(L_PRINT_MATERIAL_REQUIREMENT_COVERAGE);

        updateButtonState(printMaterialRequirementCoverage, generated);
    }

    private void updateButtonState(final RibbonActionItem ribbonActionItem, final boolean isEnabled) {
        ribbonActionItem.setEnabled(isEnabled);
        ribbonActionItem.requestUpdate(true);
    }

}
