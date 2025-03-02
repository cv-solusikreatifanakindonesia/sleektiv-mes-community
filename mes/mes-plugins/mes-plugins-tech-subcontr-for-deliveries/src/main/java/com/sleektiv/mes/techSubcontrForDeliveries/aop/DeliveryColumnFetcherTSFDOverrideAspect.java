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
package com.sleektiv.mes.techSubcontrForDeliveries.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.deliveries.print.DeliveryProduct;
import com.sleektiv.mes.techSubcontrForDeliveries.constants.TechSubcontrForDeliveriesConstants;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(TechSubcontrForDeliveriesConstants.PLUGIN_IDENTIFIER)
public class DeliveryColumnFetcherTSFDOverrideAspect {

    @Autowired
    private DeliveryColumnFetcherTSFDOverrideUtil deliveryColumnFetcherTSFDOverrideUtil;

    @Pointcut("execution(private boolean com.sleektiv.mes.deliveries.print.DeliveryColumnFetcher.compareProducts(..)) "
            + "&& args(deliveryProduct, deliveredProduct)")
    public void compareProductsExecution(final DeliveryProduct deliveryProduct, final Entity deliveredProduct) {
    }

    @Around("compareProductsExecution(deliveryProduct, deliveredProduct)")
    public boolean aroundCompareProductsExecution(final ProceedingJoinPoint pjp, final DeliveryProduct deliveryProduct,
            final Entity deliveredProduct) throws Throwable {
        if (deliveryColumnFetcherTSFDOverrideUtil.shouldOverride()) {
            return deliveryColumnFetcherTSFDOverrideUtil.compareProductsAndOperation(deliveryProduct, deliveredProduct);
        } else {
            return (Boolean) pjp.proceed();
        }
    }

}
