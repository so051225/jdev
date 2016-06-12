package adf.sample.view.bean;

import adf.sample.view.GenericTableSelectionHandler;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.BindingContainer;

import oracle.jbo.Row;

import org.apache.myfaces.trinidad.event.SelectionEvent;

public class DepartmentsBacking {
    public DepartmentsBacking() {
    }

    public void onTableSelect(SelectionEvent selectionEvent) {
        
        //pre-trigger code
        //GenericTableSelectionHandler.makeCurrent(selectionEvent);
        //post-trigger code
                          
    }

    //test row selection
    public String print_action() {
        DCIteratorBinding iter = getIterator();

        Row rw = iter.getCurrentRow();
        
        //print
        
        System.out.println("Selected Row Data: "+rw.getAttribute(0)+" "+rw.getAttribute(1));
        
        return null;
    }

    public void onDelete(ActionEvent evt) {
        String rowKeyStr = (String) evt.getComponent().getAttributes().get("rowKeyStr");
        
        //getIterator().setCurrentRowWithKey(rowKeyStr);
        getIterator().removeRowWithKey(rowKeyStr);
        
        System.out.println("Selected Row key: " + rowKeyStr);
    }

    private DCIteratorBinding getIterator() {
        BindingContext bindingContext = BindingContext.getCurrent();
        BindingContainer bindings = bindingContext.getCurrentBindingsEntry();

        DCIteratorBinding iter = (DCIteratorBinding) bindings.get("DepartmentsView1Iterator");
        return iter;
    }
}
