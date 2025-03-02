package com.sleektiv.mes.materialFlowResources.hooks;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sleektiv.mes.materialFlowResources.constants.PalletBalanceFields;
import com.sleektiv.model.api.DataDefinition;
import com.sleektiv.model.api.Entity;

@Service
public class PalletBalanceHooks {

    public void onCopy(final DataDefinition palletBalanceDD, final Entity palletBalance) {

        palletBalance.setField(PalletBalanceFields.GENERATED, false);
        palletBalance.setField(PalletBalanceFields.GENERATED_BY, null);
        palletBalance.setField(PalletBalanceFields.GENERATED_DATE, null);
        palletBalance.setField(PalletBalanceFields.DATE_TO, new Date());
        palletBalance.setField(PalletBalanceFields.FILE_NAME, StringUtils.EMPTY);
    }

}
