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
package com.sleektiv.mes.technologiesGenerator;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sleektiv.commons.functional.Either;
import com.sleektiv.mes.technologiesGenerator.constants.TechnologiesGeneratorConstants;
import com.sleektiv.mes.technologiesGenerator.dataProvider.GeneratorContextDataProvider;
import com.sleektiv.mes.technologiesGenerator.domain.ContextId;
import com.sleektiv.model.api.EntityMessagesHolder;
import com.sleektiv.plugin.api.RunIfEnabled;

@Service
public class UnusedContextsCleanUpService {

    private static final String CRON_EVERYDAY_AT_5AM = "0 0 5 * * ?";

    private static final Logger LOGGER = LoggerFactory.getLogger(UnusedContextsCleanUpService.class);

    @Autowired
    private GeneratorContextDataProvider generatorContextDataProvider;

    @Async
    @Scheduled(cron = CRON_EVERYDAY_AT_5AM)
    @RunIfEnabled(TechnologiesGeneratorConstants.PLUGIN_IDENTIFIER)
    public void performCleanUp() {
        DateTime threshold = DateTime.now().minusWeeks(1);
        Either<EntityMessagesHolder, List<ContextId>> result = generatorContextDataProvider.deleteContextsNotUsedSince(threshold);
        if (result.isLeft()) {
            warn(String.format("CleanUp aborted - Technology generator context couldn't be deleted: %s", result.getLeft()));
        } else {
            info(String.format("CleanUp done - %s technology generator contexts were deleted.", result.getRight().size()));
        }
    }

    private void info(final String msg) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(msg);
        }
    }

    private void warn(final String msg) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(msg);
        }
    }

}
