package com.sleektiv.mes.technologies.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TechnologiesWithUsingProductListListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void goToModifyTechnology(final ViewDefinitionState view, final ComponentState componentState, final String[] args) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        List<Integer> entitiesId = Lists.newArrayList();

        Set<Long> selected = grid.getSelectedEntitiesIds();
        List<Entity> entities = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, "operationProductInProductDto").find().add(
                SearchRestrictions.in("id", selected)).list().getEntities();

        boolean isProductBySize = entities.stream().anyMatch(entry -> selected.contains(entry.getId()) && entry.getBooleanField("sizeProduct"));

        for (Entity entity : entities) {

            if(selected.contains(entity.getId())) {
                if(isProductBySize) {
                    if(!entitiesId.contains(entity.getIntegerField("productBySizeGroupId"))) {
                        entitiesId.add(entity.getIntegerField("productBySizeGroupId"));
                    }
                } else {
                    if(!entitiesId.contains(entity.getIntegerField("operationProductInComponentId"))) {
                        entitiesId.add(entity.getIntegerField("operationProductInComponentId"));
                    }
                }
            }
        }

        Entity modifyTechnology = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                TechnologiesConstants.MODIFY_TECHNOLOGY_HELPER).create();

        modifyTechnology.setField("product", form.getEntityId());
        modifyTechnology.setField("sizeProduct", isProductBySize);
        modifyTechnology.setField("selectedEntities", entitiesId.stream().map(e -> e.toString()).collect(Collectors.joining(",")));

        modifyTechnology = modifyTechnology.getDataDefinition().save(modifyTechnology);

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("form.id", modifyTechnology.getId());
        String url = "../page/technologies/modifyTechnology.html";
        view.openModal(url, parameters);
    }
}
