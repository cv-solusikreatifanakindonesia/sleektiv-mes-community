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
package com.sleektiv.view.internal.components;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.internal.ComponentDefinition;
import com.sleektiv.view.internal.patterns.AbstractComponentPattern;

public class SeparatorLinePattern extends AbstractComponentPattern {

    private static final String JS_OBJECT = "QCD.components.containers.layout.SeperatorLine";

    private static final String JSP_PATH = "elements/separatorLine.jsp";

    private static final String JS_PATH = "/sleektivView/public/js/crud/qcd/components/elements/separatorLine.js";

    public SeparatorLinePattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    protected ComponentState getComponentStateInstance() {
        return new EmptyContainerState();
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }
}
