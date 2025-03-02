package com.sleektiv.mes.productFlowThruDivision.aop;

import com.sleektiv.mes.productFlowThruDivision.constants.ProductFlowThruDivisionConstants;
import com.sleektiv.mes.productFlowThruDivision.service.ProductionCountingDocumentService;
import com.sleektiv.model.api.Entity;
import com.sleektiv.plugin.api.RunIfEnabled;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Aspect
@Configurable
@RunIfEnabled(ProductFlowThruDivisionConstants.PLUGIN_IDENTIFIER)
public class CostNormsForMaterialsServiceAspectPFTD {

    @Autowired
    private ProductionCountingDocumentService productionCountingDocumentService;

    @AfterReturning(pointcut = "execution(public com.sleektiv.model.api.Entity com.sleektiv.mes.costNormsForMaterials.CostNormsForMaterialsService.updateCostsInOrder(com.sleektiv.model.api.Entity))", returning = "order")
    public void afterUpdateCostsInOrder(final JoinPoint jp, final Entity order) throws Throwable {
        productionCountingDocumentService.updateCostsForOrder(order);
    }

}
