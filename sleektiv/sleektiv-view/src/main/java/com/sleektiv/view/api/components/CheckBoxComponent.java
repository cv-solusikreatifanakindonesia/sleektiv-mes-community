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
package com.sleektiv.view.api.components;

/**
 * CheckBox component
 * 
 * @author Marcin Kubala
 * @since 1.2.1
 */
public interface CheckBoxComponent extends FieldComponent {

    /**
     * Mark this component as (un)checked.
     * 
     * @param value
     *            true if you want to mark this component as checked.
     */
    void setChecked(final boolean value);

    /**
     * Check if this component is checked
     * 
     * @return true if this component is checked
     */
    boolean isChecked();

}
