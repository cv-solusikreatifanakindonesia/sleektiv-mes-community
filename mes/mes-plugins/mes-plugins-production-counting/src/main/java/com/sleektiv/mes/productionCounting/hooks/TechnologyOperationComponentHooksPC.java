/**
 * ***************************************************************************
 * Copyright (c) 2025 Sleektiv.
 * Project: Sleektiv MES
 * Version: 1.4
 * <p>
 * This file is part of Sleektiv.
 * <p>
 * Sleektiv is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.sleektiv.mes.productionCounting.hooks;

import static com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields.OPERATION;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.costNormsForOperation.constants.TechnologyOperationComponentFieldsCNFO;
import com.sleektiv.mes.productionCounting.ProductionCountingService;
import com.sleektiv.mes.productionCounting.constants.TechnologyFieldsPC;
import com.sleektiv.mes.technologies.constants.TechnologyOperationComponentFields;
import com.sleektiv.mes.timeNormsForOperations.constants.OperationFieldsTFNO;
import com.sleektiv.mes.timeNormsForOperations.constants.TechnologyOperationComponentFieldsTNFO;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class TechnologyOperationComponentHooksPC {

    @Autowired
    private ProductionCountingService productionCountingService;

    public void copyTimeNormsToTechnologyOperationComponent(final DataDefinition dd, final Entity technologyOperationComponent) {
        if (technologyOperationComponent.getBelongsToField(OPERATION) == null) {
            return;
        }
        if (productionCountingService.isTypeOfProductionRecordingForEach(technologyOperationComponent.
                getBelongsToField(TechnologyOperationComponentFields.TECHNOLOGY).getStringField(TechnologyFieldsPC.TYPE_OF_PRODUCTION_RECORDING))) {
            technologyOperationComponent.setField(TechnologyOperationComponentFieldsTNFO.PIECEWORK_PRODUCTION,
                    technologyOperationComponent.getBelongsToField(OPERATION).getBooleanField(OperationFieldsTFNO.PIECEWORK_PRODUCTION));
        }
        technologyOperationComponent.setField(TechnologyOperationComponentFieldsCNFO.PIECE_RATE,
                technologyOperationComponent.getBelongsToField(OPERATION).getBelongsToField(TechnologyOperationComponentFieldsCNFO.PIECE_RATE));
    }

    public boolean validatesWith(final DataDefinition dd, final Entity technologyOperationComponent) {
        if (technologyOperationComponent.getBooleanField(TechnologyOperationComponentFieldsTNFO.PIECEWORK_PRODUCTION)
                && Objects.isNull(technologyOperationComponent.getBelongsToField(TechnologyOperationComponentFieldsCNFO.PIECE_RATE))) {
            technologyOperationComponent.addError(dd.getField(TechnologyOperationComponentFieldsCNFO.PIECE_RATE),
                    "sleektivView.validate.field.error.missing");

            return false;
        }

        return true;
    }

}
