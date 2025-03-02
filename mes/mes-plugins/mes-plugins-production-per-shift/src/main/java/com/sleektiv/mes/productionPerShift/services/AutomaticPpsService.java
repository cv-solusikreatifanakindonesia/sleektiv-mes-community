package com.sleektiv.mes.productionPerShift.services;

import java.util.Date;

import com.sleektiv.mes.productionPerShift.domain.ProgressForDaysContainer;
import com.sleektiv.model.api.Entity;

public interface AutomaticPpsService {

    void generateProgressForDays(ProgressForDaysContainer progressForDaysContainer, Entity productionPerShift);

    void generatePlanProgressForDays(ProgressForDaysContainer progressForDaysContainer, Entity planProductionPerShift, Date startDate);
}
