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
package com.sleektiv.mes.costNormsForOperationInOrder.hooks;

import com.sleektiv.localization.api.utils.DateUtils;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.technologies.constants.TechnologyFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.FieldComponent;
import com.sleektiv.view.api.components.FormComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class HourlyCostNormsInOrderDetailsHooks {

    public void onBeforeRender(final ViewDefinitionState view) {
        setLastUpdateDateTioc(view);
    }

    public void setLastUpdateDateTioc(final ViewDefinitionState view) {
        FormComponent orderForm = (FormComponent) view.getComponentByReference(SleektivViewConstants.L_FORM);
        Entity order = orderForm.getEntity().getDataDefinition().get(orderForm.getEntityId());
        List<Entity> tocs = order.getBelongsToField(OrderFields.TECHNOLOGY)
                .getHasManyField(TechnologyFields.OPERATION_COMPONENTS);
        if (tocs.isEmpty()) {
            return;
        }
        Date updateDate = new Date(0);
        for (Entity toc : tocs) {

            if (toc.getDateField("updateDate").after(updateDate)) {
                updateDate = toc.getDateField("updateDate");
            }

        }
        FieldComponent lastUpdate = (FieldComponent) view.getComponentByReference("lastUpdate");
        String lastUpdateDate = new SimpleDateFormat(DateUtils.L_DATE_TIME_FORMAT, view.getLocale()).format(updateDate);
        lastUpdate.setFieldValue(lastUpdateDate);
        lastUpdate.requestComponentUpdateState();
    }
}
