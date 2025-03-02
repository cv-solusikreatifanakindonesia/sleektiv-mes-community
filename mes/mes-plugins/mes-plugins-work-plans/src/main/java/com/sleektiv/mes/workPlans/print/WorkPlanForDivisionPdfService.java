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
package com.sleektiv.mes.workPlans.print;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.technologies.ProductQuantitiesServiceImpl;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.mes.workPlans.constants.WorkPlanFields;
import com.sleektiv.mes.workPlans.pdf.document.WorkPlanPdfForDivision;
import com.sleektiv.mes.workPlans.pdf.document.operation.grouping.container.GroupingContainer;
import com.sleektiv.mes.workPlans.pdf.document.operation.grouping.factory.GroupingContainerFactory;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.EntityList;
import com.sleektiv.model.api.utils.EntityTreeUtilsService;
import com.sleektiv.report.api.pdf.PdfDocumentWithWriterService;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkPlanForDivisionPdfService extends PdfDocumentWithWriterService {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private GroupingContainerFactory groupingContainerFactory;

    @Autowired
    private WorkPlanPdfForDivision workPlanPdfForDivision;

    @Autowired
    private ProductQuantitiesServiceImpl productQuantitiesServiceImpl;

    @Autowired
    private EntityTreeUtilsService entityTreeUtilsService;

    @Autowired
    private WorkPlanPdfService workPlanPdfService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public String getReportTitle(final Locale locale) {
        return translationService.translate("workPlans.workPlan.report.title", locale);
    }

    @Override
    public void buildPdfContent(final PdfWriter writer, final Document document, final Entity workPlan, final Locale locale)
            throws DocumentException {

        GroupingContainer groupingContainer = groupingContainerFactory.create(workPlan, locale);

        for (Entity order : orders(workPlan)) {
            for (Entity operationComponent : operationComponents(technology(order))) {
                List<Entity> productionCountingQuantitiesIn = workPlanPdfService.getProductionCountingQuantitiesIn(order, operationComponent);
                List<Entity> productionCountingQuantitiesOut = workPlanPdfService.getProductionCountingQuantitiesOut(order, operationComponent);
                groupingContainer.add(order, operationComponent, productionCountingQuantitiesIn, productionCountingQuantitiesOut);
            }
        }

        workPlanPdfForDivision.print(writer, groupingContainer, workPlan, document, locale);

    }

    private List<Entity> operationComponents(Entity technology) {
        return entityTreeUtilsService.getSortedEntities(technology.getTreeField(TechnologyFields.OPERATION_COMPONENTS));
    }

    private Entity technology(Entity order) {
        return order.getBelongsToField(OrderFields.TECHNOLOGY);
    }

    private EntityList orders(Entity workPlan) {
        return workPlan.getHasManyField(WorkPlanFields.ORDERS);
    }


}
