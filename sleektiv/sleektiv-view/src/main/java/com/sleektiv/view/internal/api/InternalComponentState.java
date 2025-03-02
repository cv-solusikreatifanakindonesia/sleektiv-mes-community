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
package com.sleektiv.view.internal.api;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.sleektiv.view.api.ComponentState;

public interface InternalComponentState extends ComponentState {

    /**
     * Initialize this component state using data from client. <b>For internal usage only</b>
     * 
     * @param json
     *            data from client
     * @param locale
     *            current localization
     * @throws JSONException
     *             when data from client contains errors
     */
    void initialize(JSONObject json, Locale locale) throws JSONException;

    /**
     * Renders this component state back to client. <b>For internal usage only</b>
     * 
     * @return data to client
     * @throws JSONException
     *             when data for client contains errors
     */
    JSONObject render() throws JSONException;

    /**
     * Returns true if element defined by this component is permanently disabled
     * 
     * @return true if element defined by this component is permanently disabled
     */
    boolean isPermanentlyDisabled();

    /**
     * Defines if element defined by this component should be permanently disabled. Permanently disabled means that you can not
     * enable them using {@link ComponentState.setEnabled()}
     * 
     * @param permanentlyDisabled
     *            true if element defined by this component should be permanently disabled
     */
    void setPermanentlyDisabled(boolean permanentlyDisabled);

}
