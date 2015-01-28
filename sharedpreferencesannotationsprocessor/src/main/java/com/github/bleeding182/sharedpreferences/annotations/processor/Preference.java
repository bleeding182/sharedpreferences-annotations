/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 David Medenjak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.bleeding182.sharedpreferences.annotations.processor;

import com.github.bleeding182.sharedpreferences.PreferenceType;
import com.github.bleeding182.sharedpreferences.annotations.DefaultValue;
import com.github.bleeding182.sharedpreferences.annotations.Type;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * @author David Medenjak
 * @version 1.0
 */
class Preference {
    private static final Set<Modifier> setPublic;

    static {
        setPublic = new HashSet<>();
        setPublic.add(Modifier.PUBLIC);
    }

    private static final String PARAM_DEFAULT_VALUE = "defaultValue";
    private static final String VALUE = "value";

    private final VariableElement mElement;
    private final PreferenceType mType;
    private final String mAccessorName;
    private final String mPreferenceKey;
    private final String mBooleanPrefix;
    private final boolean hasDefaultValue;
    private final boolean createDefaultGetter;
    private final String mDefaultValue;
    private final String mFieldName;

    static String camelCaseName(String name) {
        String[] split = name.toLowerCase().split("_");
        String ret = split[0];
        for (int i = 1; i < split.length; i++) {
            ret += Character.toUpperCase(split[i].charAt(0)) + split[i].substring(1);
        }
        return ret;
    }

    Preference(String fieldName, String accessorName, String preferenceKey, VariableElement element, PreferenceType defaultType) {
        mFieldName = fieldName;
        mAccessorName = accessorName;
        mPreferenceKey = preferenceKey;
        mElement = element;
        Type type = element.getAnnotation(Type.class);
        if (type == null) {
            mType = defaultType != null ? defaultType : PreferenceType.STRING;
            mBooleanPrefix = "is";
        } else {
            mType = type.value();
            mBooleanPrefix = type.booleanPrefix();
        }

        DefaultValue defValue = element.getAnnotation(DefaultValue.class);
        if (defValue != null) {
            hasDefaultValue = true;
            mDefaultValue = defValue.value();
            createDefaultGetter = defValue.createDefaultGetter();
        } else {
            hasDefaultValue = false;
            createDefaultGetter = true;
            mDefaultValue = null;
        }
    }

    VariableElement getElement() {
        return mElement;
    }

    void writeGetter(JavaWriter writer) throws IOException {
        final String prefix = mType == PreferenceType.BOOLEAN ? mBooleanPrefix : "get";

        // Create getter() for default value
        if (hasDefaultValue) {
            writer.emitEmptyLine().emitJavadoc("Getter for the value stored under the key {@code %1$s} in the preferences.\n" +
                    "The method will return {@code %2$s} if no other value has been set.\n\n" +
                    "@return the value stored under {@code %1$s} in the preferences", mPreferenceKey, mDefaultValue)
                    .beginMethod(mType.getReturnType(), prefix + getPreferenceNameUpperFirst(), setPublic)
                    .emitStatement("return get%1$s(%2$s, %3$s)",
                            mType.getFullName(), mFieldName, getTypedString(mDefaultValue)).endMethod();
        }
        if (!createDefaultGetter)
            return;
        writer.emitEmptyLine().emitJavadoc("Getter for the value stored under the key {@code %1$s} in the preferences.\n\n" +
                "@param %2$s the default value to use if no value has previously been set\n" +
                "@return the value stored under {@code %1$s} in the preferences", mPreferenceKey, PARAM_DEFAULT_VALUE)
                .beginMethod(mType.getReturnType(), prefix + getPreferenceNameUpperFirst(), setPublic, mType.getReturnType(), PARAM_DEFAULT_VALUE)
                .emitStatement("return get%1$s(%2$s, %3$s)", mType.getFullName(), mFieldName, PARAM_DEFAULT_VALUE).endMethod();
    }

    private String getTypedString(String value) {
        switch (mType) {
            case STRING:
                return "\"" + value + "\"";
            default:
                return value;
        }
    }

    private String getPreferenceNameUpperFirst() {
        return Character.toUpperCase(mAccessorName.charAt(0)) + mAccessorName.substring(1);
    }

    void writeSetter(JavaWriter writer) throws IOException {
        writer.emitEmptyLine().emitJavadoc("Sets the value for key {@code %1$s} in the preferences.\n\n" +
                "@param %2$s the new value for {@code%1$s}", mPreferenceKey, VALUE)
                .beginMethod("void", "set" + getPreferenceNameUpperFirst(), setPublic, mType.getReturnType(), VALUE)
                .emitStatement("edit().put%1$s(%2$s, %3$s).apply()", mType.getFullName(), mFieldName, VALUE)
                .endMethod();
    }

    void writeChainSetter(JavaWriter writer, String editorType, String editor) throws IOException {
        writer.emitEmptyLine().emitJavadoc("Sets the value for key {@code %1$s} in the preferences.\n\n" +
                "@param %2$s the new value for {@code %1$s}", mPreferenceKey, VALUE)
                .beginMethod(editorType, "set" + getPreferenceNameUpperFirst(), setPublic, mType.getReturnType(), VALUE)
                .emitStatement("%1$s.put%2$s(%3$s, %4$s)", editor, mType.getFullName(), mFieldName, VALUE)
                .emitStatement("return this")
                .endMethod();
    }
}
