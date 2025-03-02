package com.sleektiv.mes.productionPerShift.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.basic.ParameterService;
import com.sleektiv.mes.orders.constants.ParameterFieldsO;
import com.sleektiv.mes.productionPerShift.constants.ParameterFieldsPPS;
import com.sleektiv.mes.productionPerShift.constants.PpsAlgorithm;

@Service
public class AutomaticPpsParametersService {

    @Autowired
    private ParameterService parameterService;

    public PpsAlgorithm getPpsAlgorithm(){
        String value = parameterService.getParameter().getStringField(ParameterFieldsPPS.PPS_ALGORITHM);
        return PpsAlgorithm.fromStringValue(value);
    }

    public boolean isAutomaticPlanForShiftOn(){
        return parameterService.getParameter().getBooleanField(ParameterFieldsO.PPS_IS_AUTOMATIC);
    }
}
