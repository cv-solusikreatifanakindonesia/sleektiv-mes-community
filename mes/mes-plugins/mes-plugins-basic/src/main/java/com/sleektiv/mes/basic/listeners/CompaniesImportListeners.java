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
package com.sleektiv.mes.basic.listeners;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.CompanyFields;
import com.sleektiv.mes.basic.imports.company.CompanyCellBinderRegistry;
import com.sleektiv.mes.basic.imports.company.CompanyXlsxImportService;
import com.sleektiv.mes.basic.imports.services.XlsxImportService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.search.SearchCriterion;
import com.sleektiv.model.api.search.SearchRestrictions;
import com.sleektiv.view.api.ComponentState;
import com.sleektiv.view.api.ViewDefinitionState;

@Service
public class CompaniesImportListeners {

    @Autowired
    private CompanyXlsxImportService companyXlsxImportService;

    @Autowired
    private CompanyCellBinderRegistry companyCellBinderRegistry;

    public void downloadImportSchema(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        companyXlsxImportService.downloadImportSchema(view, BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_COMPANY,
                XlsxImportService.L_XLSX);
    }

    public void processImportFile(final ViewDefinitionState view, final ComponentState state, final String[] args)
            throws IOException {
        companyXlsxImportService.processImportFile(view, companyCellBinderRegistry.getCellBinderRegistry(), true,
                BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_COMPANY, CompaniesImportListeners::createRestrictionForCompany);
    }

    private static SearchCriterion createRestrictionForCompany(final Entity company) {
        return SearchRestrictions.eq(CompanyFields.NUMBER, company.getStringField(CompanyFields.NUMBER));
    }

    public void redirectToLogs(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        companyXlsxImportService.redirectToLogs(view, BasicConstants.MODEL_COMPANY);
    }

    public void onInputChange(final ViewDefinitionState view, final ComponentState state, final String[] args) {
        companyXlsxImportService.changeButtonsState(view, false);
    }

}
