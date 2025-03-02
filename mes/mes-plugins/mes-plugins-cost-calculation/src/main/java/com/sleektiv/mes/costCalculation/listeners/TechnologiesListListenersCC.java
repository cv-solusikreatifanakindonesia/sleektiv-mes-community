package com.sleektiv.mes.costCalculation.listeners;

import com.google.common.collect.Maps;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.costCalculation.constants.CostCalculationConstants;
import com.sleektiv.mes.costCalculation.constants.CostCalculationFields;
import com.sleektiv.mes.costCalculation.constants.MaterialCostsUsed;
import com.sleektiv.mes.costCalculation.constants.SourceOfOperationCosts;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.utils.NumberGeneratorService;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TechnologiesListListenersCC {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @Autowired
    private ParameterService parameterService;

    public final void createCostCalculation(final ViewDefinitionState view, final ComponentState componentState,
                                            final String[] args) {
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        List<Entity> selectedEntities = grid.getSelectedEntities();

        if (!selectedEntities.isEmpty()) {
            DataDefinition technologyDD = dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER,
                    TechnologiesConstants.MODEL_TECHNOLOGY);
            List<Entity> technologies = selectedEntities.stream().map(e -> technologyDD.get(e.getId()))
                    .collect(Collectors.toList());
            DataDefinition costCalculationDD = dataDefinitionService.get(CostCalculationConstants.PLUGIN_IDENTIFIER,
                    CostCalculationConstants.MODEL_COST_CALCULATION);
            Entity costCalculation = costCalculationDD.create();
            costCalculation.setField(CostCalculationFields.TECHNOLOGIES, technologies);
            costCalculation.setField(CostCalculationFields.QUANTITY, BigDecimal.ONE);
            Entity parameter = parameterService.getParameter();
            costCalculation.setField(CostCalculationFields.MATERIAL_COSTS_USED,
                    parameter.getStringField(CostCalculationFields.MATERIAL_COSTS_USED) != null
                            ? parameter.getStringField(CostCalculationFields.MATERIAL_COSTS_USED)
                            : MaterialCostsUsed.NOMINAL.getStringValue());
            costCalculation.setField(CostCalculationFields.USE_NOMINAL_COST_PRICE_NOT_SPECIFIED,
                    parameter.getBooleanField(CostCalculationFields.USE_NOMINAL_COST_PRICE_NOT_SPECIFIED));
            costCalculation.setField(CostCalculationFields.SOURCE_OF_OPERATION_COSTS,
                    parameter.getStringField(CostCalculationFields.SOURCE_OF_OPERATION_COSTS) != null
                            ? parameter.getStringField(CostCalculationFields.SOURCE_OF_OPERATION_COSTS)
                            : SourceOfOperationCosts.TECHNOLOGY_OPERATION.getStringValue());
            costCalculation.setField(CostCalculationFields.STANDARD_LABOR_COST,
                    parameter.getBelongsToField(CostCalculationFields.STANDARD_LABOR_COST));
            costCalculation.setField(CostCalculationFields.AVERAGE_MACHINE_HOURLY_COST,
                    parameter.getDecimalField(CostCalculationFields.AVERAGE_MACHINE_HOURLY_COST));
            costCalculation.setField(CostCalculationFields.AVERAGE_LABOR_HOURLY_COST,
                    parameter.getDecimalField(CostCalculationFields.AVERAGE_LABOR_HOURLY_COST));
            costCalculation.setField(CostCalculationFields.INCLUDE_TPZ,
                    parameter.getBooleanField(CostCalculationFields.INCLUDE_TPZ));
            costCalculation.setField(CostCalculationFields.INCLUDE_ADDITIONAL_TIME,
                    parameter.getBooleanField(CostCalculationFields.INCLUDE_ADDITIONAL_TIME));

            costCalculation.setField(CostCalculationFields.MATERIAL_COST_MARGIN,
                    parameter.getDecimalField(CostCalculationFields.MATERIAL_COST_MARGIN));
            costCalculation.setField(CostCalculationFields.PRODUCTION_COST_MARGIN,
                    parameter.getDecimalField(CostCalculationFields.PRODUCTION_COST_MARGIN));
            costCalculation.setField(CostCalculationFields.ADDITIONAL_OVERHEAD,
                    parameter.getDecimalField(CostCalculationFields.ADDITIONAL_OVERHEAD));
            costCalculation.setField(CostCalculationFields.REGISTRATION_PRICE_OVERHEAD,
                    parameter.getDecimalField(CostCalculationFields.REGISTRATION_PRICE_OVERHEAD));
            costCalculation.setField(CostCalculationFields.TECHNICAL_PRODUCTION_COST_OVERHEAD,
                    parameter.getDecimalField(CostCalculationFields.TECHNICAL_PRODUCTION_COST_OVERHEAD));
            costCalculation.setField(CostCalculationFields.PROFIT, parameter.getDecimalField(CostCalculationFields.PROFIT));
            costCalculation.setField(CostCalculationFields.NUMBER, numberGeneratorService
                    .generateNumber(CostCalculationConstants.PLUGIN_IDENTIFIER, CostCalculationConstants.MODEL_COST_CALCULATION));
            costCalculation = costCalculationDD.fastSave(costCalculation);
            String url = "../page/costCalculation/costCalculationDetails.html";
            Map<String, Object> parameters = Maps.newHashMap();
            parameters.put("form.id", costCalculation.getId());

            view.redirectTo(url, false, true, parameters);
        }
    }
}
