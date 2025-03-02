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
package com.sleektiv.view.internal.components.layout;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.internal.ComponentDefinition;
import com.sleektiv.view.internal.api.ComponentPattern;
import com.sleektiv.view.internal.components.EmptyContainerState;
import com.sleektiv.view.internal.patterns.AbstractComponentPattern;
import com.sleektiv.view.internal.patterns.AbstractContainerPattern;

public abstract class AbstractLayoutPattern extends AbstractContainerPattern {

    public AbstractLayoutPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    public final void addFieldEntityIdChangeListener(final String field, final ComponentPattern listener) {
        AbstractComponentPattern parent = (AbstractComponentPattern) this.getParent();
        parent.addFieldEntityIdChangeListener(field, listener);
    }

    public final void addScopeEntityIdChangeListener(final String field, final ComponentPattern listener) {
        AbstractComponentPattern parent = (AbstractComponentPattern) this.getParent();
        parent.addScopeEntityIdChangeListener(field, listener);
    }

    @Override
    protected ComponentState getComponentStateInstance() {
        return new EmptyContainerState();
    }

    @Override
    public final String getFunctionalPath() {
        return getParent().getFunctionalPath();
    }
}
