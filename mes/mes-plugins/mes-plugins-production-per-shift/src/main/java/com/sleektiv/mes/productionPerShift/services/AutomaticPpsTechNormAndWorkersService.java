package com.sleektiv.mes.productionPerShift.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sleektiv.mes.productionPerShift.domain.ProgressForDaysContainer;
import com.sleektiv.model.api.Entity;

@Service
public class AutomaticPpsTechNormAndWorkersService implements AutomaticPpsService {

    @Autowired
    private PpsTechNormAndWorkersAlgorithmService ppsTechNormAlgorithmService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateProgressForDays(ProgressForDaysContainer progressForDaysContainer, Entity productionPerShift) {
        ppsTechNormAlgorithmService.generateProgressForDays(progressForDaysContainer, productionPerShift);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generatePlanProgressForDays(ProgressForDaysContainer progressForDaysContainer, Entity planProductionPerShift, Date startDate) {
        ppsTechNormAlgorithmService.generatePlanProgressForDays(progressForDaysContainer, planProductionPerShift, startDate);
    }
}
