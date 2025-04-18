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
package com.sleektiv.mes.cmmsMachineParts.states.constants;

public final class MaintenanceEventStateChangeFields {

    public static final String PLANNED_EVENT_TYPE = "plannedEventType";

    public static final String PLANNED_EVENT_TYPE_REQUIRED = "plannedEventTypeRequired";

    private MaintenanceEventStateChangeFields() {
    }

    public static final String STATUS = "status";

    public static final String MAINTENANCE_EVENT = "maintenanceEvent";

    public static final String TARGET_STATE = "targetState";

    public static final String SOURCE_STATE = "sourceState";

    public static final String MESSAGES = "messages";

    public static final String PHASE = "phase";

    public static final String COMMENT = "comment";

    public static final String COMMENT_REQUIRED = "commentRequired";

    public static final String DATE_AND_TIME = "dateAndTime";

}
