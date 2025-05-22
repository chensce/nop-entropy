/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.report.pdf.renderer;

import io.nop.core.resource.tpl.ITemplateOutput;
import io.nop.excel.model.ExcelWorkbook;
import io.nop.ooxml.xlsx.output.IExcelSheetGenerator;
import io.nop.report.core.engine.IReportRendererFactory;

public class PdfReportRendererFactory implements IReportRendererFactory {

    @Override
    public ITemplateOutput buildRenderer(ExcelWorkbook model, IExcelSheetGenerator sheetGenerator) {
        return new PdfReportRenderer(model, sheetGenerator);
    }
}
