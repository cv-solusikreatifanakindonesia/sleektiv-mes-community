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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.mes.techSubcontracting.constants.TechSubcontractingConstants;
import com.sleektiv.mes.technologies.constants.MrpAlgorithm;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(TechSubcontractingConstants.PLUGIN_IDENTIFIER)
public class MaterialRequirementServiceImplTSOverrideAspect {

    @Autowired
    private MaterialRequirementServiceImplTSOverrideUtil materialRequirementServiceImplTSOverrideUtil;

    @Pointcut("execution(public com.sleektiv.mes.technologies.constants.MrpAlgorithm com.sleektiv.mes.materialRequirements.MaterialRequirementServiceImpl.getDefaultMrpAlgorithm(..))")
    public void getDefaultMrpAlgorithmExecution() {
    }

    @Around("getDefaultMrpAlgorithmExecution()")
    public MrpAlgorithm aroundGetDefaultMrpAlgorithmExecution(final ProceedingJoinPoint pjp) {
        return materialRequirementServiceImplTSOverrideUtil.getDefaultMrpAlgorithm();
    }

}
