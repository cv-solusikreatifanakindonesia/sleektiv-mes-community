package com.sleektiv.mes.productionPerShift.report.columns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.orders.constants.OrderFields;
import com.sleektiv.mes.productionPerShift.report.PPSReportXlsHelper;
import com.sleektiv.model.api.Entity;
import com.sleektiv.model.api.NumberService;

@Component("orderSizeReportColumn")
public class OrderSizeReportColumn extends AbstractReportColumn {

    private PPSReportXlsHelper ppsReportXlsHelper;

    private NumberService numberService;

    @Autowired
    public OrderSizeReportColumn(final TranslationService translationService, final PPSReportXlsHelper ppsReportXlsHelper,
            final NumberService numberService) {
        super(translationService);
        this.ppsReportXlsHelper = ppsReportXlsHelper;
        this.numberService = numberService;
    }

    @Override
    public String getIdentifier() {
        return "orderSize";
    }

    @Override
    public Object getValue(final Entity productionPerShift) {
        return ppsReportXlsHelper.getOrder(productionPerShift).getDecimalField(OrderFields.PLANNED_QUANTITY).setScale(5)
                .doubleValue();
    }

    @Override
    public Object getFirstRowValue(final Entity productionPerShift) {
        return getValue(productionPerShift);
    }

    @Override
    public int getColumnWidth() {
        return 7 * 256;
    }

}
