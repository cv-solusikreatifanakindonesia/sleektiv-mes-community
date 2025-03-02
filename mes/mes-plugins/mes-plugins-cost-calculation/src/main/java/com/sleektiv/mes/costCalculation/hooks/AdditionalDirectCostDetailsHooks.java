package com.sleektiv.mes.costCalculation.hooks;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.costCalculation.constants.AdditionalDirectCostFields;
import com.sleektiv.mes.costCalculation.constants.AdditionalDirectCostItemFields;
import com.sleektiv.mes.costCalculation.constants.CostCalculationConstants;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class AdditionalDirectCostDetailsHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberService numberService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent formComponent = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        if (Objects.nonNull(formComponent.getEntityId())) {
            FieldComponent currentCostFieldComponent = (FieldComponent) view.getComponentByReference(AdditionalDirectCostFields.CURRENT_COST);
            BigDecimal currentCost = findCurrentCost(formComponent.getEntityId());
            currentCostFieldComponent.setFieldValue(numberService.formatWithMinimumFractionDigits(currentCost, 0));
        }
    }

    private BigDecimal findCurrentCost(Long entityId) {
        Entity additionalDirectCost = dataDefinitionService.get(CostCalculationConstants.PLUGIN_IDENTIFIER, CostCalculationConstants.MODEL_ADDITIONAL_DIRECT_COST)
                .get(entityId);

        SearchCriteriaBuilder scb = dataDefinitionService.get(CostCalculationConstants.PLUGIN_IDENTIFIER, CostCalculationConstants.MODEL_ADDITIONAL_DIRECT_COST_ITEM)
                .find().addOrder(SearchOrders.desc(AdditionalDirectCostItemFields.DATE_FROM))
                .add(SearchRestrictions.belongsTo(AdditionalDirectCostItemFields.ADDITIONAL_DIRECT_COST, additionalDirectCost))
                .add(SearchRestrictions.le(AdditionalDirectCostItemFields.DATE_FROM, new Date()));
        Entity additionalDirectCostItem = scb.setMaxResults(1).uniqueResult();
        if (Objects.isNull(additionalDirectCostItem)) {
            return null;
        } else {
            return additionalDirectCostItem.getDecimalField(AdditionalDirectCostItemFields.ACTUAL_COST);
        }

    }
}
