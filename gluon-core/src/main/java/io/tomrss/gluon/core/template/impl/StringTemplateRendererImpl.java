package io.tomrss.gluon.core.template.impl;

import io.tomrss.gluon.core.template.GluonTemplateException;
import io.tomrss.gluon.core.template.StringTemplateRenderer;
import io.tomrss.gluon.core.util.CaseUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTemplateRendererImpl implements StringTemplateRenderer {
    public static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\{\\{([a-zA-Z_.-]+)}}");
    public static final String PROPERTY_PATH_SEPARATOR = "\\.";
    //TODO using different impl of StringTemplateRender will break this regex!
    public static final Pattern ENTITY_TEMPLATE_PATTERN = Pattern.compile("\\{\\{entity(\\..+)?}}");

    @Override
    public String render(String stringTemplate, Object model) {
        final Matcher m = TEMPLATE_VARIABLE_PATTERN.matcher(stringTemplate);
        final StringBuilder sb = new StringBuilder();
        while (m.find()) {
            final String templateVariable = m.group(1);
            final String[] propertyPath = templateVariable.split(PROPERTY_PATH_SEPARATOR);
            final String resolvedVariableValue = resolvePathTemplateVariableValue(templateVariable, propertyPath, model);
            m.appendReplacement(sb, resolvedVariableValue);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String resolvePathTemplateVariableValue(String fullPropertyPath, String[] propertyPath, Object model) {
        if (propertyPath.length == 0) {
            return model.toString();
        }
        final String property = propertyPath[0];
        Object value;
        if (model instanceof Map) {
            value = ((Map<?, ?>) model).get(property);
        } else {
            try {
                value = tryToGetPropertyValue(model, property);
            } catch (InvocationTargetException e) {
                throw new GluonTemplateException("Error trying to get a value of property " + property +
                        " in object " + model +
                        " of class " + model.getClass() + ". " +
                        "Full propertyPath is " + fullPropertyPath,
                        e);
            }
        }
        if (value == null) {
            throw new GluonTemplateException("Property " + property +
                    " in object " + model +
                    " of class " + model.getClass() + " not found. " +
                    "Full propertyPath is " + fullPropertyPath);
        }
        final String[] restOfPropertyPath = Arrays.copyOfRange(propertyPath, 1, propertyPath.length);
        return resolvePathTemplateVariableValue(fullPropertyPath, restOfPropertyPath, value);
    }

    private Object tryToGetPropertyValue(Object model, String property) throws InvocationTargetException {
        try {
            return tryGetPublicField(model, property);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            try {
                return tryGetByOldSchoolGetterMethod(model, property);
            } catch (IllegalAccessException | NoSuchMethodException ex) {
                try {
                    return tryGetByRecordLikeGetterMethod(model, property);
                } catch (NoSuchMethodException | IllegalAccessException exc) {
                    return null;
                }
            }
        }
    }

    private Object tryGetByRecordLikeGetterMethod(Object model, String property) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return model.getClass()
                .getMethod(property)
                .invoke(model);
    }

    private Object tryGetByOldSchoolGetterMethod(Object model, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final String getterName = "get" + CaseUtils.capitalize(property);
        return model.getClass()
                .getMethod(getterName)
                .invoke(model);
    }

    private Object tryGetPublicField(Object model, String property) throws IllegalAccessException, NoSuchFieldException {
        return model.getClass()
                .getField(property)
                .get(model);
    }
}
