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

import static com.sleektiv.mes.technologies.states.constants.TechnologyStateChangeFields.STATUS;

import java.util.Objects;
import java.util.Optional;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.states.constants.StateChangeStatus;
import com.sleektiv.mes.states.service.client.util.StateChangeHistoryService;
import com.sleektiv.mes.technologies.constants.TechnologiesConstants;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.technologies.criteriaModifiers.QualityCardCriteriaModifiers;
import com.sleektiv.mes.technologies.criteriaModifiers.TechnologyDetailsCriteriaModifiers;
import com.sleektiv.mes.technologies.states.constants.TechnologyState;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.CustomRestriction;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.components.TreeComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.components.lookup.FilterValueHolder;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class TechnologiesAttachmentsHooks {

    public void onBeforeRender(final ViewDefinitionState view) throws JSONException {
        String technologiesIds = view.getJsonContext().get("window.mainTab.technology.gridLayout.technologiesIds")
                .toString();

        FieldComponent technologyMultiUploadLocaleField = (FieldComponent) view.getComponentByReference("technologyMultiUploadLocale");
        FieldComponent technologiesIdsField = (FieldComponent) view.getComponentByReference("technologiesIds");
        technologyMultiUploadLocaleField.setFieldValue(LocaleContextHolder.getLocale());
        technologiesIdsField.setFieldValue(technologiesIds);
    }

}
