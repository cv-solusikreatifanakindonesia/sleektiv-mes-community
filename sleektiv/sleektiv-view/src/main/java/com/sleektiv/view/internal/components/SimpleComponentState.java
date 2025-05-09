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

import org.json.JSONException;
import org.json.JSONObject;

import com.sleektiv.view.internal.states.AbstractComponentState;

public final class SimpleComponentState extends AbstractComponentState {

    private String value;

    @Override
    protected void initializeContent(final JSONObject json) throws JSONException {
        value = json.getString(JSON_VALUE);
    }

    @Override
    public void setFieldValue(final Object value) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = value.toString();
        }
        requestRender();
        requestUpdateState();
    }

    @Override
    public Object getFieldValue() {
        return value;
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_VALUE, value);
        return json;
    }

}
