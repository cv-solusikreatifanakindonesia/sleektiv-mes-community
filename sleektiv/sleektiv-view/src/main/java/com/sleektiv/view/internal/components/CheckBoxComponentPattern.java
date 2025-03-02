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

import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.internal.ComponentDefinition;
import com.sleektiv.view.internal.ComponentOption;

public final class CheckBoxComponentPattern extends FieldComponentPattern {

    private static final String JSP_PATH = "elements/checkBox.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.CheckBox";

    private boolean textRepresentationOnDisabled;

    private String align = "left";

    public CheckBoxComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new CheckBoxComponentState(this);
    }

    @Override
    protected void initializeComponent() throws JSONException {
        super.initializeComponent();
        for (ComponentOption option : getOptions()) {
            if ("textRepresentationOnDisabled".equals(option.getType())) {
                textRepresentationOnDisabled = Boolean.parseBoolean(option.getValue());
            } else if ("align".equals(option.getType())) {
                align = option.getValue();
            } else if (!"labelWidth".equals(option.getType())) {
                throw new IllegalStateException("Unknown option for checkbox: " + option.getType());
            }
        }
    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = super.getJspOptions(locale);
        options.put("textRepresentationOnDisabled", textRepresentationOnDisabled);
        options.put("align", align);
        return options;
    }

    @Override
    protected JSONObject getJsOptions(final Locale locale) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject jsonTranslations = new JSONObject();
        jsonTranslations.put("true", getTranslationService().translate("sleektivView.true", locale));
        jsonTranslations.put("false", getTranslationService().translate("sleektivView.false", locale));
        json.put("translations", jsonTranslations);
        return json;
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
