package com.sleektiv.mes.costCalculation.hooks;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.costCalculation.constants.AdditionalDirectCostFields;
import com.sleektiv.mes.costCalculation.constants.AdditionalDirectCostItemFields;
import com.sleektiv.mes.costCalculation.constants.CostCalculationConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchRestrictions;

@Service
public class AdditionalDirectCostHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void onView(final DataDefinition dataDefinition, final Entity entity) {
        entity.setField(AdditionalDirectCostFields.CURRENT_COST, findCurrentCost(entity));
    }

    private BigDecimal findCurrentCost(Entity entity) {
        SearchCriteriaBuilder scb = dataDefinitionService
                .get(CostCalculationConstants.PLUGIN_IDENTIFIER, CostCalculationConstants.MODEL_ADDITIONAL_DIRECT_COST_ITEM)
                .find().addOrder(SearchOrders.desc(AdditionalDirectCostItemFields.DATE_FROM))
                .add(SearchRestrictions.belongsTo(AdditionalDirectCostItemFields.ADDITIONAL_DIRECT_COST, entity))
                .add(SearchRestrictions.le(AdditionalDirectCostItemFields.DATE_FROM, new Date()));
        Entity additionalDirectCostItem = scb.setMaxResults(1).uniqueResult();
        if(Objects.isNull(additionalDirectCostItem)) {
            return null;
        } else {
            return additionalDirectCostItem.getDecimalField(AdditionalDirectCostItemFields.ACTUAL_COST);
        }

    }

}
