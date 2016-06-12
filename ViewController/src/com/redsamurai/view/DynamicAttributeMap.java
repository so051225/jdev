package com.redsamurai.view;

import com.redsamurai.view.utils.ADFUtils;

import java.util.HashMap;
import java.util.Map;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCControlBinding;
import oracle.adf.model.binding.DCControlBindingDef;
import oracle.adf.model.binding.DCDefBase;
import oracle.adf.model.binding.DefinitionFactory;

import oracle.jbo.uicli.binding.JUCtrlAttrsBinding;
import oracle.jbo.uicli.binding.JUCtrlValueDef;
import oracle.jbo.uicli.mom.JUMetaObjectManager;
import oracle.jbo.uicli.mom.JUTags;

public class DynamicAttributeMap extends HashMap {
    public DynamicAttributeMap(Map map) {
        super(map);
    }
    
    transient DCBindingContainer dc; 
    String iteratorName;

    public DynamicAttributeMap(DCBindingContainer dc, String iteratorName) {
        super();
        this.dc = dc;
        this.iteratorName = iteratorName;
    }

    @Override
    public Object get(Object attrName) {
        
        DCControlBinding dynamicAttr = dc.findCtrlBinding((String) attrName);

        if (dynamicAttr == null) {
            dynamicAttr = createDynamicAttribute((String) attrName);
        }

        return dynamicAttr;
    }
    
    private JUCtrlAttrsBinding createDynamicAttribute(String attrName) {
        DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
        JUCtrlValueDef attrDef = (JUCtrlValueDef) defFactory.createControlDef(DCDefBase.PNAME_TextField);

        HashMap initValues = new HashMap();
        initValues.put(DCControlBindingDef.PNAME_IterBinding, this.iteratorName);
        initValues.put(JUCtrlValueDef.PNAME_AttrNames, new String[] { attrName });
        initValues.put(JUTags.ID, attrName);
        attrDef.init(initValues);

        DCBindingContainer bindings = dc;
        JUCtrlAttrsBinding attr = (JUCtrlAttrsBinding) attrDef.createControlBinding(bindings);
        bindings.addControlBinding(attrDef.getName(), attr);

        return attr;
    }
}
