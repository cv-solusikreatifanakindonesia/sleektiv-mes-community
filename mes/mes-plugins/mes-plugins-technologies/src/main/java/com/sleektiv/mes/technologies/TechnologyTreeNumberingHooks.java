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
package com.sleektiv.mes.technologies;

import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityTree;
import com.sleektiv.model.api.utils.TreeNumberingService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sleektiv.mes.technologies.states.constants.TechnologyState.DRAFT;

@Service
public class TechnologyTreeNumberingHooks {

    @Autowired
    private TreeNumberingService treeNumberingService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    private static final Logger LOG = LoggerFactory.getLogger(TechnologyTreeNumberingHooks.class);

    public void rebuildTreeNumbering(final ViewDefinitionState view) {
        FormComponent form = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Long technologyId = form.getEntityId();
        if (technologyId == null) {
            return;
        }

        Entity technology = getTechnologyById(technologyId);
        if (!isDraftTechnology(technology)) {
            return;
        }

        EntityTree technologyTree = technology.getTreeField(TechnologyFields.OPERATION_COMPONENTS);
        if (technologyTree == null || technologyTree.getRoot() == null) {
            return;
        }

        debug("Fire tree node number generator for tecnology with id = " + technologyId);
        treeNumberingService.generateTreeNumbers(technologyTree);
    }

    private Entity getTechnologyById(final Long id) {
        return dataDefinitionService.get(TechnologiesConstants.PLUGIN_IDENTIFIER, TechnologiesConstants.MODEL_TECHNOLOGY).get(id);
    }

    private boolean isDraftTechnology(final Entity technology) {
        return DRAFT.getStringValue().equals(technology.getStringField(TechnologyFields.STATE));
    }

    private void debug(final String message) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(message);
        }
    }
}
