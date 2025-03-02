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
package com.sleektiv.mes.deliveriesToMaterialFlow.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.deliveries.constants.DeliveredProductFields;
import com.sleektiv.mes.deliveries.constants.DeliveredProductMultiPositionFields;
import com.sleektiv.mes.deliveriesToMaterialFlow.constants.DeliveriesToMaterialFlowConstants;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(DeliveriesToMaterialFlowConstants.PLUGIN_IDENTIFIER)
public class DeliveredProductAddMultiListenersAspect {

    @Pointcut("execution(private com.sleektiv.model.api.Entity com.sleektiv.mes.deliveries.listeners.DeliveredProductAddMultiListeners.createDeliveredProduct(..))"
            + "&& args(position, deliveredProductDD)")
    public void createDeliveredProduct(Entity position, DataDefinition deliveredProductDD) {
    }

    @Around("createDeliveredProduct(position, deliveredProductDD)")
    public Entity aroundCreateDeliveredProduct(final ProceedingJoinPoint pjp, Entity position, DataDefinition deliveredProductDD)
            throws Throwable {
        Entity deliveredProduct = (Entity) pjp.proceed();
        deliveredProduct.setField(DeliveredProductFields.EXPIRATION_DATE,
                position.getStringField(DeliveredProductMultiPositionFields.EXPIRATION_DATE));
        return deliveredProduct;
    }

}
