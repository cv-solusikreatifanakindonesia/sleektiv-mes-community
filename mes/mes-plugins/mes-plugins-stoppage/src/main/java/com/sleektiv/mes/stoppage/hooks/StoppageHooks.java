package com.sleektiv.mes.stoppage.hooks;

import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.stoppage.constants.StoppageFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class StoppageHooks {

    private static final String L_STOPPAGE_REASON_OTHER = "Inna";

    private static final String L_NAME = "name";

    public boolean validatesWith(final DataDefinition dataDefinition, final Entity entity) {

        if (L_STOPPAGE_REASON_OTHER.equals(entity.getBelongsToField(StoppageFields.REASON).getStringField(L_NAME))
                && StringUtils.isEmpty(entity.getStringField(StoppageFields.DESCRIPTION))) {
            entity.addError(dataDefinition.getField(OrderFields.DESCRIPTION), "sleektivView.validate.field.error.missing");
            return false;
        }

        Date dateFrom = entity.getDateField(StoppageFields.DATE_FROM);
        Date dateTo = entity.getDateField(StoppageFields.DATE_TO);

        if (dateFrom == null || dateTo == null || dateTo.after(dateFrom)) {
            return true;
        }

        entity.addError(dataDefinition.getField(OrderFields.DATE_TO), "stoppage.validate.global.error.datesStoppage");

        return false;
    }
}
