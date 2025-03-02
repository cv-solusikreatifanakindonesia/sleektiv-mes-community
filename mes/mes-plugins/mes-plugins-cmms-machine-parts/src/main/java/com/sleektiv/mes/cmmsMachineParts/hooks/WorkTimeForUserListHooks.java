package com.sleektiv.mes.cmmsMachineParts.hooks;

import com.google.common.collect.Maps;
import com.sleektiv.model.api.Entity;
import com.sleektiv.security.api.UserService;
import com.sleektiv.view.api.ViewDefinitionState;
import com.sleektiv.view.api.components.CheckBoxComponent;
import com.sleektiv.view.api.components.GridComponent;
import com.sleektiv.view.constants.SleektivViewConstants;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WorkTimeForUserListHooks {

    @Autowired
    private UserService userService;

    public void fillDefaultFilters(final ViewDefinitionState view) {
        CheckBoxComponent initialized = (CheckBoxComponent) view.getComponentByReference("initialized");
        if (initialized.isChecked()) {
            return;
        }
        initialized.setChecked(true);
        GridComponent grid = (GridComponent) view.getComponentByReference(SleektivViewConstants.L_GRID);
        Entity currentUser = userService.getCurrentUserEntity();

        Map<String, String> filters = Maps.newHashMap();
        DateTime currentDate = getCurrentDate();
        filters.put("startDate", ">=" + currentDate.toString("yyyy-MM-dd hh:mm:ss"));
        filters.put("finishDate", "=<" + currentDate.plusDays(1).toString("yyyy-MM-dd hh:mm:ss"));
        filters.put("username", currentUser.getStringField("userName"));
        grid.setFilters(filters);
    }

    private DateTime getCurrentDate() {
        DateTime now = DateTime.now();
        DateTime firstShiftStart = DateTime.now().withMillisOfDay(21600000);
        DateTimeComparator timeComparator = DateTimeComparator.getTimeOnlyInstance();
        if (timeComparator.compare(firstShiftStart, now) > 0) {
            return firstShiftStart.minusDays(1);
        }
        return firstShiftStart;
    }
}
