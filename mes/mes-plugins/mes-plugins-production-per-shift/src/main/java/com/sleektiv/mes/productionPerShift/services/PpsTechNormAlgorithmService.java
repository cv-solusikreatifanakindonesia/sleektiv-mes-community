package com.sleektiv.mes.productionPerShift.services;

import com.sleektiv.mes.basic.shift.Shift;
import com.sleektiv.mes.basic.util.DateTimeRange;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.productionPerShift.domain.ProgressForDaysContainer;
import com.sleektiv.mes.productionPerShift.domain.ShiftEfficiencyCalculationHolder;
import com.sleektiv.mes.technologies.TechnologyService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.validators.ErrorMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Optional;

@Service
public class PpsTechNormAlgorithmService extends PpsBaseAlgorithmService {

    @Autowired
    private NumberService numberService;

    @Autowired
    private TechnologyService technologyService;

    @Override
    protected ShiftEfficiencyCalculationHolder calculateShiftEfficiency(ProgressForDaysContainer progressForDaysContainer,
                                                                        Entity productionPerShift, Shift shift, Entity technology, Entity productionLine, DateTimeRange range, BigDecimal shiftEfficiency,
                                                                        int progressForDayQuantity, boolean allowIncompleteUnits) {
        ShiftEfficiencyCalculationHolder calculationHolder = new ShiftEfficiencyCalculationHolder();
        BigDecimal scaledNorm = getStandardPerformanceNorm(progressForDaysContainer, technology, productionLine);
        long minutes = range.durationInMins();
        BigDecimal efficiencyForRange = calculateEfficiencyForRange(scaledNorm, minutes, allowIncompleteUnits);
        shiftEfficiency = shiftEfficiency.add(efficiencyForRange, numberService.getMathContext());
        calculationHolder.setShiftEfficiency(shiftEfficiency);
        if (shiftEfficiency.compareTo(progressForDaysContainer.getPlannedQuantity()) > 0) {
            calculateEfficiencyTime(calculationHolder, progressForDaysContainer.getPlannedQuantity(), scaledNorm);
        } else {
            calculateEfficiencyTime(calculationHolder, shiftEfficiency, scaledNorm);
        }
        return calculationHolder;
    }

    protected void calculateEfficiencyTime(ShiftEfficiencyCalculationHolder calculationHolder, BigDecimal shiftEfficiency,
                                           BigDecimal scaledNorm) {
        int time = shiftEfficiency.divide(scaledNorm, numberService.getMathContext()).setScale(0, RoundingMode.HALF_UP)
                .intValue();
        calculationHolder.addEfficiencyTime(time);
    }

    protected BigDecimal calculateEfficiencyForRange(BigDecimal scaledNorm, long minuets, boolean allowIncompleteUnits) {
        BigDecimal value = scaledNorm.multiply(new BigDecimal(minuets), numberService.getMathContext());
        if (allowIncompleteUnits) {
            return value;
        } else {
            return value.setScale(0, RoundingMode.HALF_UP);
        }
    }

    protected BigDecimal getStandardPerformanceNorm(ProgressForDaysContainer progressForDaysContainer, Entity technology, Entity productionLine) {
        Optional<BigDecimal> norm = Optional.empty();
        if (productionLine != null) {
            norm = technologyService.getStandardPerformance(technology, productionLine);
        }
        if (!norm.isPresent()) {
            progressForDaysContainer.addError(new ErrorMessage(
                    "productionPerShift.automaticAlgorithm.technology.standardPerformanceTechnologyRequired", false));
            throw new IllegalStateException("No standard performance norm in technology");
        }
        return norm.get();

    }

}
