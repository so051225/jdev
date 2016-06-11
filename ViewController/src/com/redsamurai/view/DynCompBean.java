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
import oracle.adf.model.binding.DCControlBinding;
import oracle.adf.model.binding.DCControlBindingDef;
import oracle.adf.model.binding.DCDefBase;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.model.binding.DefinitionFactory;

import oracle.adf.view.rich.component.rich.fragment.RichDynamicDeclarativeComponent;

import oracle.jbo.AttributeDef;
import oracle.jbo.AttributeHints;
import oracle.jbo.ViewObject;
import oracle.jbo.common.ListBindingDef;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.uicli.binding.JUCtrlAttrsBinding;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlListBinding;
import oracle.jbo.uicli.binding.JUCtrlValueDef;
import oracle.jbo.uicli.mom.JUMetaObjectManager;
import oracle.jbo.uicli.mom.JUTags;


public class DynCompBean {

    public DynCompBean() {
        super();
    }

    public AttributeDef[] getAttributeDefs() {
        System.err.println("iter name: " + this.getIteratorName());
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
            //ViewObject vo = ADFUtils.findIterator(this.getIteratorName()).getViewObject();
            
            DCIteratorBinding dciter = ADFUtils.findIterator(this.getIteratorName());
            attributeMap = new AttributeTypeMap(dciter.getViewObject());

        }
        return attributeMap;
    }

    public Map getDynamicAttribute() {
        return new HashMap() {
            @Override
            public Object get(Object attrName) {
                DCControlBinding dynamicAttr = (DCControlBinding) ADFUtils.findControlBinding((String) attrName);

                if (dynamicAttr == null) {
                    dynamicAttr = createDynamicAttribute((String) attrName);
                }

                return dynamicAttr;
            }
        };
    }

    private JUCtrlAttrsBinding createDynamicAttribute(String attrName) {
        DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
        JUCtrlValueDef attrDef = (JUCtrlValueDef) defFactory.createControlDef(DCDefBase.PNAME_TextField);

        HashMap initValues = new HashMap();
        initValues.put(DCControlBindingDef.PNAME_IterBinding, this.getIteratorName());
        initValues.put(JUCtrlValueDef.PNAME_AttrNames, new String[] { attrName });
        initValues.put(JUTags.ID, attrName);
        attrDef.init(initValues);

        DCBindingContainer bindings = ADFUtils.getDCBindingContainer();
        JUCtrlAttrsBinding attr = (JUCtrlAttrsBinding) attrDef.createControlBinding(bindings);
        bindings.addControlBinding(attrDef.getName(), attr);

        return attr;
    }

    public Map getDynamicLovAttribute() {
        return new HashMap() {
            @Override
            public Object get(Object attrName) {
                String comboLov = "LOV_" + attrName;

                DCControlBinding dynamicLov = (DCControlBinding) ADFUtils.findControlBinding(comboLov);

                if (dynamicLov == null) {
                    dynamicLov = createDynamicLovAttribute((String) attrName, comboLov);
                }

                return dynamicLov;
            }
        };
    }

    private JUCtrlListBinding createDynamicLovAttribute(String attrName, String comboLov) {
        //Create an instance of listOfValues binding definition
        //Actually lovDef is going to be an instance of FacesCtrlLOVDef
        DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
        JUCtrlValueDef lovDef = (JUCtrlValueDef) defFactory.createControlDef(JUTags.PNAME_listOfValues);

        //Initialize listOfValues binding definition
        HashMap initValues = new HashMap();
        initValues.put(DCControlBindingDef.PNAME_IterBinding, this.getIteratorName());
        initValues.put(ListBindingDef.PNAME_ListServerBindingName, comboLov);
        initValues.put(ListBindingDef.PNAME_AttrNames, new String[] { attrName });
        initValues.put(JUTags.ID, comboLov);
        lovDef.init(initValues);

        DCBindingContainer bindings = ADFUtils.getDCBindingContainer();

        //Create an instance of listOfValues binding
        JUCtrlListBinding lov = (JUCtrlListBinding) lovDef.createControlBinding(bindings);

        //Add the instance to the current binding container
        bindings.addControlBinding(lovDef.getName(), lov);

        return lov;
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


    //Generate name of the tree bindning as "TREE_" + component's Id
    //For example: TREE_dc1, TREE_dc2, ...
    private String getTreeName() {
        //return new StringBuilder(TREE_NAME).append(getID()).toString();
        return this.getTreeBindingName();
    }

    //Generate name of the iterator bindning as "Iterator_" + component's Id
    //For example: Iterator_dc1, Iterator_dc2, ...
    private String getIteratorName() {
        //return new StringBuilder(ITERATOR_NAME).append(getID()).toString();
        return (String) getMyself().getAttributes().get("IteratorName");
    }
    
    private String getTreeBindingName() {
        //return new StringBuilder(ITERATOR_NAME).append(getID()).toString();
        return (String) getMyself().getAttributes().get("TreeBindingName");
    }


    //Getters for the component's attributes - ViewObjectName, RSIName, DataControlName, id
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


    //A couple of helper methods to get an instance of our component
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
