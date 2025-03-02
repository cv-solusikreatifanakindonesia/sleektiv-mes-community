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
package com.sleektiv.security.internal.api;

import java.util.Date;

import com.sleektiv.model.api.Entity;

public class SleektivUser {

    private String login;

    private String email;

    private String firstName;

    private String lastName;

    private Entity group;

    private Date lastActivity;

    public SleektivUser() {
        // nothing
    }

    public SleektivUser(final String login, final String email, final String firstName, final String lastName, final Entity group,
            final Date lastActivity) {
        this.login = login;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.group = group;
        this.lastActivity = new Date(lastActivity.getTime());
    }

    public SleektivUser(final Entity userEntity) {
        this.login = userEntity.getStringField("userName");
        this.email = userEntity.getStringField("email");
        this.firstName = userEntity.getStringField("firstName");
        this.lastName = userEntity.getStringField("lastName");
        this.group = userEntity.getBelongsToField("group");
        this.lastActivity = (Date) userEntity.getField("lastActivity");
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Entity getGroup() {
        return group;
    }

    public Date getLastActivity() {
        if (lastActivity == null) {
            return null;
        }
        return new Date(lastActivity.getTime());
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setGroup(final Entity group) {
        this.group = group;
    }

    public void setLastActivity(final Date lastActivity) {
        if (lastActivity == null) {
            this.lastActivity = null;
        } else {
            this.lastActivity = new Date(lastActivity.getTime());
        }
    }

}
