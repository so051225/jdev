package com.redsamurai.view;

import com.redsamurai.view.utils.ADFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.fragment.RichDynamicDeclarativeComponent;

import oracle.jbo.AttributeDef;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;


public class DynCompBean {

    public DynCompBean() {
        super();
    }

    public AttributeDef[] getAttributeDefs() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(this.getIteratorName());

        AttributeDef[] attrDefsAll = dcIter.getAttributeDefs();
        ArrayList<AttributeDef> attrDefsList = new ArrayList<AttributeDef>();

        for (int i = 0; i < attrDefsAll.length; i++) {
            AttributeDef attrDef = attrDefsAll[i];
            String attrDisplayHint = (String) attrDef.getProperty(AttributeDefImpl.ATTRIBUTE_DISPLAY_HINT);

            if (!"Hide".equals(attrDisplayHint) && attrDef.getAttributeKind() != AttributeDefImpl.ATTR_ROWSET) {
                attrDefsList.add(attrDef);
            }
        }

        AttributeDef[] attrDefs = new AttributeDef[attrDefsList.size()];
        for (int i = 0; i < attrDefsList.size(); i++) {
            attrDefs[i] = attrDefsList.get(i);
        }

        return attrDefs;
    }
    
    Map attributeMap = null;
    public Map getAttributeType() {
        if (attributeMap==null) {
            DCIteratorBinding dciter = ADFUtils.findIterator(this.getIteratorName());
            attributeMap = new AttributeTypeMap(dciter.getViewObject());
        }
        return attributeMap;
    }

    Map dynamicAttributeMap = null;
    public Map getDynamicAttribute() {
        if (dynamicAttributeMap == null) {
            dynamicAttributeMap = new DynamicAttributeMap(ADFUtils.getDCBindingContainer(), this.getIteratorName());
        }
        
        return dynamicAttributeMap;
    }

    Map dynamicLovAttributeMap = null;
    public Map getDynamicLovAttribute() {
        if (dynamicLovAttributeMap == null) {
            dynamicLovAttributeMap = new DynamicLovAttributeMap(ADFUtils.getDCBindingContainer(), this.getIteratorName());
        }
        return dynamicLovAttributeMap;
    }

    public void genericValueChangeListener(ValueChangeEvent event) {
        System.out.println(event.getComponent().getAttributes().get("attrName") + " : " + event.getNewValue());
    }


    private DCBindingContainer getBindings() {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer dcb = (DCBindingContainer) bc.getCurrentBindingsEntry();
        return dcb;

    }

    public DCIteratorBinding getIterator() {
        DCBindingContainer dcb = getBindings();
        DCIteratorBinding jub = dcb.findIteratorBinding(this.getIteratorName());
        return jub;
    }


    public JUCtrlHierBinding getTree() {
        DCBindingContainer dcb = getBindings();

        //May be the VEmp tree binding is already created
        JUCtrlHierBinding chb = (JUCtrlHierBinding) dcb.findCtrlBinding(getTreeName());
        return chb;
    }

    private String getTreeName() {
        return this.getTreeBindingName();
    }

    private String getIteratorName() {
        return (String) getMyself().getAttributes().get("IteratorName");
    }
    
    private String getTreeBindingName() {
        return (String) getMyself().getAttributes().get("TreeBindingName");
    }

    private String getVOName() {
        return (String) getMyself().getAttributes().get("ViewObjectName");
    }

    private String getRSIName() {
        return (String) getMyself().getAttributes().get("RSIName");
    }

    private String getDataControlName() {
        return (String) getMyself().getAttributes().get("DataControlName");
    }

    private String getID() {
        return (String) getMyself().getAttributes().get("id");
    }

    private RichDynamicDeclarativeComponent getMyself() {
        RichDynamicDeclarativeComponent _this =
            (RichDynamicDeclarativeComponent) getValueObject("#{dyntable}", RichDynamicDeclarativeComponent.class);
        return _this;
    }

    private static Object getValueObject(String expr, Class returnType) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elctx = fc.getELContext();
        ExpressionFactory elFactory = fc.getApplication().getExpressionFactory();
        ValueExpression valueExpr = elFactory.createValueExpression(elctx, expr, returnType);
        return valueExpr.getValue(elctx);
    }

}
