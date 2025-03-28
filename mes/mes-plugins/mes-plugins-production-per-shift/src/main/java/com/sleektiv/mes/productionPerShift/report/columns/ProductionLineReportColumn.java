package com.sleektiv.mes.productionPerShift.report.columns;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.productionLines.constants.ProductionLineFields;
import com.sleektiv.mes.productionPerShift.report.PPSReportXlsHelper;
import com.sleektiv.model.api.Entity;

@Component("productionLineReportColumn")
public class ProductionLineReportColumn extends AbstractReportColumn {

    private final PPSReportXlsHelper ppsReportXlsHelper;

    @Autowired
    public ProductionLineReportColumn(final TranslationService translationService, final PPSReportXlsHelper ppsReportXlsHelper) {
        super(translationService);
        this.ppsReportXlsHelper = ppsReportXlsHelper;
    }

    @Override
    public String getIdentifier() {
        return "productionLine";
    }

    @Override
    public Object getValue(final Entity productionPerShift) {
        return StringUtils.EMPTY;
    }

    @Override
    public Object getFirstRowValue(final Entity productionPerShift) {
        return ppsReportXlsHelper.getProductionLine(productionPerShift).getStringField(ProductionLineFields.NUMBER);
    }

    @Override
    public String getFirstRowChangeoverValue(final Entity productionPerShift) {
        return ppsReportXlsHelper.getProductionLine(productionPerShift).getStringField(ProductionLineFields.NUMBER);
    }

    @Override
    public int getColumnWidth() {
        return 8 * 256;
    }

}
