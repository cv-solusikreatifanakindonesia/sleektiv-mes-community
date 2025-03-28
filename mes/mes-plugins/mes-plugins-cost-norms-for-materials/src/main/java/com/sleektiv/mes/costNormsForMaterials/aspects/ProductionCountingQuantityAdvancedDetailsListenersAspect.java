package com.sleektiv.mes.costNormsForMaterials.aspects;

import com.sleektiv.mes.basicProductionCounting.constants.ProductionCountingQuantityFields;
import com.sleektiv.mes.costNormsForMaterials.constants.CostNormsForMaterialsConstants;
import com.sleektiv.mes.costNormsForMaterials.constants.OrderFieldsCNFM;
import com.sleektiv.mes.costNormsForMaterials.orderRawMaterialCosts.OrderMaterialsCostDataGenerator;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

@Aspect
@Configurable
@Service
@RunIfEnabled(CostNormsForMaterialsConstants.PLUGIN_IDENTIFIER)
public class ProductionCountingQuantityAdvancedDetailsListenersAspect {

    @Autowired
    private OrderMaterialsCostDataGenerator orderMaterialsCostDataGenerator;

    @Pointcut("execution(public void com.sleektiv.mes.basicProductionCounting.listeners.ProductionCountingQuantityAdvancedDetailsListeners.afterSave(..)) "
            + "&& args(productionCountingQuantity)")
    public void afterSave(final Entity productionCountingQuantity) {

    }

    @Around("afterSave(productionCountingQuantity)")
    public void aroundAfterSave(final ProceedingJoinPoint pjp, final Entity productionCountingQuantity) throws Throwable {
        Entity order = productionCountingQuantity.getBelongsToField(ProductionCountingQuantityFields.ORDER);
        Entity orderDb = order.getDataDefinition().get(order.getId());
        updateMaterialCosts(order);
    }

    private Entity updateMaterialCosts(Entity order) {
        List<Entity> orderMaterialsCosts = orderMaterialsCostDataGenerator.generateUpdatedMaterialsListFor(order);
        order.setField(OrderFieldsCNFM.TECHNOLOGY_INST_OPER_PRODUCT_IN_COMPS, orderMaterialsCosts);
        return order.getDataDefinition().save(order);
    }
}
