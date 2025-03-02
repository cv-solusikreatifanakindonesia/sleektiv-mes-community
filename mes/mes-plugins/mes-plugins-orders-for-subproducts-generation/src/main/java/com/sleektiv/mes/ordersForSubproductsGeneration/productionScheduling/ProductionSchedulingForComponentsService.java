package com.sleektiv.mes.ordersForSubproductsGeneration.productionScheduling;

import com.google.common.collect.Maps;
import com.sleektiv.mes.ordersForSubproductsGeneration.constants.OrderFieldsOFSPG;
import com.sleektiv.model.api.Entity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProductionSchedulingForComponentsService {

    public Map<Integer, OrdersByLevel> mapToOrdersByLevel(final List<Entity> orders) {
        Map<Integer, OrdersByLevel> ordersByLevel = Maps.newHashMap();
        orders.forEach(order -> {
            Integer level = order.getIntegerField(OrderFieldsOFSPG.LEVEL);
            if (ordersByLevel.containsKey(level)) {
                OrdersByLevel ords = ordersByLevel.get(level);
                ords.pushOrder(order);
            } else {
                OrdersByLevel ords = new OrdersByLevel(level);
                ords.pushOrder(order);
                if(Objects.isNull(level)){
                    level = OrdersByLevel.ROOT_LEVEL;
                }
                ordersByLevel.put(level, ords);
            }
        });
        return ordersByLevel;
    }
}
