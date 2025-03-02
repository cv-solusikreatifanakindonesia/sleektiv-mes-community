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
package com.sleektiv.security.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import com.sleektiv.security.internal.permissionEvaluators.SleektivPermisionEvaluator;

public class SleektivGlobalPermissionEvaluator implements PermissionEvaluator {

    private final Map<String, SleektivPermisionEvaluator> evaluators = new HashMap<String, SleektivPermisionEvaluator>();

    public void setSleektivEvaluators(final Set<SleektivPermisionEvaluator> evaluatorsSet) {
        for (SleektivPermisionEvaluator evaluator : evaluatorsSet) {
            evaluators.put(evaluator.getTargetType(), evaluator);
        }
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Object domainObject, final Object permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPermission(final Authentication authentication, final Serializable targetId, final String targetType,
            final Object permission) {

        SleektivPermisionEvaluator evaluator = evaluators.get(targetType);
        if (evaluator == null) {
            throw new IllegalArgumentException("there is no evaluator for target type '" + targetType + "'");
        }

        return evaluator.hasPermission(authentication, (String) permission, (String) targetId);
    }

}
