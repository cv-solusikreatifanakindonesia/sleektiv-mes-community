package com.sleektiv.mes.productionPerShift.report.columns;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sleektiv.localization.api.TranslationService;
import com.sleektiv.mes.basic.constants.ProductFields;
import com.sleektiv.mes.productionPerShift.report.PPSReportXlsHelper;
import com.sleektiv.mes.productionPerShift.report.print.PPSReportXlsStyleContainer;
import com.sleektiv.model.api.Entity;

@Component("productNumberReportColumn")
public class ProductNumberReportColumn extends AbstractReportColumn {

    private final PPSReportXlsHelper ppsReportXlsHelper;

    @Autowired
    public ProductNumberReportColumn(final TranslationService translationService, final PPSReportXlsHelper ppsReportXlsHelper) {
        super(translationService);
        this.ppsReportXlsHelper = ppsReportXlsHelper;
    }

    @Override
    public String getIdentifier() {
        return "productNumber";
    }

    @Override
    public Object getValue(final Entity productionPerShift) {
        return ppsReportXlsHelper.getProduct(productionPerShift).getStringField(ProductFields.NUMBER);
    }

    @Override
    public Object getFirstRowValue(final Entity productionPerShift) {
        return getValue(productionPerShift);
    }

    @Override
    public int getColumnWidth() {
        return 8 * 256;
    }

    @Override
    public void setWhiteDataStyle(final HSSFCell cell, final PPSReportXlsStyleContainer styleContainer) {
        cell.setCellStyle(styleContainer.getStyles().get(PPSReportXlsStyleContainer.I_WhiteDataStyleRed));
    }

    @Override
    public void setGreyDataStyle(final HSSFCell cell, final PPSReportXlsStyleContainer styleContainer) {
        cell.setCellStyle(styleContainer.getStyles().get(PPSReportXlsStyleContainer.I_GreyDataStyleRed));
    }

    @Override
    public void setHeaderStyle(final HSSFCell cell, final PPSReportXlsStyleContainer styleContainer) {
        cell.setCellStyle(styleContainer.getStyles().get(PPSReportXlsStyleContainer.I_HeaderStyle2Red));
    }

}
