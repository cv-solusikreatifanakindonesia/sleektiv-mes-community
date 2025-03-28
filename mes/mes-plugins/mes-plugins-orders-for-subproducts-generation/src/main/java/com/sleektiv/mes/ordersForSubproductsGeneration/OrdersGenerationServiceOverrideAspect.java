package com.sleektiv.mes.ordersForSubproductsGeneration;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.masterOrders.GenerationOrderResult;
import com.sleektiv.mes.masterOrders.SubOrderErrorHolder;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.orders.constants.OrdersConstants;
import com.sleektiv.mes.ordersForSubproductsGeneration.constants.OrdersForSubproductsGenerationConstans;
import com.sleektiv.mes.technologies.constants.ProductStructureTreeNodeFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
@RunIfEnabled(OrdersForSubproductsGenerationConstans.PLUGIN_IDENTIFIER)
public class OrdersGenerationServiceOverrideAspect {

    public static final String PARAMETER_AUTOMATICALLY_GENERATE_ORDERS_FOR_COMPONENTS = "automaticallyGenerateOrdersForComponents";

    public static final String PARAMETER_ORDERS_GENERATED_BY_COVERAGE = "ordersGeneratedByCoverage";

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private OrdersForSubproductsGenerationService ordersForSubproductsGenerationService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Pointcut("execution(public void com.sleektiv.mes.masterOrders.OrdersGenerationService.generateSubOrders(..)) "
            + "&& args(result, order)")
    public void generateSubOrders(GenerationOrderResult result, Entity order) {

    }

    @Around("generateSubOrders(result, order)")
    public void aroundGenerateSubOrders(final ProceedingJoinPoint pjp, GenerationOrderResult result, Entity order)
            throws Throwable {

        try {
            Entity parameter = parameterService.getParameter();

            if (parameter.getBooleanField(PARAMETER_AUTOMATICALLY_GENERATE_ORDERS_FOR_COMPONENTS)) {

                addProductsWithCheckedTechnologiesInfo(result, order);
                if (parameter.getBooleanField(PARAMETER_ORDERS_GENERATED_BY_COVERAGE)) {
                    ordersForSubproductsGenerationService.generateOrdersByCoverage(order);
                } else {
                    ordersForSubproductsGenerationService.generateOrders(order);
                }
            }
            List<Entity> orders = geSubOrders(order.getId());
            if (orders.isEmpty()) {
                result.addOrderWithNoGeneratedSubOrders(order.getStringField(OrderFields.NUMBER));
            } else {
                result.addOrderWithGeneratedSubOrders(order.getStringField(OrderFields.NUMBER));
            }
        } catch (Exception exc) {
            result.addOrderWithoutGeneratedSubOrders(new SubOrderErrorHolder(order.getStringField(OrderFields.NUMBER), ""));
        }
    }

    private void addProductsWithCheckedTechnologiesInfo(GenerationOrderResult result, Entity order) {
        List<Entity> products = ordersForSubproductsGenerationService.getProductNodesWithCheckedTechnologies(null, order);
        if (!products.isEmpty()) {
            products.stream()
                    .map(node -> node.getBelongsToField(ProductStructureTreeNodeFields.PRODUCT).getStringField(
                            ProductFields.NUMBER)).forEach(result::addProductWithoutAcceptedTechnology);
        }
    }

    private List<Entity> geSubOrders(final Long orderID) {
        String sql = "SELECT o FROM #orders_order AS o WHERE o.root = :orderID";
        return getOrderDD().find(sql).setLong("orderID", orderID).list().getEntities();
    }

    private DataDefinition getOrderDD() {
        return dataDefinitionService.get(OrdersConstants.PLUGIN_IDENTIFIER, OrdersConstants.MODEL_ORDER);
    }
}
