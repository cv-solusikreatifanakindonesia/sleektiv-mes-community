package com.sleektiv.mes.orders.hooks;

import com.sleektiv.mes.orders.constants.ScheduleFields;
import com.sleektiv.mes.orders.states.constants.ScheduleStateStringValues;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Service
public class ScheduleHooks {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void onCreate(final DataDefinition scheduleDD, final Entity schedule) {
        setInitialState(schedule);
    }

    public void onCopy(final DataDefinition scheduleDD, final Entity schedule) {
        setInitialState(schedule);
    }

    private void setInitialState(final Entity schedule) {
        schedule.setField(ScheduleFields.STATE, ScheduleStateStringValues.DRAFT);
    }

    public void onSave(final DataDefinition scheduleDD, final Entity schedule) {
        if (schedule.getId() != null) {
            Entity dbSchedule = scheduleDD.get(schedule.getId());
            if (dbSchedule.getBooleanField(ScheduleFields.INCLUDE_TPZ) != schedule.getBooleanField(ScheduleFields.INCLUDE_TPZ)) {
                schedule.setField(ScheduleFields.INCLUDE_TPZ_CHANGED, true);
            }
        }
        setScheduleNumber(schedule);
    }

    private void setScheduleNumber(final Entity schedule) {
        if (checkIfShouldInsertNumber(schedule)) {
            String number = jdbcTemplate.queryForObject("select generate_schedule_number()", Collections.emptyMap(),
                    String.class);
            schedule.setField(ScheduleFields.NUMBER, number);
            if (StringUtils.isBlank(schedule.getStringField(ScheduleFields.NAME))) {
                schedule.setField(ScheduleFields.NAME, number);
            }
        }
    }

    private boolean checkIfShouldInsertNumber(final Entity schedule) {
        if (!Objects.isNull(schedule.getId())) {
            return false;
        }
        return !StringUtils.isNotBlank(schedule.getStringField(ScheduleFields.NUMBER));
    }
}
