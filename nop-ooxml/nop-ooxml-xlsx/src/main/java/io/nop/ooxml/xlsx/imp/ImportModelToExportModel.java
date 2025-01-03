package io.nop.ooxml.xlsx.imp;

import io.nop.api.core.beans.DictBean;
import io.nop.commons.cache.ICache;
import io.nop.commons.cache.MapCache;
import io.nop.core.dict.DictProvider;
import io.nop.core.lang.eval.IEvalScope;
import io.nop.core.model.table.CellPosition;
import io.nop.core.model.table.ICellView;
import io.nop.core.model.table.tree.TreeCell;
import io.nop.excel.imp.model.ImportFieldModel;
import io.nop.excel.imp.model.ImportModel;
import io.nop.excel.imp.model.ImportSheetModel;
import io.nop.excel.model.ExcelCell;
import io.nop.excel.model.ExcelDataValidation;
import io.nop.excel.model.ExcelSheet;
import io.nop.excel.model.ExcelTable;
import io.nop.excel.model.ExcelWorkbook;
import io.nop.ooxml.xlsx.XlsxConstants;
import io.nop.ooxml.xlsx.parse.ExcelWorkbookParser;
import io.nop.xlang.api.XLang;

import java.util.List;

import static io.nop.ooxml.xlsx.imp.TreeObjectLayout.STYLE_ID_AUTO_SEQ;
import static io.nop.ooxml.xlsx.imp.TreeObjectLayout.STYLE_ID_COL;
import static io.nop.ooxml.xlsx.imp.TreeObjectLayout.STYLE_ID_HEADER;
import static io.nop.ooxml.xlsx.imp.TreeObjectLayout.STYLE_ID_LABEL;
import static io.nop.ooxml.xlsx.imp.TreeObjectLayout.STYLE_ID_SEQ;
import static io.nop.ooxml.xlsx.imp.TreeObjectLayout.STYLE_ID_TITLE;
import static io.nop.ooxml.xlsx.imp.TreeObjectLayout.STYLE_ID_VALUE;

public class ImportModelToExportModel {
    private ExcelWorkbook wk;
    private ExcelSheet dataSheet;
    private String labelStyle;
    private String valueStyle;
    private String titleStyle;
    private ICache<Object, Object> cache = new MapCache<>("import", false);
    private IEvalScope scope = XLang.newEvalScope();

    public ImportModelToExportModel() {
        wk = new ExcelWorkbookParser().parseFromVirtualPath(XlsxConstants.SIMPLE_DATA_TEMPLATE_PATH);
        dataSheet = wk.getSheet(XlsxConstants.SHEET_DATA);
        wk.clearSheets();

        labelStyle = this.getCellStyleId(0, 0);
        valueStyle = this.getCellStyleId(1, 0);
        titleStyle = this.getCellStyleId(0, 1);
    }

    String getCellStyleId(int row, int col) {
        ICellView cell = dataSheet.getTable().getCell(row, col);
        return cell != null ? cell.getStyleId() : null;
    }

    public ExcelWorkbook build(ImportModel model) {
        for (ImportSheetModel sheetModel : model.getSheets()) {
            ExcelSheet sheet = buildSheet(sheetModel);
            wk.addSheet(sheet);
        }
        return wk;
    }

    ExcelSheet buildSheet(ImportSheetModel sheetModel) {
        ExcelSheet sheet = new ExcelSheet();
        sheet.setName(sheetModel.getName());
        sheet.setLocation(sheetModel.getLocation());

        TreeObjectLayout layout = new TreeObjectLayout();
        TreeCell rootCell = layout.init(sheetModel);

        assignToTable(sheet.getTable(), rootCell.getChildren());

        addValidation(rootCell, sheetModel);
        return sheet;
    }

    private void assignToTable(ExcelTable table, List<TreeCell> cells) {
        for (TreeCell cell : cells) {
            if (cell.isVirtual()) {
                if (cell.getChildren() != null)
                    assignToTable(table, cell.getChildren());
                continue;
            }

            ExcelCell ec = (ExcelCell) table.makeCell(cell.getRowIndex(), cell.getColIndex());
            if (STYLE_ID_HEADER.equals(cell.getStyleId())) {
                ec.setStyleId(labelStyle);
                ImportFieldModel field = (ImportFieldModel) cell.getValue();
                ec.setValue(field.getDisplayNameOrName());
            } else if (STYLE_ID_LABEL.equals(cell.getStyleId())) {
                ImportFieldModel field = (ImportFieldModel) cell.getValue();
                ec.setStyleId(labelStyle);
                ec.setValue(field.getDisplayNameOrName());
            } else if (STYLE_ID_VALUE.equals(cell.getStyleId())) {
                ec.setStyleId(valueStyle);
            } else if (STYLE_ID_TITLE.equals(cell.getStyleId())) {
                ec.setStyleId(titleStyle);
                ImportFieldModel field = (ImportFieldModel) cell.getValue();
                ec.setValue(field.getDisplayNameOrName());
            }else if(STYLE_ID_AUTO_SEQ.equals(cell.getStyleId())){
                ec.setStyleId(labelStyle);
                ec.setValue("序号");
            }else if(STYLE_ID_SEQ.equals(cell.getStyleId())){
                ec.setStyleId(valueStyle);
                ec.setValue("1");
            }
        }
    }

    void addValidation(TreeCell cell, ImportSheetModel sheetModel) {
        for (TreeCell child : cell.getChildren()) {
            if (!STYLE_ID_COL.equals(child.getStyleId()))
                continue;

            TreeCell fieldCell = child.getChildren().get(1);
            ImportFieldModel field = (ImportFieldModel) fieldCell.getValue();
            if (field != null && field.getSchema() != null && field.getSchema().getDict() != null) {
                String dictName = field.getSchema().getDict();
                DictBean dict = DictProvider.instance().getDict(null, dictName, cache, scope);

                ExcelDataValidation validation = ExcelDataValidation.buildFromDict(dict);
                String start = CellPosition.toABString(fieldCell.getRowIndex(), fieldCell.getColIndex());
                String end = CellPosition.toABString(CellPosition.MAX_ROWS, fieldCell.getColIndex());
                validation.setSqref(start + ":" + end);
            }
        }
    }

}