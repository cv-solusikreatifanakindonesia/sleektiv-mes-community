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
package com.sleektiv.mes.technologies.hooks;

import org.springframework.stereotype.Service;

import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologyReferenceTechnologyComponentDetailsHooks {

    public void disabledSaveBackButton(final ViewDefinitionState viewDefinitionState) {
        WindowComponent window = (WindowComponent) viewDefinitionState.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonGroup actionsGroup = (RibbonGroup) window.getRibbon().getGroupByName("actions");
        RibbonActionItem saveBack = (RibbonActionItem) actionsGroup.getItemByName("saveBack");

        LookupComponent technology = (LookupComponent) viewDefinitionState.getComponentByReference("technology");
        if (technology.getEntity() == null) {
            saveBack.setEnabled(false);
        } else {
            saveBack.setEnabled(true);
        }
        saveBack.requestUpdate(true);
    }
}
