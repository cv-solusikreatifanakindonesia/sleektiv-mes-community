package com.sleektiv.mes.technologies.rowStyleResolvers;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.sleektiv.mes.technologies.constants.OperationProductInComponentDtoFields;
import com.sleektiv.model.api.Entity;
import com.sleektiv.view.constants.RowStyle;

@Service
public class OperationProductInComponentsListResolver {

    public Set<String> fillRowStyles(final Entity entity) {
        final Set<String> rowStyles = Sets.newHashSet();
        if (entity.getBooleanField(OperationProductInComponentDtoFields.HAS_ACCEPTED_TECHNOLOGY)) {
            rowStyles.add(RowStyle.GREEN_FONT_COLOR);
        } else if (entity.getBooleanField(OperationProductInComponentDtoFields.HAS_CHECKED_TECHNOLOGY)) {
            rowStyles.add(RowStyle.ORANGE_FONT_COLOR);
        }

        return rowStyles;
    }
}
