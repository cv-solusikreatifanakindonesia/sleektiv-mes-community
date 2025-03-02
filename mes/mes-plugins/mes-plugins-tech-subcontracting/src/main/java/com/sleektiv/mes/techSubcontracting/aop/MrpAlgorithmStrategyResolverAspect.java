/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 *
 * This file is part of Sleektiv.
 *
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.techSubcontracting.aop;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.sleektiv.mes.techSubcontracting.constants.TechSubcontractingConstants;
import com.sleektiv.mes.technologies.MrpAlgorithmStrategy;
import com.sleektiv.mes.technologies.constants.MrpAlgorithm;
import com.sleektiv.mes.technologies.dto.OperationProductComponentHolder;
import com.sleektiv.mes.technologies.dto.OperationProductComponentWithQuantityContainer;
import com.sleektiv.plugin.api.PluginUtils;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@RunIfEnabled(TechSubcontractingConstants.PLUGIN_IDENTIFIER)
public abstract class MrpAlgorithmStrategyResolverAspect {

    protected abstract MrpAlgorithmStrategy getAlgorithmService();

    @Pointcut("execution(private java.util.Map<Long, java.math.BigDecimal> com.sleektiv.mes.technologies.ProductQuantitiesServiceImpl.getProductWithQuantities(..)) "
            + "&& args(productComponentWithQuantities, nonComponents, mrpAlgorithm, operationProductComponentModelName)")
    public void getProductsMethodExecution(final OperationProductComponentWithQuantityContainer productComponentWithQuantities,
            final Set<OperationProductComponentHolder> nonComponents, final MrpAlgorithm mrpAlgorithm,
            final String operationProductComponentModelName) {
    }

    @SuppressWarnings("unchecked")
    @Around("getProductsMethodExecution(productComponentWithQuantities, nonComponents, mrpAlgorithm, operationProductComponentModelName)")
    public Map<Long, BigDecimal> aroundGetProductsMethodExecution(final ProceedingJoinPoint pjp,
            final OperationProductComponentWithQuantityContainer productComponentWithQuantities,
            final Set<OperationProductComponentHolder> nonComponents, final MrpAlgorithm mrpAlgorithm,
            final String operationProductComponentModelName) throws Throwable {
        if (PluginUtils.isEnabled(TechSubcontractingConstants.PLUGIN_IDENTIFIER)
                && getAlgorithmService().isApplicableFor(mrpAlgorithm)) {
            return getAlgorithmService().perform(productComponentWithQuantities, nonComponents, mrpAlgorithm,
                    operationProductComponentModelName);
        } else {
            return (Map<Long, BigDecimal>) pjp.proceed();
        }
    }

}
