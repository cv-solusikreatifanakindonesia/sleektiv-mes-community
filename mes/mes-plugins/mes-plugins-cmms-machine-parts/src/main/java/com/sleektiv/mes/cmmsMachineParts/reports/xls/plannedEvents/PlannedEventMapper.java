package com.sleektiv.mes.cmmsMachineParts.reports.xls.plannedEvents;

import com.sleektiv.mes.cmmsMachineParts.reports.xls.plannedEvents.dto.PlannedEventDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlannedEventMapper  implements RowMapper {

    @Override public PlannedEventDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        PlannedEventDTO plannedEventDTO = new PlannedEventDTO();
        return plannedEventDTO;
    }
}

