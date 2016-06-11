package com.redsamurai.view.beans;


import com.redsamurai.view.utils.ADFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCControlBinding;
import oracle.adf.model.binding.DCControlBindingDef;
import oracle.adf.model.binding.DCDefBase;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.model.binding.DefinitionFactory;

import oracle.jbo.AttributeDef;
import oracle.jbo.AttributeHints;
import oracle.jbo.ViewObject;
import oracle.jbo.common.ListBindingDef;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.uicli.binding.JUCtrlAttrsBinding;
import oracle.jbo.uicli.binding.JUCtrlListBinding;
import oracle.jbo.uicli.binding.JUCtrlValueDef;
import oracle.jbo.uicli.mom.JUMetaObjectManager;
import oracle.jbo.uicli.mom.JUTags;


public class Employees {
    private static final String ITERATOR = "EmployeesView1Iterator";
    
    public Employees() {
        super();
    }
    
    public AttributeDef[] getAttributeDefs() {
        DCIteratorBinding dcIter = ADFUtils.findIterator(ITERATOR);

        AttributeDef[] attrDefsAll = dcIter.getAttributeDefs();
        ArrayList<AttributeDef> attrDefsList = new ArrayList<AttributeDef>();

        for (int i = 0; i < attrDefsAll.length; i++) {
            AttributeDef attrDef = attrDefsAll[i];
            String attrDisplayHint = (String)attrDef.getProperty(AttributeDefImpl.ATTRIBUTE_DISPLAY_HINT);
            
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
    
    public Map getAttributeType() {
        return new HashMap() {
            @Override
            public Object get(Object attrName) {
                DCIteratorBinding dcIter = ADFUtils.findIterator(ITERATOR);
                ViewObject vo = dcIter.getViewObject();
                AttributeDefImpl attrDef = (AttributeDefImpl)vo.getAttributeDef(vo.getAttributeIndexOf((String)attrName));

                if (attrDef.getJavaType().getName().equals("oracle.jbo.domain.Timestamp")) {
                    return "date";
                }

                String attrType = attrDef.getJavaType().getName();
                if (attrType.equals("java.lang.Integer") || attrType.equals("oracle.jbo.domain.Number") ||
                    attrType.equals("oracle.sql.REF") || attrType.equals("java.lang.Float") ||
                    attrType.equals("java.math.BigDecimal") || attrType.equals("java.lang.Long") ||
                    attrType.equals("java.lang.Double")) {
                    return "number";
                }
                
                Object attrCtlType = attrDef.getProperty(AttributeHints.ATTRIBUTE_CTL_TYPE);
                if (attrCtlType != null) {
                    if (attrCtlType.equals(AttributeHints.CTL_INPUT_TEXT_LOV)) {
                        return "input_text_lov";
                    }
                }

                return "default";
            }
        };
    }
    
    public Map getDynamicAttribute() {
        return new HashMap() {

            @Override
            public Object get(Object attrName) {
                DCControlBinding dynamicAttr = (DCControlBinding)ADFUtils.findControlBinding((String)attrName);
                
                if (dynamicAttr == null) {
                    dynamicAttr = createDynamicAttribute((String)attrName);
                }
                
                return dynamicAttr;
            }
        };
    }
    
    private JUCtrlAttrsBinding createDynamicAttribute(String attrName) {
        DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
        JUCtrlValueDef attrDef = (JUCtrlValueDef) defFactory.createControlDef(DCDefBase.PNAME_TextField);
        
        HashMap initValues = new HashMap();  
        initValues.put(DCControlBindingDef.PNAME_IterBinding, ITERATOR);    
        initValues.put(JUCtrlValueDef.PNAME_AttrNames, new String[] { attrName });  
        initValues.put(JUTags.ID, attrName);  
        attrDef.init(initValues);  
        
        DCBindingContainer bindings = ADFUtils.getDCBindingContainer();
        JUCtrlAttrsBinding attr = (JUCtrlAttrsBinding)attrDef.createControlBinding(bindings);
        bindings.addControlBinding(attrDef.getName(), attr);
        
        return attr;  
    }
    
    public Map getDynamicLovAttribute() {
        return new HashMap() {
            @Override
            public Object get(Object attrName) {
                String comboLov = "LOV_" + attrName;

                DCControlBinding dynamicLov = (DCControlBinding)ADFUtils.findControlBinding(comboLov);

                if (dynamicLov == null) {
                    dynamicLov = createDynamicLovAttribute((String)attrName, comboLov);
                }

                return dynamicLov;
            }
        };
    }
    
    private JUCtrlListBinding createDynamicLovAttribute(String attrName, String comboLov) {
        //Create an instance of listOfValues binding definition
        //Actually lovDef is going to be an instance of FacesCtrlLOVDef
        DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
        JUCtrlValueDef lovDef = (JUCtrlValueDef)defFactory.createControlDef(JUTags.PNAME_listOfValues);

        //Initialize listOfValues binding definition
        HashMap initValues = new HashMap();
        initValues.put(DCControlBindingDef.PNAME_IterBinding, ITERATOR);
        initValues.put(ListBindingDef.PNAME_ListServerBindingName, comboLov);
        initValues.put(ListBindingDef.PNAME_AttrNames, new String[] { attrName });
        initValues.put(JUTags.ID, comboLov);
        lovDef.init(initValues);

        DCBindingContainer bindings = ADFUtils.getDCBindingContainer();

        //Create an instance of listOfValues binding
        JUCtrlListBinding lov = (JUCtrlListBinding)lovDef.createControlBinding(bindings);

        //Add the instance to the current binding container
        bindings.addControlBinding(lovDef.getName(), lov);

        return lov;
    }
    
    public void genericValueChangeListener(ValueChangeEvent event) {
        System.out.println(event.getComponent().getAttributes().get("attrName") + " : " + event.getNewValue());
    }
}
