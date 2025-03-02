package com.sleektiv.mes.basicProductionCounting.aop;

import com.sleektiv.mes.basicProductionCounting.BasicProductionCountingService;
import com.sleektiv.mes.basicProductionCounting.constants.BasicProductionCountingConstants;
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
@RunIfEnabled(BasicProductionCountingConstants.PLUGIN_IDENTIFIER)
public class OrderCreationServiceAspect {

    @Autowired
    private BasicProductionCountingService basicProductionCountingService;

    @Pointcut("execution(public void com.sleektiv.mes.orders.controllers.OrderCreationService.fillFlow(..)) "
            + "&& args(productionCountingQuantities, order)")
    public void fillFlow(final List<Entity> productionCountingQuantities, final Entity order) {
    }

    @Around("fillFlow(productionCountingQuantities, order)")
    public void aroundFillFlow(final ProceedingJoinPoint pjp, final List<Entity> productionCountingQuantities, final Entity order)
            throws Throwable {

        basicProductionCountingService.fillFlow(productionCountingQuantities, order);

    }

}
