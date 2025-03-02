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
package com.sleektiv.view.internal.patterns;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.Locale;

import org.json.JSONObject;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.view.internal.ComponentDefinition;
import com.sleektiv.view.internal.api.ComponentPattern;
import com.sleektiv.view.internal.api.ContainerPattern;
import com.sleektiv.view.internal.api.ContextualHelpService;
import com.sleektiv.view.internal.api.InternalViewDefinition;
import com.sleektiv.view.internal.api.ViewDefinition;

public abstract class AbstractPatternTest {

    public ComponentDefinition getComponentDefinition(final String name, final ViewDefinition viewDefinition) {
        return getComponentDefinition(name, null, null, null, viewDefinition);
    }

    public ComponentDefinition getComponentDefinition(final String name, final ContainerPattern parent,
            final ViewDefinition viewDefinition) {
        return getComponentDefinition(name, null, null, parent, viewDefinition);
    }

    public ComponentDefinition getComponentDefinition(final String name, final String fieldPath, final String sourceFieldPath,
            final ContainerPattern parent, final ViewDefinition viewDefinition) {
        ComponentDefinition componentDefinition = new ComponentDefinition();
        componentDefinition.setName(name);
        componentDefinition.setFieldPath(fieldPath);
        componentDefinition.setSourceFieldPath(sourceFieldPath);
        componentDefinition.setParent(parent);
        TranslationService translationService = mock(TranslationService.class);
        componentDefinition.setTranslationService(translationService);
        ContextualHelpService contextualHelpService = mock(ContextualHelpService.class);
        componentDefinition.setContextualHelpService(contextualHelpService);
        if (viewDefinition != null) {
            componentDefinition.setViewDefinition(viewDefinition);
        } else {
            componentDefinition.setViewDefinition(mock(InternalViewDefinition.class));
        }
        return componentDefinition;
    }

    public JSONObject getJsOptions(final ComponentPattern pattern) throws Exception {
        Method method = AbstractComponentPattern.class.getDeclaredMethod("getJsOptions", Locale.class);
        method.setAccessible(true);
        return (JSONObject) method.invoke(pattern, Locale.ENGLISH);
    }

}
