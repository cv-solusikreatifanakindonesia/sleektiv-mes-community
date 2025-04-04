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
package com.sleektiv.view.internal.ribbon.model;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sleektiv.view.api.ribbon.RibbonComboBox;

public class RibbonComboBoxImpl extends RibbonActionItemImpl implements RibbonComboBox {

    private List<String> options = new LinkedList<String>();

    @Override
    public void addOption(final String option) {
        options.add(option);
    }

    @Override
    public JSONObject getAsJson() throws JSONException {
        JSONObject obj = super.getAsJson();
        JSONArray optionsArray = new JSONArray();
        for (String option : options) {
            optionsArray.put(option);
        }
        obj.put("options", optionsArray);
        return obj;
    }

    @Override
    public InternalRibbonActionItem getCopy() {
        RibbonComboBoxImpl copy = new RibbonComboBoxImpl();
        copyFields(copy);
        for (String option : options) {
            copy.addOption(option);
        }
        return copy;
    }
}
