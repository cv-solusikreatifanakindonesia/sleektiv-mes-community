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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.PalletNumberGenerator;
import com.sleektiv.mes.basic.constants.PalletNumberFields;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.api.components.WindowComponent;
import com.sleektiv.view.api.ribbon.Ribbon;
import com.sleektiv.view.api.ribbon.RibbonActionItem;
import com.sleektiv.view.api.ribbon.RibbonGroup;
import com.sleektiv.view.constants.SleektivViewConstants;

@Service
public class PalletNumberDetailsHooks {




    public static final String L_PRINT = "print";

    public static final String L_PRINT_PALLET_NUMBER_REPORT = "printPalletNumberReport";

    @Autowired
    private PalletNumberGenerator palletNumberGenerator;

    public void onBeforeRender(final ViewDefinitionState view) {
        generatePalletNumber(view);

        disableButtonsWhenNotSaved(view);
    }

    private void generatePalletNumber(final ViewDefinitionState view) {
        FieldComponent numberField = (FieldComponent) view.getComponentByReference(PalletNumberFields.NUMBER);

        if (palletNumberGenerator.checkIfShouldInsertNumber(view)) {
            String number = palletNumberGenerator.generate();

            numberField.setFieldValue(number);
            numberField.requestComponentUpdateState();
        }
    }

    public void disableButtonsWhenNotSaved(final ViewDefinitionState view) {
        FormComponent palletNumberForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);

        WindowComponent window = (WindowComponent) view.getComponentByReference(SleektivViewConstants.L_WINDOW);
        Ribbon ribbon = window.getRibbon();

        RibbonGroup printRibbonGroup = ribbon.getGroupByName(L_PRINT);

        RibbonActionItem printPalletNumberReportRibbonActionItem = printRibbonGroup.getItemByName(L_PRINT_PALLET_NUMBER_REPORT);

        Long palletNumberId = palletNumberForm.getEntityId();

        boolean isSaved = (palletNumberId != null);

        if (printPalletNumberReportRibbonActionItem != null) {
            printPalletNumberReportRibbonActionItem.setEnabled(isSaved);

            printPalletNumberReportRibbonActionItem.requestUpdate(true);
        }
    }

}
