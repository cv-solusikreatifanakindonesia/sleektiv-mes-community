package com.sleektiv.mes.materialFlowResources.listeners;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.AttributeFields;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.materialFlowResources.constants.DocumentPositionParametersItemFields;
import com.sleektiv.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.sleektiv.mes.materialFlowResources.constants.ParameterFieldsMFR;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ParametersMFRListeners {



    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ParameterService parameterService;

    public void addColumnWithResourceAttribute(final ViewDefinitionState view, final ComponentState state, final String[] args) {

        StringBuilder url = new StringBuilder("../page/materialFlowResources/documentAttributePosition.html");

        view.openModal(url.toString());
    }

    public void addColumns(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        CheckBoxComponent generated = (CheckBoxComponent) view.getComponentByReference("generated");

        Set<Long> ids = grid.getSelectedEntitiesIds();
        if (ids.isEmpty()) {
            generated.setChecked(false);
            view.addMessage("materialFlowResources.documentAttributePosition.noSelectedAttributes", ComponentState.MessageType.INFO);
            return;
        }
        ids.forEach(attrId -> {
            Entity attribute = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_ATTRIBUTE).get(attrId);

            Entity positionItem = dataDefinitionService.get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                    MaterialFlowResourcesConstants.MODEL_DOCUMENT_POSITION_PARAMETERS_ITEM).create();
            positionItem.setField(DocumentPositionParametersItemFields.NAME, attribute.getStringField(AttributeFields.NUMBER));
            positionItem.setField(DocumentPositionParametersItemFields.CHECKED, true);
            positionItem.setField(DocumentPositionParametersItemFields.EDITABLE, true);
            positionItem.setField(DocumentPositionParametersItemFields.PARAMETERS, parameterService.getParameter()
                    .getBelongsToField(ParameterFieldsMFR.DOCUMENT_POSITION_PARAMETERS).getId());
            positionItem.setField(DocumentPositionParametersItemFields.FOR_ATTRIBUTE, true);
            positionItem.setField(DocumentPositionParametersItemFields.ATTRIBUTE, attribute.getId());
            positionItem = positionItem.getDataDefinition().save(positionItem);
        });
        generated.setChecked(true);
    }
}
