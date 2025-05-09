/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv Framework
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
package com.sleektiv.customTranslation.internal.aop;

import java.util.Locale;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.sleektiv.customTranslation.constants.CustomTranslationContants;
import com.sleektiv.plugin.api.RunIfEnabled;

@Aspect
@Configurable
@RunIfEnabled(CustomTranslationContants.PLUGIN_IDENTIFIER)
public class TranslationServiceOverrideAspect {

    @Autowired
    private TranslationServiceOverrideUtil translationServiceOverrideUtil;

    @Pointcut("execution(private String com.sleektiv.localization.internal.TranslationServiceImpl.translateWithError(..)) "
            + "&& args(messageCode, locale, args)")
    public void translateWithErrorExecution(final String messageCode, final Locale locale, final String[] args) {
    }

    @Around("translateWithErrorExecution(messageCode, locale, args)")
    public String aroundTranslateWithErrorExecution(final ProceedingJoinPoint pjp, final String messageCode, final Locale locale,
            final String[] args) throws Throwable {
        String result = null;

        if (translationServiceOverrideUtil.shouldOverrideTranslation(messageCode, locale)) {
            result = translationServiceOverrideUtil.getCustomTranslation(messageCode, locale, args);

            if (result == null) {
                result = (String) pjp.proceed();
            }
        } else {
            result = (String) pjp.proceed();
        }

        return result;
    }

}
