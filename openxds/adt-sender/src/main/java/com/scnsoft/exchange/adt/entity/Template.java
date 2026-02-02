package com.scnsoft.exchange.adt.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by averazub on 10/6/2016.
 */
public class Template {
    String sourceTemplate;

    Map<String, String> replacements;
    List<String> blocksToInclude;

    public Template(String sourceTemplate) {
        this.sourceTemplate = sourceTemplate;
        replacements = new HashMap<String, String>();
        blocksToInclude = new ArrayList<String>();
    }

    public String getSourceTemplate() {
        return sourceTemplate;
    }

    public void setSourceTemplate(String sourceTemplate) {
        this.sourceTemplate = sourceTemplate;
    }

    public Template addReplacements(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field: fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(o);
                String fieldStr = fieldValue==null?"":fieldValue.toString();
                addReplacement(field.getName(), fieldStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public Template addReplacement(String placeholder, String replacement) {
        replacement = replacement
                .replaceAll("&amp;","&")
                .replaceAll("&", "&amp;");

        this.replacements.put(placeholder, replacement);
        return this;
    }

    public Template includeBlock(String blockName, boolean doInclude) {
        if (doInclude) includeBlock(blockName);
        return this;
    }

    public Template includeBlock(String blockName) {
        this.blocksToInclude.add(blockName);
        return this;
    }

    public Template clear() {
        replacements = new HashMap<String, String>();
        blocksToInclude = new ArrayList<String>();
        return this;
    }

    public String build() {
        String result = new String(sourceTemplate);
        for (Map.Entry<String, String> replacement: replacements.entrySet()) {
            result = result.replaceAll("\\$\\{"+replacement.getKey()+"\\}", replacement.getValue());
        }
        for (String block: blocksToInclude) {
            result = result.replaceAll("\\<\\!\\-\\-\\{"+block+"\\}","");
            result = result.replaceAll("\\{"+block+"\\}\\-\\-\\>","");
        }
        return result;
    }


}
