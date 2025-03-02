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

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.internal.ComponentDefinition;
import com.sleektiv.view.internal.api.ContainerState;
import com.sleektiv.view.internal.states.ContainerStateMock;

public class ContainerPatternMock extends AbstractContainerPattern {

    private final ContainerState containerState;

    public ContainerPatternMock(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
        this.containerState = new ContainerStateMock();
    }

    public ContainerPatternMock(final ComponentDefinition componentDefinition, final ContainerState containerState) {
        super(componentDefinition);
        this.containerState = containerState;
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return containerState;
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
