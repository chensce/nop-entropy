/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.report.core.engine.expand;

import io.nop.commons.collections.iterator.FillMinIterator;
import io.nop.commons.collections.iterator.LimitIterator;
import io.nop.commons.mutable.MutableInt;
import io.nop.commons.util.CollectionHelper;
import io.nop.commons.util.MathHelper;
import io.nop.commons.util.StringHelper;
import io.nop.excel.model.XptCellModel;
import io.nop.excel.model.constants.XptExpandType;
import io.nop.report.core.dataset.DynamicReportDataSet;
import io.nop.report.core.engine.IXptRuntime;
import io.nop.report.core.engine.renderer.HtmlRenderHelper;
import io.nop.report.core.model.ExpandedCell;
import io.nop.report.core.model.ExpandedTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractCellExpander implements ICellExpander {
    static final Logger LOG = LoggerFactory.getLogger(AbstractCellExpander.class);

    static class ExpandCounter {
        int count;
        int incSpan;
        int realIncSpan;
        int minReuse = Integer.MAX_VALUE;
        int maxReuse;
    }

    @Override
    public void expand(ExpandedCell cell, Deque<ExpandedCell> processing, IXptRuntime xptRt) {
        //  LOG.info("nop.report.expand-cell:cell={},expandType={},rowParentExpandIndex={},colParentExpandIndex={}",
        //          cell.getName(), cell.getExpandType(), cell.getRowParentExpandIndex(), cell.getColParentExpandIndex());

        Iterator<?> expandList = runExpandExpr(cell, xptRt);
        if (!expandList.hasNext()) {
            if (cell.getModel() != null && cell.getModel().shouldRemoveEmpty()) {
                removeCell(cell);
            } else {
                clearCell(cell);
            }
        } else {
            Object value = expandList.next();
            cell.setExpandIndex(0);
            cell.setExpandValue(value);

            ExpandCounter counter = duplicate(expandList, cell, processing);
            if (counter.incSpan > 0)
                extendCells(cell, counter);
        }

        if (xptRt.getWorkbook().isEnableDump())
            HtmlRenderHelper.dumpHtml(xptRt.getWorkbook(), cell.getSheet(), StringHelper.leftPad(MathHelper.nextSeq() + "-", 4, '0') + cell.getName() + ".html");
    }

    protected Iterator<?> runExpandExpr(ExpandedCell cell, IXptRuntime xptRt) {
        Iterator<?> it = runExpandExpr0(cell, xptRt);
        if (it == null)
            it = Collections.emptyListIterator();

        XptCellModel cellModel = cell.getModel();
        if (cellModel == null)
            return it;

        if (cellModel.getExpandMaxCount() != null && cellModel.getExpandMaxCount() >= 0) {
            it = new LimitIterator<>(it, cellModel.getExpandMaxCount());
        }

        if (cellModel.getExpandMinCount() != null && cellModel.getExpandMinCount() > 0) {
            it = new FillMinIterator<>(it, cellModel.getExpandMinCount(), null);
        }
        return it;
    }

    protected Iterator<?> runExpandExpr0(ExpandedCell cell, IXptRuntime xptRt) {
        XptCellModel model = cell.getModel();
        xptRt.setCell(cell);

        if (model.getExpandExpr() == null) {
            // 如果既没有指定expandExpr，也没有指定field，则本单元格相当于是不展开，仅仅是设置一下expandType
            String field = model.getField();
            if (field == null)
                return Collections.singletonList(cell.getValue()).iterator();

            // 如果没有指定expandExpr，则根据ds.group(field)来获取数据
            String dsName = model.getDs();
            if (dsName == null) {
                return valueToIterator(xptRt.field(field));
            }

            DynamicReportDataSet ds = DynamicReportDataSet.makeDataSet(xptRt, dsName);
            return ds.group(field).iterator();
        }

        Object value = model.getExpandExpr().invoke(xptRt);
        return valueToIterator(value);
    }

    Iterator<?> valueToIterator(Object value) {
        return CollectionHelper.toIterator(value, true);
    }

    protected abstract void removeCell(ExpandedCell cell);

    protected abstract void extendCells(ExpandedCell cell, ExpandCounter counter);

    protected ExpandCounter duplicate(Iterator<?> it, ExpandedCell cell,
                                      Deque<ExpandedCell> processing) {

        ExpandCounter counter = new ExpandCounter();
        counter.count = 1;

        int expandIndex = 0;
        while (it.hasNext()) {
            expandIndex++;
            counter.count++;
            Object expandValue = it.next();

            duplicateCell(cell, expandIndex, expandValue, processing, counter);
        }

        return counter;
    }

    protected abstract void duplicateCell(ExpandedCell cell, int expandIndex, Object expandValue,
                                         Collection<ExpandedCell> processing, ExpandCounter counter);

    protected Map<String, List<ExpandedCell>> getNewListMap(Map<String, List<ExpandedCell>> listMap, Map<ExpandedCell, ExpandedCell> cellMap) {
        Map<String, List<ExpandedCell>> ret = CollectionHelper.newHashMap(listMap.size());
        for (Map.Entry<String, List<ExpandedCell>> entry : listMap.entrySet()) {
            List<ExpandedCell> list = entry.getValue();
            List<ExpandedCell> newList = getNewList(list, cellMap);
            ret.put(entry.getKey(), newList);
        }
        return ret;
    }

    protected List<ExpandedCell> getNewList(List<ExpandedCell> list, Map<ExpandedCell, ExpandedCell> cellMap) {
        List<ExpandedCell> ret = new ArrayList<>(list.size());
        for (ExpandedCell cell : list) {
            ExpandedCell newCell = cellMap.get(cell);
            if (newCell == null)
                newCell = cell;
            ret.add(newCell);
        }
        return ret;
    }

    protected void copyCellValue(ExpandedCell nextCell, ExpandedCell newCell, boolean row) {
        newCell.setModel(nextCell.getModel());
        newCell.setValue(nextCell.getValue());
        if (!nextCell.isProxyCell()) {
            newCell.setMergeAcross(nextCell.getMergeAcross());
            newCell.setMergeDown(nextCell.getMergeDown());
        }
        newCell.setStyleId(nextCell.getStyleId());
        newCell.setRowParent(nextCell.getRowParent());
        newCell.setColParent(nextCell.getColParent());
    }

    protected void clearCell(ExpandedCell cell) {
        cell.markEvaluated();

        XptExpandType expandType = cell.getExpandType();
        Map<String, List<ExpandedCell>> descendants;
        if (expandType == XptExpandType.c) {
            descendants = cell.getColDescendants();
        } else {
            descendants = cell.getRowDescendants();
        }
        if (descendants != null) {
            for (List<ExpandedCell> list : descendants.values()) {
                for (ExpandedCell child : list) {
                    child.markEvaluated();
                }
            }
        }
    }

    protected void initRowParentAndColParent(Map<ExpandedCell, ExpandedCell> cellMap) {
        for (Map.Entry<ExpandedCell, ExpandedCell> entry : cellMap.entrySet()) {
            ExpandedCell cell = entry.getKey();
            ExpandedCell newCell = entry.getValue();

            ExpandedCell colParent = cell.getColParent();
            if (colParent != null) {
                ExpandedCell newParent = cellMap.get(colParent);
                if (newParent == null)
                    newParent = colParent;
                newCell.setColParent(newParent);
            }

            ExpandedCell rowParent = cell.getRowParent();
            if (rowParent != null) {
                ExpandedCell newParent = cellMap.get(rowParent);
                if (newParent == null)
                    newParent = rowParent;
                newCell.setRowParent(newParent);
            }
        }
    }

    /**
     * 维护新建单元格的父子关系
     */
    protected void addNewCellToParentDescendants(ExpandedTable table, Map<ExpandedCell, ExpandedCell> cellMap) {
        for (ExpandedCell newCell : cellMap.values()) {
            table.addNamedCell(newCell);

            if (newCell.getMergeAcross() > 0 || newCell.getMergeDown() > 0)
                newCell.markProxy();

            // 新生成的单元格又可能既有rowParent，又有colParent，都需要维护对应的父子关系
            if (newCell.getRowParent() != null) {
                newCell.getRowParent().addRowChild(newCell);
            }

            if (newCell.getColParent() != null) {
                newCell.getColParent().addColChild(newCell);
            }
        }
    }
}