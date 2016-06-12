package com.redsamurai.view;

import com.redsamurai.view.utils.ADFUtils;

import java.util.HashMap;
import java.util.Map;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCControlBinding;
import oracle.adf.model.binding.DCControlBindingDef;
import oracle.adf.model.binding.DefinitionFactory;

import oracle.jbo.common.ListBindingDef;
import oracle.jbo.uicli.binding.JUCtrlListBinding;
import oracle.jbo.uicli.binding.JUCtrlValueDef;
import oracle.jbo.uicli.mom.JUMetaObjectManager;
import oracle.jbo.uicli.mom.JUTags;

public class DynamicLovAttributeMap extends HashMap {
    @SuppressWarnings("compatibility:-82596092328656956")
    private static final long serialVersionUID = 6677768504216406473L;

    public DynamicLovAttributeMap(Map map) {
        super(map);
    }

    transient DCBindingContainer dc; 
    String iteratorName;

    public DynamicLovAttributeMap(DCBindingContainer dc, String iteratorName) {
        super();
        this.dc = dc;
        this.iteratorName = iteratorName;
    }
    
    private JUCtrlListBinding createDynamicLovAttribute(String attrName, String comboLov) {
        //Create an instance of listOfValues binding definition
        //Actually lovDef is going to be an instance of FacesCtrlLOVDef
        DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
        JUCtrlValueDef lovDef = (JUCtrlValueDef) defFactory.createControlDef(JUTags.PNAME_listOfValues);

        //Initialize listOfValues binding definition
        HashMap initValues = new HashMap();
        initValues.put(DCControlBindingDef.PNAME_IterBinding, this.iteratorName);
        initValues.put(ListBindingDef.PNAME_ListServerBindingName, comboLov);
        initValues.put(ListBindingDef.PNAME_AttrNames, new String[] { attrName });
        initValues.put(JUTags.ID, comboLov);
        lovDef.init(initValues);

        DCBindingContainer bindings = dc;

        //Create an instance of listOfValues binding
        JUCtrlListBinding lov = (JUCtrlListBinding) lovDef.createControlBinding(bindings);

        //Add the instance to the current binding container
        bindings.addControlBinding(lovDef.getName(), lov);

        return lov;
    }
    
    @Override
    public Object get(Object attrName) {
        String comboLov = "LOV_" + attrName;

        DCControlBinding dynamicLov = dc.findCtrlBinding(comboLov);

        if (dynamicLov == null) {
            dynamicLov = createDynamicLovAttribute((String) attrName, comboLov);
        }

        return dynamicLov;
    }
}
