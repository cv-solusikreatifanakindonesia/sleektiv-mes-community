package com.sleektiv.mes.technologies.listeners;

import com.google.common.base.Strings;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.DivisionFields;
import com.sleektiv.mes.technologies.constants.ParameterFieldsT;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.exception.EntityRuntimeException;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TechnologiesListListeners {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ParameterService parameterService;


    public void runTechnologyConfigurator(final ViewDefinitionState view, final ComponentState state, final String[] args) {

        String typeOfProductionRecording = parameterService.getParameter().getStringField("typeOfProductionRecording");
        String range = parameterService.getParameter().getStringField("range");
        Entity division = parameterService.getParameter().getBelongsToField("division");
        int activeDivisions = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_DIVISION)
                .find()
                .add(SearchRestrictions.eq("active", true))
                .list().getTotalNumberOfEntities();

        int activeWarehouses = dataDefinitionService.get("materialFlow", "location")
                .find()
                .add(SearchRestrictions.eq("active", true))
                .list().getTotalNumberOfEntities();

        if (!typeOfProductionRecording.equals("02cumulated") && !typeOfProductionRecording.equals("03forEach")) {
            view.addMessage("technologies.technologyConfigurator.configuration.error.typeOfProductionRecording", ComponentState.MessageType.INFO, false);
            return;
        } else if (!parameterService.getParameter().getBooleanField(ParameterFieldsT.COMPLETE_WAREHOUSES_FLOW_WHILE_CHECKING)) {
            view.addMessage("technologies.technologyConfigurator.configuration.error.completeWarehousesFlowWhileChecking", ComponentState.MessageType.INFO, false);
            return;
        } else if (Strings.isNullOrEmpty(range)) {
            view.addMessage("technologies.technologyConfigurator.configuration.error.range", ComponentState.MessageType.INFO, false);
            return;
        } else if (activeDivisions < 1) {
            view.addMessage("technologies.technologyConfigurator.configuration.error.activeDivisions", ComponentState.MessageType.INFO, false);
            return;
        } else if (activeWarehouses < 1) {
            view.addMessage("technologies.technologyConfigurator.configuration.error.activeWarehouses", ComponentState.MessageType.INFO, false);
            return;
        } else if(range.equals("01oneDivision") && Objects.isNull(division)) {
            view.addMessage("technologies.technologyConfigurator.configuration.error.divisionEmpty", ComponentState.MessageType.INFO, false);
            return;
        }

        view.redirectTo("/technologyConfigurator.html", false, false);
    }

    public void setAsDefault(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        try {
            trySetAsDefault(view);
        } catch (EntityRuntimeException exc) {
            view.addMessage("technologies.setAsDefault.error.errorWhenSetAsDefault",
                    ComponentState.MessageType.FAILURE, exc.getEntity().getStringField(TechnologyFields.NUMBER));
        }
    }

    @Transactional
    public void trySetAsDefault(ViewDefinitionState view) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Set<Long> selectedEntities = grid.getSelectedEntitiesIds();
        selectedEntities.forEach(techId -> {
            Entity technology = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGY).get(techId);

            technology.setField(TechnologyFields.MASTER, Boolean.TRUE);
            technology = technology.getDataDefinition().save(technology);
            if (!technology.isValid()) {
                throw new EntityRuntimeException(technology);
            }
        });
    }

    public void changeParameters(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Set<Long> selectedEntities = grid.getSelectedEntitiesIds();
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("selectedEntities", selectedEntities);
        JSONObject context = new JSONObject(parameters);
        String url = "../page/technologies/changeTechnologyParameters.html?context=" + context;

        view.openModal(url);
    }

    public void addAttachments(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("technologiesIds", grid.getSelectedEntitiesIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        String url = "../page/technologies/technologiesAttachments.html";

        view.openModal(url, parameters);
    }

}
