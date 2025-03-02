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
package com.sleektiv.plugins.dictionaries.internal.hooks;

import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.constants.DictionaryFields;
import com.sleektiv.model.constants.DictionaryItemFields;
import com.sleektiv.model.constants.SleektivModelConstants;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.sleektiv.model.constants.DictionaryItemFields.TECHNICAL_CODE;

@Service
public class DictionaryItemDetailsHooks {

    



    private static final String L_STATES = "states";

    private static final String L_DEACTIVATE = "deactivate";

    private static final String L_ACTIVATE = "activate";

    private static final String L_IS_INTEGER = "isInteger";

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void blockedActivationOptionWhenDictionaryWasAddFromSystem(final ViewDefinitionState view) {
        FormComponent dictionaryItemForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        Long dictionaryItemId = dictionaryItemForm.getEntityId();

        if (dictionaryItemId == null) {
            return;
        }

        Entity dictionaryItem = getDictionaryItem(dictionaryItemId);

        if (StringUtils.isNotEmpty(dictionaryItem.getStringField(TECHNICAL_CODE))) {
            changedEnabledButton(view, false);
        }
    }

    protected void changedEnabledButton(final ViewDefinitionState view, final boolean enabled) {
        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        RibbonActionItem deactivateButton = window.getRibbon().getGroupByName(L_STATES).getItemByName(L_DEACTIVATE);
        RibbonActionItem activateButton = window.getRibbon().getGroupByName(L_STATES).getItemByName(L_ACTIVATE);

        deactivateButton.setEnabled(enabled);
        deactivateButton.requestUpdate(true);
        activateButton.setEnabled(enabled);
        activateButton.requestUpdate(true);
    }

    public void disableNameEdit(final ViewDefinitionState view) {
        FormComponent dictionaryItemForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        FieldComponent nameFieldComponent = (FieldComponent) view.getComponentByReference(DictionaryItemFields.NAME);

        if (dictionaryItemForm.getEntityId() == null) {
            return;
        } else {
            nameFieldComponent.setEnabled(false);
        }
    }

    public void disableDictionaryItemFormForExternalItems(final ViewDefinitionState state) {
        FormComponent dictionaryItemForm = (FormComponent) state.getComponentByReference(SleektivViewConstants.L_FORM);

        Long dictionaryItemId = dictionaryItemForm.getEntityId();

        if (dictionaryItemId == null) {
            dictionaryItemForm.setFormEnabled(true);

            return;
        }

        Entity dictionaryItem = getDictionaryItem(dictionaryItemId);

        if (Objects.isNull(dictionaryItem)) {
            return;
        }

        String externalNumber = dictionaryItem.getStringField(DictionaryItemFields.EXTERNAL_NUMBER);

        dictionaryItemForm.setFormEnabled(Objects.isNull(externalNumber));
    }

    public void showIntegerCheckbox(final ViewDefinitionState state) {
        FormComponent form = (FormComponent) state.getComponentByReference(SleektivViewConstants.L_FORM);

        String dictionaryName = form.getEntity().getBelongsToField(DictionaryItemFields.DICTIONARY)
                .getStringField(DictionaryFields.NAME);

        if (SleektivModelConstants.DICTIONARY_UNITS.equals(dictionaryName)) {
            state.getComponentByReference(L_IS_INTEGER).setVisible(true);
        }
    }

    private Entity getDictionaryItem(final Long dictionaryItemId) {
        return dataDefinitionService
                .get(SleektivModelConstants.PLUGIN_IDENTIFIER, SleektivModelConstants.MODEL_DICTIONARY_ITEM)
                .get(dictionaryItemId);
    }

}
