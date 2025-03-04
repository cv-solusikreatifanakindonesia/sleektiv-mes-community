package com.sleektiv.mes.materialFlowResources.criteriaModifiers;

import com.sleektiv.mes.basic.constants.AttributeFields;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchRestrictions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttributeCriteriaModifiersMFR {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void showForProduct(final SearchCriteriaBuilder scb) {
        scb.add(SearchRestrictions.eq(AttributeFields.FOR_PRODUCT, Boolean.TRUE));
    }

    public void showForResourceAndColumnPosition(final SearchCriteriaBuilder scb) {
        List<Entity> columns = dataDefinitionService
                .get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                        MaterialFlowResourcesConstants.MODEL_DOCUMENT_POSITION_PARAMETERS_ITEM).find()
                .add(SearchRestrictions.eq("forAttribute", true)).list().getEntities();
        if(!columns.isEmpty()) {
            List<Long> ids = columns.stream().map(col -> col.getBelongsToField("attribute").getId()).collect(Collectors.toList());
            ids.forEach(id -> {
                scb.add(SearchRestrictions.idNe(id));
            });
        }
        scb.add(SearchRestrictions.eq(AttributeFields.FOR_RESOURCE, Boolean.TRUE));
    }
}
