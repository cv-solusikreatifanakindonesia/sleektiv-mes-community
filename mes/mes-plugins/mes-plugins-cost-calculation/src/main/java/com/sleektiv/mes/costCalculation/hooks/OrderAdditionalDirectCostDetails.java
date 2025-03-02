package com.sleektiv.mes.costCalculation.hooks;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.costCalculation.constants.AdditionalDirectCostItemFields;
import com.sleektiv.mes.costCalculation.constants.CostCalculationConstants;
import com.sleektiv.mes.costCalculation.constants.OrderAdditionalDirectCostFields;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class OrderAdditionalDirectCostDetails {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberService numberService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity entity = null;
        if (form.getEntityId() != null) {
            entity = form.getEntity().getDataDefinition().get(form.getEntityId());
        }
        FieldComponent actualCost = (FieldComponent) view.getComponentByReference(OrderAdditionalDirectCostFields.ACTUAL_COST);
        if (entity == null || entity.getDecimalField(OrderAdditionalDirectCostFields.ACTUAL_COST) == null) {
            Date startDate = form.getEntity().getBelongsToField(OrderAdditionalDirectCostFields.ORDER).getDateField(OrderFields.START_DATE);
            if (startDate != null) {
                LookupComponent additionalDirectCost = (LookupComponent) view.getComponentByReference(OrderAdditionalDirectCostFields.ADDITIONAL_DIRECT_COST);
                if (additionalDirectCost.getFieldValue() != null) {
                    BigDecimal currentCost = findCurrentCost(additionalDirectCost.getEntity().getId(), startDate);
                    actualCost.setFieldValue(numberService.formatWithMinimumFractionDigits(currentCost, 0));
                } else {
                    actualCost.setFieldValue(null);
                }
            } else {
                actualCost.setFieldValue(null);
            }
        }
    }

    public BigDecimal findCurrentCost(Long entityId, Date startDate) {
        Entity additionalDirectCost = dataDefinitionService.get(CostCalculationConstants.PLUGIN_IDENTIFIER, CostCalculationConstants.MODEL_ADDITIONAL_DIRECT_COST)
                .get(entityId);

        SearchCriteriaBuilder scb = dataDefinitionService.get(CostCalculationConstants.PLUGIN_IDENTIFIER, CostCalculationConstants.MODEL_ADDITIONAL_DIRECT_COST_ITEM)
                .find().addOrder(SearchOrders.desc(AdditionalDirectCostItemFields.DATE_FROM))
                .add(SearchRestrictions.belongsTo(AdditionalDirectCostItemFields.ADDITIONAL_DIRECT_COST, additionalDirectCost))
                .add(SearchRestrictions.le(AdditionalDirectCostItemFields.DATE_FROM, startDate));
        Entity additionalDirectCostItem = scb.setMaxResults(1).uniqueResult();
        if (Objects.isNull(additionalDirectCostItem)) {
            return null;
        } else {
            return additionalDirectCostItem.getDecimalField(AdditionalDirectCostItemFields.ACTUAL_COST);
        }

    }
}
