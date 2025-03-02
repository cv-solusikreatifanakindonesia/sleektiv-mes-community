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
package com.sleektiv.mes.technologiesGenerator.hooks;

import com.sleektiv.mes.technologiesGenerator.constants.GeneratorContextFields;
import com.sleektiv.mes.technologiesGenerator.constants.TechnologiesGeneratorConstants;
import com.sleektiv.mes.technologiesGenerator.criteriaModifier.TechnologiesForProductsCM;
import com.sleektiv.mes.technologiesGenerator.dataProvider.TechnologyStructureNodeDataProvider;
import com.sleektiv.mes.technologiesGenerator.view.GeneratorView;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.api.utils.NumberGeneratorService;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeneratorViewHooks {

    

    @Autowired
    private TechnologyStructureNodeDataProvider nodeDataProvider;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    public void onBeforeRender(final ViewDefinitionState view) {
        GeneratorView generatorView = GeneratorView.from(view);
        generateProductNumber(view);
        showRibbonButtons(generatorView, view);
        GridComponent grid = (GridComponent) view.getComponentByReference("generatorTechnologiesForProducts");
        FilterValueHolder gridHolder = grid.getFilterValue();
        gridHolder.put(TechnologiesForProductsCM.PARAMETER, generatorView.getFormEntity().getId());
        grid.setFilterValue(gridHolder);

    }

    public void generateProductNumber(final ViewDefinitionState view) {
        numberGeneratorService.generateAndInsertNumber(view, TechnologiesGeneratorConstants.PLUGIN_IDENTIFIER, TechnologiesGeneratorConstants.MODEL_GENERATOR_CONTEXT,
                SleektivViewConstants.L_FORM, GeneratorContextFields.NAME);
    }

    void showRibbonButtons(final GeneratorView generatorView, final ViewDefinitionState view) {
        Entity contextEntity = generatorView.getFormEntity();
        boolean isAlreadyGenerated = contextEntity.getBooleanField(GeneratorContextFields.GENERATED);
        boolean generationInProgress = contextEntity.getBooleanField(GeneratorContextFields.GENERATION_IN_PROGRSS);
        generatorView.setGenerationEnabled(!isAlreadyGenerated);
        generatorView.setRefreshRibbonButtonEnabled(isAlreadyGenerated);
        Entity context = generatorView.getFormEntity();
        if (context.getId() != null) {
            List<Entity> products = context.getHasManyField(GeneratorContextFields.PRODUCTS);
            Optional<List<Entity>> optionalNodes = nodeDataProvider.getCastumizedNodesForContext(context);
            if (optionalNodes.get().isEmpty() || products.isEmpty()) {
                generatorView.setGenerationGroupButtonEnabled(false, false, "generateTechnologies");

            } else {
                generatorView.setGenerationGroupButtonEnabled(!generationInProgress, true, "generateTechnologies");
                generatorView.setGenerationGroupButtonEnabled(!generationInProgress, true, "refresh");
                generatorView.setGenerationGroupButtonEnabled(!generationInProgress, true, "customize");
                generatorView.setActionsGroupButtonEnabled(!generationInProgress, true, "save");
                generatorView.setActionsGroupButtonEnabled(!generationInProgress, true, "delete");
            }
        } else {
            generatorView.setGenerationGroupButtonEnabled(false, false, "generateTechnologies");

        }
    }

}
