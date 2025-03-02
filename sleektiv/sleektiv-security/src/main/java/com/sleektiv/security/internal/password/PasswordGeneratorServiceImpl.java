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
package com.sleektiv.security.internal.password;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.sleektiv.security.api.PasswordGeneratorService;

@Service
public class PasswordGeneratorServiceImpl implements PasswordGeneratorService {

    private static final String PASSWORD_GENERATOR_CHARS = "123456789wertyuipasdfghjkzxcvbnmQWERTYUASDFGHJKZXCVBNM!*&%$#@";

    @Override
    public String generatePassword() {
        return generatePassword(DEFAULT_PASSWORD_LENGTH);
    }

    @Override
    public String generatePassword(final int length) {
        return RandomStringUtils.random(length, PASSWORD_GENERATOR_CHARS);
    }

}
