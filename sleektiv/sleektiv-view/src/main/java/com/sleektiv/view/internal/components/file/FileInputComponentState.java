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
package com.sleektiv.view.internal.components.file;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.sleektiv.model.api.file.FileUtils;
import com.sleektiv.view.internal.components.FieldComponentPattern;
import com.sleektiv.view.internal.components.FieldComponentState;
import com.sleektiv.view.utils.FileAccessUtils;

import java.util.Objects;

public class FileInputComponentState extends FieldComponentState {

    public static final String JSON_FILE_NAME = "fileName";

    public static final String JSON_FILE_URL = "fileUrl";

    public static final String JSON_FILE_LAST_MODIFICATION_DATE = "fileLastModificationDate";

    public FileInputComponentState(final FieldComponentPattern pattern) {
        super(pattern);
    }

    @Override
    protected JSONObject renderContent() throws JSONException {
        JSONObject json = super.renderContent();

        if (Objects.nonNull(getFieldValue()) && StringUtils.hasText((String) getFieldValue())) {
            String url = FileUtils.getInstance().getUrl((String) getFieldValue());
            String securedUrl = null;

            if (Objects.nonNull(url)) {
                securedUrl = FileAccessUtils.getInstance().getFileAccessService().createSecuredUrl(url);
            }

            json.put(JSON_FILE_NAME, FileUtils.getInstance().getName((String) getFieldValue()));
            json.put(JSON_FILE_URL, securedUrl);
            json.put(JSON_FILE_LAST_MODIFICATION_DATE, FileUtils.getInstance().getLastModificationDate((String) getFieldValue()));
        } else {
            json.put(JSON_FILE_NAME, "");
            json.put(JSON_FILE_URL, "");
            json.put(JSON_FILE_LAST_MODIFICATION_DATE, "");
        }

        return json;
    }

}
