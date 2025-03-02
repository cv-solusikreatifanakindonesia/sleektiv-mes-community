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
package com.sleektiv.mes.avgLaborCostCalcForOrder;

import com.google.common.collect.Lists;
import com.sleektiv.mes.assignmentToShift.constants.AssignmentToShiftConstants;
import com.sleektiv.mes.assignmentToShift.constants.AssignmentToShiftFields;
import com.sleektiv.mes.assignmentToShift.constants.StaffAssignmentToShiftFields;
import com.sleektiv.mes.assignmentToShift.constants.StaffAssignmentToShiftState;
import com.sleektiv.mes.assignmentToShift.states.constants.AssignmentToShiftState;
import com.sleektiv.mes.avgLaborCostCalcForOrder.constants.AssignmentWorkerToShiftFields;
import com.sleektiv.mes.avgLaborCostCalcForOrder.constants.AvgLaborCostCalcForOrderConstants;
import com.sleektiv.mes.avgLaborCostCalcForOrder.constants.AvgLaborCostCalcForOrderFields;
import com.sleektiv.mes.basic.ShiftsService;
import com.sleektiv.mes.basic.shift.Shift;
import com.sleektiv.model.api.DataDefinitionService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;
import com.sleektiv.model.api.search.SearchCriteriaBuilder;
import com.sleektiv.model.api.search.SearchOrders;
import com.sleektiv.model.api.search.SearchRestrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.sleektiv.mes.assignmentToShift.constants.AssignmentToShiftFields.SHIFT;
import static com.sleektiv.mes.assignmentToShift.constants.AssignmentToShiftFields.START_DATE;
import static com.sleektiv.mes.assignmentToShift.constants.StaffAssignmentToShiftFields.WORKER;
import static com.sleektiv.mes.avgLaborCostCalcForOrder.constants.AvgLaborCostCalcForOrderFields.AVERAGE_LABOR_HOURLY_COST;

