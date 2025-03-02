package com.sleektiv.mes.orders.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.orders.controllers.dataProvider.DashboardKanbanDataProvider;
import com.sleektiv.mes.orders.controllers.dto.OperationalTaskHolder;
import com.sleektiv.mes.orders.controllers.dto.OrderHolder;
import com.sleektiv.mes.orders.controllers.responses.OrderResponse;
import com.sleektiv.mes.orders.states.aop.OrderStateChangeAspect;
import com.sleektiv.mes.orders.states.constants.OrderState;
import com.sleektiv.mes.states.StateChangeContext;
import com.sleektiv.mes.states.messages.util.MessagesUtil;
import com.sleektiv.mes.states.service.StateChangeContextBuilder;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.validators.ErrorMessage;

@Controller
@RequestMapping("/dashboardKanban")
public class DashboardKanbanController {

    @Autowired
    private DashboardKanbanDataProvider dashboardKanbanDataProvider;

    @Autowired
    private OrderStateChangeAspect orderStateChangeAspect;

    @Autowired
    private StateChangeContextBuilder stateChangeContextBuilder;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private TranslationService translationService;

    @ResponseBody
    @RequestMapping(value = "/ordersPending", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderHolder> getOrdersPending() {
        return dashboardKanbanDataProvider.getOrdersPending();
    }

    @ResponseBody
    @RequestMapping(value = "/ordersInProgress", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderHolder> getOrdersInProgress() {
        return dashboardKanbanDataProvider.getOrdersInProgress();
    }

    @ResponseBody
    @RequestMapping(value = "/ordersCompleted", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderHolder> getOrdersCompleted() {
        return dashboardKanbanDataProvider.getOrdersCompleted();
    }

    @ResponseBody
    @RequestMapping(value = "/operationalTasksPending", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OperationalTaskHolder> getOperationalTasksPending() {
        return dashboardKanbanDataProvider.getOperationalTasksPending();
    }

    @ResponseBody
    @RequestMapping(value = "/operationalTasksInProgress", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OperationalTaskHolder> getOperationalTasksInProgress() {
        return dashboardKanbanDataProvider.getOperationalTasksInProgress();
    }

    @ResponseBody
    @RequestMapping(value = "/operationalTasksCompleted", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OperationalTaskHolder> getOperationalTasksCompleted() {
        return dashboardKanbanDataProvider.getOperationalTasksCompleted();
    }

    @ResponseBody
    @RequestMapping(value = "/updateOrderState/{orderId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderResponse updateOrderState(@PathVariable final Long orderId) {
        Entity order = getOrderDD().get(orderId);

        String targetState = OrderState.IN_PROGRESS.getStringValue();

        if (OrderState.IN_PROGRESS.getStringValue().equals(order.getStringField(OrderFields.STATE))) {
            targetState = OrderState.COMPLETED.getStringValue();
        }

        StateChangeContext orderStateChangeContext = stateChangeContextBuilder
                .build(orderStateChangeAspect.getChangeEntityDescriber(), order, targetState);

        orderStateChangeAspect.changeState(orderStateChangeContext);

        OrderResponse orderResponse = new OrderResponse(dashboardKanbanDataProvider.getOrder(orderId));

        List<ErrorMessage> errors = Lists.newArrayList();

        if (!orderStateChangeContext.getAllMessages().isEmpty()) {
            for (Entity entity : orderStateChangeContext.getAllMessages()) {
                errors.add(new ErrorMessage(MessagesUtil.getKey(entity), MessagesUtil.getArgs(entity)));
            }
        }

        if (!errors.isEmpty()) {
            String errorMessages = errors.stream().map(errorMessage -> translationService.translate(errorMessage.getMessage(),
                    LocaleContextHolder.getLocale(), errorMessage.getVars())).collect(Collectors.joining(", "));

            orderResponse.setMessage(translationService.translate("orders.order.orderStates.error",
                    LocaleContextHolder.getLocale(), errorMessages));
        }

        return orderResponse;
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }

}
