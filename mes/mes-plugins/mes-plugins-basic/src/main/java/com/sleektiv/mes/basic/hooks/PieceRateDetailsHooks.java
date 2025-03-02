package com.sleektiv.mes.basic.hooks;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.PieceRateFields;
import com.sleektiv.mes.basic.constants.PieceRateItemFields;
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
public class PieceRateDetailsHooks {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberService numberService;

    public void onBeforeRender(final ViewDefinitionState view) {
        FormComponent formComponent = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        if (Objects.nonNull(formComponent.getEntityId())) {
            FieldComponent currentRateFieldComponent = (FieldComponent) view.getComponentByReference(PieceRateFields.CURRENT_RATE);
            BigDecimal currentRate = findCurrentRate(formComponent.getEntityId());
            currentRateFieldComponent.setFieldValue(numberService.formatWithMinimumFractionDigits(currentRate, 0));
        }
    }

    private BigDecimal findCurrentRate(Long entityId) {
        Entity pieceRate = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PIECE_RATE)
                .get(entityId);

        SearchCriteriaBuilder scb = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PIECE_RATE_ITEM)
                .find().addOrder(SearchOrders.desc(PieceRateItemFields.DATE_FROM))
                .add(SearchRestrictions.belongsTo(PieceRateItemFields.PIECE_RATE, pieceRate))
                .add(SearchRestrictions.le(PieceRateItemFields.DATE_FROM, new Date()));
        Entity pieceRateItem = scb.setMaxResults(1).uniqueResult();
        if (Objects.isNull(pieceRateItem)) {
            return null;
        } else {
            return pieceRateItem.getDecimalField(PieceRateItemFields.ACTUAL_RATE);
        }

    }
}