@Service
public class AverageCostService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private NumberService numberService;

    @Autowired
    private ShiftsService shiftsService;

    public Entity generateAssignmentWorkerToShiftAndAverageCost(Entity entity) {
        Map<Entity, BigDecimal> workersWithHoursWorked = generateMapWorkersWithHoursWorked(entity);
        entity.setField(AVERAGE_LABOR_HOURLY_COST, null);
        entity = createAssignmentWorkerToShift(entity, workersWithHoursWorked);
        entity = countAverageCost(entity, workersWithHoursWorked);
        return entity;
    }

    private Map<Entity, BigDecimal> generateMapWorkersWithHoursWorked(final Entity entity) {
        Map<Entity, BigDecimal> workersWithHours = new HashMap<>();
        List<Shift> shifts = shiftsService.findAll();
        List<DateTime> days = shiftsService.getDaysBetweenGivenDates(
                new DateTime(entity.getDateField(AvgLaborCostCalcForOrderFields.START_DATE)),
                new DateTime(entity.getDateField(AvgLaborCostCalcForOrderFields.FINISH_DATE)));
        for (DateTime day : days) {
            for (Shift shift : shifts) {
                Entity assignmentToShift = getAssignmentToShift(shift, day);
                if (assignmentToShift == null) {
                    continue;
                }
                List<Entity> staffs = getStaffAssignmentToShiftDependOnAssignmentToShiftState(assignmentToShift,
                        entity.getBelongsToField(AvgLaborCostCalcForOrderFields.PRODUCTION_LINE));
                for (Entity staff : staffs) {
                    if (workersWithHours.containsKey(staff)) {
                        BigDecimal countHours = workersWithHours.get(staff).add(shiftsService.getWorkedHoursOfWorker(shift, day));
                        workersWithHours.put(staff, countHours);
                    } else {
                        workersWithHours.put(staff, shiftsService.getWorkedHoursOfWorker(shift, day));
                    }
                }
            }
        }
        return workersWithHours;
    }

    private Entity countAverageCost(Entity entity, final Map<Entity, BigDecimal> workersWithHoursWorked) {
        BigDecimal costOfWorkersHours = BigDecimal.ZERO;
        BigDecimal countHours = BigDecimal.ZERO;
        for (Entry<Entity, BigDecimal> workerWithHours : workersWithHoursWorked.entrySet()) {
            BigDecimal quantityOfHours = workerWithHours.getValue();
            BigDecimal laborHourlyCost = workerWithHours.getKey().getBelongsToField(StaffAssignmentToShiftFields.WORKER)
                    .getDecimalField("laborHourlyCost");
            if (laborHourlyCost == null) {
                entity.addGlobalError("avgLaborCostCalcForOrder.avgLaborCostCalcForOrder.laborHourlyCost.isEmpty");
                return entity;
            }
            BigDecimal costOfWorkerHours = laborHourlyCost.multiply(quantityOfHours);
            costOfWorkersHours = costOfWorkersHours.add(costOfWorkerHours);
            countHours = countHours.add(quantityOfHours);
        }
        if (countHours.equals(BigDecimal.ZERO)) {
            entity.addGlobalError("avgLaborCostCalcForOrder.avgLaborCostCalcForOrder.averageLaborHourlyCost.isZero");
            return entity;
        }
        BigDecimal averageCost = numberService
                .setScaleWithDefaultMathContext(costOfWorkersHours.divide(countHours, numberService.getMathContext()));
        entity.setField(AVERAGE_LABOR_HOURLY_COST, averageCost);
        entity = entity.getDataDefinition().save(entity);
        return entity;
    }

    private Entity getAssignmentToShift(final Shift shift, final DateTime date) {
        boolean shiftWorks = shift.worksAt(date.dayOfWeek().get());
        if (shiftWorks) {
            return dataDefinitionService
                    .get(AssignmentToShiftConstants.PLUGIN_IDENTIFIER, AssignmentToShiftConstants.MODEL_ASSIGNMENT_TO_SHIFT)
                    .find().add(SearchRestrictions.belongsTo(SHIFT, shift.getEntity()))
                    .add(SearchRestrictions.le(START_DATE, date.toDate())).addOrder(SearchOrders.desc(START_DATE))
                    .setMaxResults(1).uniqueResult();
        } else {
            return null;
        }
    }

    private List<Entity> getStaffAssignmentToShiftDependOnAssignmentToShiftState(final Entity assignmentToShift,
            final Entity productionLine) {
        List<Entity> staffAssignmentToShifts = new ArrayList<Entity>();
        String state = assignmentToShift.getStringField(AssignmentToShiftFields.STATE);
        SearchCriteriaBuilder searchCriteriaBuilder = assignmentToShift.getHasManyField("staffAssignmentToShifts").find()
                .add(SearchRestrictions.eq("occupationTypeEnum", "01workOnLine"))
                .add(SearchRestrictions.belongsTo(StaffAssignmentToShiftFields.PRODUCTION_LINE, productionLine));
        if (state.equals(AssignmentToShiftState.CORRECTED.getStringValue())) {
            staffAssignmentToShifts = searchCriteriaBuilder
                    .add(SearchRestrictions.eq("state", StaffAssignmentToShiftState.CORRECTED.getStringValue())).list()
                    .getEntities();
        } else if (state.equals(AssignmentToShiftState.ACCEPTED.getStringValue())
                || state.equals(AssignmentToShiftState.DURING_CORRECTION.getStringValue())) {
            staffAssignmentToShifts = searchCriteriaBuilder
                    .add(SearchRestrictions.eq("state", StaffAssignmentToShiftState.ACCEPTED.getStringValue())).list()
                    .getEntities();
        }
        return staffAssignmentToShifts;
    }

    private Entity createAssignmentWorkerToShift(Entity entity, final Map<Entity, BigDecimal> workersWithHoursWorked) {
        Map<Long, Entity> workers = new HashMap<>();
        for (Entry<Entity, BigDecimal> workerWithHours : workersWithHoursWorked.entrySet()) {
            Entity worker = workerWithHours.getKey().getBelongsToField(WORKER);
            Long workerId = worker.getId();
            Entity assignmentWorkerToShift = dataDefinitionService.get(AvgLaborCostCalcForOrderConstants.PLUGIN_IDENTIFIER,
                    AvgLaborCostCalcForOrderConstants.MODEL_ASSIGNMENT_WORKER_TO_SHIFT).create();
            if (workers.containsKey(workerId)) {
                assignmentWorkerToShift = workers.get(workerId);
                BigDecimal countHours = assignmentWorkerToShift.getDecimalField(AssignmentWorkerToShiftFields.WORKED_HOURS)
                        .add(workerWithHours.getValue());
                assignmentWorkerToShift.setField(AssignmentWorkerToShiftFields.WORKED_HOURS, countHours);
            } else {
                assignmentWorkerToShift.setField(AssignmentWorkerToShiftFields.ASSIGNMENT_TO_SHIFT,
                        workerWithHours.getKey().getBelongsToField("assignmentToShift"));
                assignmentWorkerToShift.setField(AssignmentWorkerToShiftFields.WORKER, worker);
                assignmentWorkerToShift.setField(AssignmentWorkerToShiftFields.WORKED_HOURS, workerWithHours.getValue());
            }
            workers.put(worker.getId(), assignmentWorkerToShift);
        }
        entity.setField(AvgLaborCostCalcForOrderFields.ASSIGNMENT_WORKER_TO_SHIFTS, Lists.newArrayList(workers.values()));
        entity = entity.getDataDefinition().save(entity);
        return entity;
    }
}
