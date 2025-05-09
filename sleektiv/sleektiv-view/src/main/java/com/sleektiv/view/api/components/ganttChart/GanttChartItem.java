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
package com.sleektiv.view.api.components.ganttChart;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @since 0.4.3
 */
public interface GanttChartItem {

    String getRowName();

    String getName();

    GanttChartItemTooltip getTooltip();

    Long getEntityId();

    String getDateFrom();

    String getDateTo();

    double getFrom();

    double getTo();

    void addBackgroundStrip(final GanttChartItemStrip bgStrip);

    JSONObject getAsJson() throws JSONException;

}
