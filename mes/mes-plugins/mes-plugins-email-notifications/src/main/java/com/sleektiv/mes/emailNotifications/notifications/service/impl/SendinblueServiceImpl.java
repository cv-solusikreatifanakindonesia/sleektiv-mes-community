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
package com.sleektiv.mes.emailNotifications.notifications.service.impl;

import com.sleektiv.mes.emailNotifications.notifications.constants.SendingStatus;
import com.sleektiv.mes.emailNotifications.notifications.sendinblue.TransactionalTemplatesApi;
import com.sleektiv.mes.emailNotifications.notifications.service.SendinblueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibModel.SendEmail;

@Service
public class SendinblueServiceImpl implements SendinblueService {

    @Value("${sendinblueApiKey}")
    private String apiKey;

    @Override
    public SendingStatus sendTemplateEmail(final Long templateId, final SendEmail sendEmail) {
        SendingStatus result = SendingStatus.sent;

        ApiClient defaultClient = Configuration.getDefaultApiClient();

        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        TransactionalTemplatesApi apiInstance = new TransactionalTemplatesApi();

        StringBuilder errorMessage = new StringBuilder();

        try {
            apiInstance.sendTemplate(templateId, sendEmail);
        } catch (ApiException e) {
            errorMessage.append("Errors during sending emails occurerd: ");
            errorMessage.append(e.getMessage());

            throw new IllegalStateException(errorMessage.toString());
        }

        return result;
    }

}
