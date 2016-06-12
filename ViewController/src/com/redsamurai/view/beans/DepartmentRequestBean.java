package com.redsamurai.view.beans;

import java.util.ArrayList;

import java.util.List;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.binding.BindingContainer;

import oracle.jbo.Key;
import oracle.jbo.Row;

import oracle.jbo.SortCriteria;

import org.apache.myfaces.trinidad.event.SortEvent;
import org.apache.myfaces.trinidad.model.SortCriterion;

public class DepartmentRequestBean {
    private RichTable tblDepartment;

    public DepartmentRequestBean() {
    }

    public void onDelete(ActionEvent actionEvent) {
        Row row = this.getIterator().getCurrentRow();

        printRow(row);


        System.err.println("active row key: " + this.getTblDepartment().getActiveRowKey());


        //if (row != null)
    }

    private void printRow(Row r) {

        System.err.println("****** print row ******: " + r);
        if (r != null) {
            int count = r.getAttributeCount();
            for (int i = 0; i < count; i++) {
                System.err.println(r.getAttributeNames()[i] + ": " + r.getAttribute(i));
            }
        }
        System.err.println("^^^^^^ end print row ^^^^^^");
    }

    private DCIteratorBinding getIterator() {
        BindingContext bindingContext = BindingContext.getCurrent();
        BindingContainer bindings = bindingContext.getCurrentBindingsEntry();

        DCIteratorBinding iter = (DCIteratorBinding) bindings.get("VDepartment1Iterator");
        return iter;
    }

    public void setTblDepartment(RichTable tblDepartment) {
        this.tblDepartment = tblDepartment;
    }

    public RichTable getTblDepartment() {
        return tblDepartment;
    }

    public void resetSort(ActionEvent actionEvent) {
        // get the binding container
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        DCBindingContainer dcBindings = (DCBindingContainer) bindings;
        RichTable table = getTblDepartment();
        table.queueEvent(new SortEvent(table, new ArrayList<SortCriterion>()));
        DCIteratorBinding iterBind = (DCIteratorBinding) dcBindings.get("VDepartment1Iterator");
        Key currentRow = null;
        Row row = iterBind.getCurrentRow();
        if (row != null)
            currentRow = row.getKey();
        SortCriteria[] sc = new SortCriteria[0];
        iterBind.applySortCriteria(sc); // iterBind is the iterator the table uses
        iterBind.executeQuery();
        if (currentRow != null)
            iterBind.setCurrentRowWithKey(currentRow.toStringFormat(true));
    }

    public void onSort(SortEvent sortEvent) {
        //SortCriterion sc = sortEvent.getSortCriteria().get(0);
        
        List<SortCriterion> sortedTableList = new ArrayList<SortCriterion>();
        
        SortCriterion sc1 = new SortCriterion("LocationId", true);
        SortCriterion sc2 = new SortCriterion("DepartmentName", true);
        
        sortedTableList.add(sc1);
        sortedTableList.add(sc2);
        
        RichTable richTable = (RichTable) sortEvent.getComponent();
        richTable.setSortCriteria(sortedTableList);
    }
}
