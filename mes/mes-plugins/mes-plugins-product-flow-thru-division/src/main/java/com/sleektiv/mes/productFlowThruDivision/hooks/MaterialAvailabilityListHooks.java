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
package com.sleektiv.mes.productFlowThruDivision.hooks;

import com.google.common.collect.Lists;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.basic.constants.SubstituteComponentFields;
import com.sleektiv.mes.materialFlow.constants.LocationFields;
import com.sleektiv.mes.materialFlow.constants.MaterialFlowConstants;
import com.sleektiv.mes.materialFlowResources.MaterialFlowResourcesService;
import com.sleektiv.mes.productFlowThruDivision.constants.MaterialAvailabilityFields;
import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.model.api.BigDecimalUtils;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaterialAvailabilityListHooks {

    public static final String L_WINDOW_MAIN_TAB_AVAILABILITY_COMPONENT_FORM_GRID_LAYOUT_LOCATIONS_IDS = "window.mainTab.availabilityComponentForm.gridLayout.locationsIds";

    @Autowired
    private MaterialFlowResourcesService materialFlowResourcesService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void fillInReplacementsAvailableQuantity(final ViewDefinitionState state) throws JSONException {
        FormComponent formComponent = (FormComponent) state.getComponentByReference("product");
        GridComponent grid = (GridComponent) state.getComponentByReference(SleektivViewConstants.L_GRID);
        JSONObject context = state.getJsonContext();

        Entity product = formComponent.getEntity().getDataDefinition().get(formComponent.getEntityId());
        List<Entity> replacements = product.getDataDefinition().get(product.getId())
                .getHasManyField(ProductFields.SUBSTITUTE_COMPONENTS).stream()
                .map(sc -> sc.getBelongsToField(SubstituteComponentFields.PRODUCT)).collect(Collectors.toList());
        List<Entity> warehouses;
        if (context.has(L_WINDOW_MAIN_TAB_AVAILABILITY_COMPONENT_FORM_GRID_LAYOUT_LOCATIONS_IDS)) {
            List<Long> ids = Lists.newArrayList();
            for (int i = 0; i < context.getJSONArray(L_WINDOW_MAIN_TAB_AVAILABILITY_COMPONENT_FORM_GRID_LAYOUT_LOCATIONS_IDS)
                    .length(); i++) {
                ids.add(context.getJSONArray(L_WINDOW_MAIN_TAB_AVAILABILITY_COMPONENT_FORM_GRID_LAYOUT_LOCATIONS_IDS).getLong(i));
            }
            warehouses = dataDefinitionService.get(MaterialFlowConstants.PLUGIN_IDENTIFIER, MaterialFlowConstants.MODEL_LOCATION)
                    .find().add(SearchRestrictions.in("id", ids)).list().getEntities();
        } else {
            warehouses = materialFlowResourcesService.getWarehouseLocationsFromDB();
        }
        List<Entity> materialAvailabilityList = Lists.newArrayList();

        DataDefinition orderMaterialAvailabilityDD = dataDefinitionService.get(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER,
                ProductFlowThruDivisionConstants.MODEL_MATERIAL_AVAILABILITY);

        for (Entity warehouse : warehouses) {
            Map<Long, BigDecimal> availableQuantities = materialFlowResourcesService
                    .getQuantitiesForProductsAndLocation(replacements, warehouse);
            for (Entity replacement : replacements) {

                    Entity materialAvailability = orderMaterialAvailabilityDD.create();
                    materialAvailability.setField(MaterialAvailabilityFields.PRODUCT, replacement);
                    materialAvailability.setField(MaterialAvailabilityFields.UNIT, replacement.getField(ProductFields.UNIT));
                    materialAvailability.setField(MaterialAvailabilityFields.AVAILABLE_QUANTITY,
                            BigDecimalUtils.convertNullToZero(availableQuantities.get(replacement.getId())));
                    materialAvailability.setField(MaterialAvailabilityFields.LOCATION, warehouse);
                    materialAvailabilityList.add(materialAvailability);
            }
        }
        grid.setEntities(materialAvailabilityList.stream()
                .sorted(Comparator.comparing(
                        e -> e.getBelongsToField(MaterialAvailabilityFields.PRODUCT).getStringField(LocationFields.NUMBER)))
                .collect(Collectors.toList()));
    }

    public void fillInAvailableQuantity(final ViewDefinitionState state) {
        FormComponent formComponent = (FormComponent) state.getComponentByReference("product");
        GridComponent grid = (GridComponent) state.getComponentByReference(SleektivViewConstants.L_GRID);

        Entity product = formComponent.getEntity().getDataDefinition().get(formComponent.getEntityId());

        List<Entity> warehouses = materialFlowResourcesService.getWarehouseLocationsFromDB();
        List<Entity> materialAvailabilityList = Lists.newArrayList();

        DataDefinition orderMaterialAvailabilityDD = dataDefinitionService.get(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER,
                ProductFlowThruDivisionConstants.MODEL_MATERIAL_AVAILABILITY);

        for (Entity warehouse : warehouses) {
            Map<Long, BigDecimal> availableQuantities = materialFlowResourcesService
                    .getQuantitiesForProductsAndLocation(Collections.singletonList(product), warehouse);
                Entity materialAvailability = orderMaterialAvailabilityDD.create();

                materialAvailability.setField(MaterialAvailabilityFields.UNIT, product.getField(ProductFields.UNIT));
                materialAvailability.setField(MaterialAvailabilityFields.AVAILABLE_QUANTITY,
                        BigDecimalUtils.convertNullToZero(availableQuantities.get(product.getId())));
                materialAvailability.setField(MaterialAvailabilityFields.LOCATION, warehouse);
                materialAvailabilityList.add(materialAvailability);
        }
        grid.setEntities(materialAvailabilityList.stream()
                .sorted(Comparator.comparing(
                        e -> e.getBelongsToField(MaterialAvailabilityFields.LOCATION).getStringField(LocationFields.NAME)))
                .collect(Collectors.toList()));
    }
}
