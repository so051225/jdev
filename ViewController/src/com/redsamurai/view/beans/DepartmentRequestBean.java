package com.redsamurai.view.beans;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.binding.BindingContainer;

import oracle.jbo.Row;

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
            for(int i=0; i<count; i++) {
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
}
