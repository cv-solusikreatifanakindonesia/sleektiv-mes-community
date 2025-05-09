package com.sleektiv.mes.orders.hooks;

import com.sleektiv.mes.orders.OrderService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.orders.util.OrderDetailsRibbonHelper;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderAdditionalDetailsHooks {

    public static final String L_TECHNOLOGY = "technology";

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailsRibbonHelper orderDetailsRibbonHelper;

    public final void onBeforeRender(final ViewDefinitionState view) {

        FormComponent orderForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        boolean disabled = false;

        Long orderId = orderForm.getEntityId();

        if (orderId != null) {
            Entity order = orderService.getOrder(orderId);

            if (order == null) {
                return;
            }

            setRibbonState(view, order);

            String state = order.getStringField(OrderFields.STATE);

            if (!OrderState.PENDING.getStringValue().equals(state)) {
                disabled = true;
            }
        }

        orderForm.setFormEnabled(!disabled);
    }

    private void setRibbonState(ViewDefinitionState view, Entity order) {
        RibbonActionItem technologyItem = orderDetailsRibbonHelper.getRibbonItem(view, L_TECHNOLOGY, L_TECHNOLOGY);
        if (Objects.nonNull(order.getBelongsToField(OrderFields.TECHNOLOGY))) {
            technologyItem.setEnabled(true);
        } else {
            technologyItem.setEnabled(false);
        }

        technologyItem.requestUpdate(true);
    }
}
