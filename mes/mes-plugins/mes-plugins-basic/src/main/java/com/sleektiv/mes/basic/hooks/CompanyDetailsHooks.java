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
package com.sleektiv.mes.basic.hooks;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.CompanyService;
import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.basic.constants.BasicConstants;
import com.sleektiv.mes.basic.constants.CompanyFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.LookupComponent;
import com.sleektiv.view.api.utils.NumberGeneratorService;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class CompanyDetailsHooks {

    private static final String L_ORDER_PRODUCTION = "orderProduction";

    private static final String L_REDIRECT_TO_FILTERED_ORDER_PRODUCTION_LIST = "redirectToFilteredOrderProductionList";

    private static final String L_DELETE = "delete";

    private static final String L_ACTIONS = "actions";

    public static final List<String> L_COMPANY_FIELDS = Arrays.asList(CompanyFields.NUMBER, CompanyFields.NAME,
            CompanyFields.TAX_COUNTRY_CODE, CompanyFields.TAX, CompanyFields.PHONE, CompanyFields.EMAIL, CompanyFields.WEBSITE,
            CompanyFields.STREET, CompanyFields.HOUSE, CompanyFields.FLAT, CompanyFields.ZIP_CODE, CompanyFields.CITY,
            CompanyFields.STATE, CompanyFields.COUNTRY, CompanyFields.CONTACT_PERSON, CompanyFields.IS_SUPPLIER,
            CompanyFields.IS_RECEIVER);

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    public void updateRibbonState(final ViewDefinitionState view) {
        disabledRedirectToFilteredOrderProductionListButton(view);
        disabledRibbonForOwnerOrExternal(view);
    }

    private void disabledRedirectToFilteredOrderProductionListButton(final ViewDefinitionState view) {
        FormComponent companyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity company = companyForm.getEntity();

        boolean isEnabled = (company.getId() != null);

        companyService.disableButton(view, L_ORDER_PRODUCTION, L_REDIRECT_TO_FILTERED_ORDER_PRODUCTION_LIST, isEnabled, null);
    }

    private void disabledRibbonForOwnerOrExternal(final ViewDefinitionState view) {
        FormComponent companyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity company = companyForm.getEntity();

        Boolean isOwner = companyService.isCompanyOwner(companyForm.getEntity());

        boolean isEnabled = !isOwner;

        String buttonMessage = "basic.company.isOwner";

        if ((company != null) && !StringUtils.isEmpty(company.getStringField(CompanyFields.EXTERNAL_NUMBER))) {
            buttonMessage = "basic.company.isExternalNumber";

            isEnabled = false;
        }

        companyService.disableButton(view, L_ACTIONS, L_DELETE, isEnabled, buttonMessage);
    }

    public void generateCompanyNumber(final ViewDefinitionState view) {
        numberGeneratorService.generateAndInsertNumber(view, BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_COMPANY,
                SleektivViewConstants.L_FORM, CompanyFields.NUMBER);
    }

    public void fillDefaultCountry(final ViewDefinitionState view) {
        FormComponent companyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (companyForm.getEntityId() != null) {
            return;
        }

        CheckBoxComponent isSetFieldsFromParameter = (CheckBoxComponent) view
                .getComponentByReference(CompanyFields.IS_SET_FIELDS_FROM_PARAMETER);
        if (isSetFieldsFromParameter.isChecked()) {
            return;
        }

        LookupComponent countryField = (LookupComponent) view.getComponentByReference(CompanyFields.COUNTRY);
        LookupComponent taxCountryField = (LookupComponent) view.getComponentByReference(CompanyFields.TAX_COUNTRY_CODE);

        Entity defaultCountry = parameterService.getParameter().getBelongsToField(CompanyFields.COUNTRY);

        if (defaultCountry != null) {
            countryField.setFieldValue(defaultCountry.getId());
            taxCountryField.setFieldValue(defaultCountry.getId());
            taxCountryField.requestComponentUpdateState();
            countryField.requestComponentUpdateState();
        }
        isSetFieldsFromParameter.setFieldValue(true);
        isSetFieldsFromParameter.requestComponentUpdateState();
    }

    public void disabledFieldsForExternalCompany(final ViewDefinitionState view) {
        FormComponent companyForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        if (companyForm.getEntityId() == null) {
            companyForm.setFormEnabled(true);

            return;
        }

        Entity company = companyForm.getEntity();

        if (!StringUtils.isEmpty(company.getStringField(CompanyFields.EXTERNAL_NUMBER))) {
            for (String fieldName : L_COMPANY_FIELDS) {
                disabledField(view, fieldName);
            }
        } else {
            companyForm.setFormEnabled(true);
        }
    }

    private void disabledField(final ViewDefinitionState view, final String reference) {
        FieldComponent fieldComponent = (FieldComponent) view.getComponentByReference(reference);

        fieldComponent.setEnabled(false);
        fieldComponent.requestComponentUpdateState();
    }

}
