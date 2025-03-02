package com.sleektiv.mes.productionCounting.newstates;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.LogService;
import com.sleektiv.mes.newstates.BasicStateService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.productionCounting.ProductionTrackingService;
import com.sleektiv.mes.productionCounting.constants.ProductionCountingConstants;
import com.sleektiv.mes.productionCounting.constants.ProductionTrackingFields;
import com.sleektiv.mes.productionCounting.states.constants.ProductionTrackingStateChangeDescriber;
import com.sleektiv.mes.productionCounting.states.constants.ProductionTrackingStateChangeFields;
import com.sleektiv.mes.productionCounting.states.constants.ProductionTrackingStateStringValues;
import com.sleektiv.mes.productionCounting.states.listener.ProductionTrackingListenerService;
import com.sleektiv.mes.states.StateChangeEntityDescriber;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.UserService;
import com.sleektiv.security.constants.UserFields;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
public class ProductionTrackingStateService extends BasicStateService implements ProductionTrackingStateServiceMarker {

    @Autowired
    private ProductionTrackingStateChangeDescriber productionTrackingStateChangeDescriber;

    @Autowired
    private ProductionTrackingListenerService productionTrackingListenerService;

    @Autowired
    private ProductionTrackingService productionTrackingService;

    @Autowired
    private LogService logService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private UserService userService;

    @Override
    public StateChangeEntityDescriber getChangeEntityDescriber() {
        return productionTrackingStateChangeDescriber;
    }

    @Override
    public Entity onValidate(Entity entity, String sourceState, String targetState, Entity stateChangeEntity,
                             StateChangeEntityDescriber describer) {
        switch (targetState) {
            case ProductionTrackingStateStringValues.ACCEPTED:
                productionTrackingListenerService.validationOnAccept(entity);
                break;

            case ProductionTrackingStateStringValues.DECLINED:
                productionTrackingListenerService.validationOnDecline(entity);
                break;
        }

        return entity;
    }

    @Override
    public Entity onBeforeSave(Entity entity, String sourceState, String targetState, Entity stateChangeEntity,
                               StateChangeEntityDescriber describer) {

        if (ProductionTrackingStateStringValues.DRAFT.equals(sourceState)) {
            productionTrackingListenerService.onLeavingDraft(entity);
        }

        switch (targetState) {
            case ProductionTrackingStateStringValues.DECLINED:
                productionTrackingListenerService.unMarkLastTracking(entity);
                break;
        }

        return entity;
    }

    @Override
    public Entity onAfterSave(Entity entity, String sourceState, String targetState, Entity stateChangeEntity,
                              StateChangeEntityDescriber describer) {
        switch (targetState) {
            case ProductionTrackingStateStringValues.ACCEPTED:
                productionTrackingListenerService.onAccept(entity);
                break;

            case ProductionTrackingStateStringValues.DECLINED:
                productionTrackingService.unCorrect(entity, false);

                if (ProductionTrackingStateStringValues.ACCEPTED.equals(sourceState)) {
                    productionTrackingListenerService.onChangeFromAcceptedToDeclined(entity);
                }
                productionTrackingListenerService.updateOrderReportedQuantity(entity);
                break;

            case ProductionTrackingStateStringValues.CORRECTED:
                productionTrackingListenerService.onCorrected(entity);
                productionTrackingListenerService.updateOrderReportedQuantity(entity);
                break;
        }

        if (entity.isValid()) {
            logActivities(entity, stateChangeEntity, targetState);
        }
        return entity;
    }

    private void logActivities(final Entity productionTracking, Entity stateChangeEntity, final String state) {
        // TODO get user from state change/current user, not create user
        Entity user = userService.find(stateChangeEntity.getStringField(ProductionTrackingStateChangeFields.WORKER));
        String worker = StringUtils.EMPTY;
        if (user != null) {
            worker = user.getStringField(UserFields.FIRST_NAME) + " " + user.getStringField(UserFields.LAST_NAME);
        }
        String number = productionTracking.getStringField(ProductionTrackingFields.NUMBER);
        Entity order = productionTracking.getBelongsToField(ProductionTrackingFields.ORDER);
        String orderNumber = order.getStringField(OrderFields.NUMBER);
        logService.add(LogService.Builder.activity(
                "productionTracking",
                translationService.translate("productionCounting.productionTracking.activity." + state + ".action",
                        LocaleContextHolder.getLocale())).withMessage(
                translationService.translate("productionCounting.productionTracking.activity." + state + ".message",
                        LocaleContextHolder.getLocale(), worker, generateDetailsUrl(number, productionTracking.getId()),
                        generateOrderDetailsUrl(orderNumber, order.getId()))));
    }

    private String generateDetailsUrl(String number, Long id) {
        return "<a href=\"" + ProductionCountingConstants.productionTrackingDetailsUrl(id) + "\" target=\"_blank\">" + number
                + "</a>";
    }

    private String generateOrderDetailsUrl(String number, Long id) {
        return "<a href=\"" + OrdersConstants.orderDetailsUrl(id) + "\" target=\"_blank\">" + number + "</a>";
    }

}
