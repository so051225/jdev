package com.redsamurai.view;

import com.redsamurai.view.utils.ADFUtils;

import java.util.HashMap;
import java.util.Map;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.AttributeHints;
import oracle.jbo.ViewObject;
import oracle.jbo.server.AttributeDefImpl;

public class AttributeTypeMap extends HashMap {
    @SuppressWarnings("compatibility:3950803358050657134")
    private static final long serialVersionUID = 7946356579436921538L;

    private transient ViewObject vo;
    
    public AttributeTypeMap(Map map) {
        super(map);
    }

    public AttributeTypeMap(ViewObject vo) {
        super();
        this.vo = vo;
    }

    @Override
    public Object get(Object attrName) {
        AttributeDefImpl attrDef =
            (AttributeDefImpl) vo.getAttributeDef(vo.getAttributeIndexOf((String) attrName));

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
}
