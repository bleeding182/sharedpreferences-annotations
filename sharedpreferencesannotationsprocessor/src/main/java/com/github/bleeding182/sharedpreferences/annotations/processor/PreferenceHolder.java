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

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.github.bleeding182.sharedpreferences.PreferenceType;
import com.github.bleeding182.sharedpreferences.annotations.DefaultPreferenceName;
import com.github.bleeding182.sharedpreferences.annotations.DefaultPreferenceType;
import com.github.bleeding182.sharedpreferences.annotations.SharedPreference;

/**
 * @author David Medenjak
 * @version 1.0
 */
public class PreferenceHolder {
    private final static String PAR_CONTEXT = "ctx";
    private final static String PAR_NAME = "name";
    private final static String PAR_EDITOR = "editor";

    private final static String PREFERENCES = "mPreferences";
    private static final String EDITOR = "mEditor";

    private static final String DEFAULT_PREFERENCES_NAME = "default_preferences";

    private final TypeElement mElement;
    private final JavaWriter mWriter;

    private final String preferencesName;
    private final String className;
    private final String editorName;

    private final SortedMap<String, Preference> preferences = new TreeMap<>();


    public PreferenceHolder(TypeElement element, Filer filer, Messager messager) throws IOException {
        this.mElement = element;

        // Set the name of the file / class and nested editor
        SharedPreference sharedPreference = mElement.getAnnotation(SharedPreference.class);
        final String name = (sharedPreference.value().isEmpty()) ? mElement.getSimpleName().toString()
                : sharedPreference.value();
        className = name + sharedPreference.preferencesSuffix();
        editorName = name + sharedPreference.editorSuffix();
        JavaFileObject jfo = filer.createSourceFile(getPackageName() + "." + className);
        this.mWriter = new JavaWriter(jfo.openWriter());

        // set the name of the sharedPreferences created with the context
        DefaultPreferenceName defName = element.getAnnotation(DefaultPreferenceName.class);
        if (defName != null)
            preferencesName = defName.value();
        else
            preferencesName = DEFAULT_PREFERENCES_NAME;

        // default type if not specified
        DefaultPreferenceType defType = element.getAnnotation(DefaultPreferenceType.class);
        final PreferenceType defaultPreferenceType;
        if (defType != null)
            defaultPreferenceType = defType.value();
        else
            defaultPreferenceType = null;

        Set<String> preferenceIds = new LinkedHashSet<>();
        for (Element e : element.getEnclosedElements()) {
            if (!e.getKind().isField()) {
                messager.printMessage(Diagnostic.Kind.WARNING, e.getSimpleName() + " is not a field", e);
                continue;
            }
            VariableElement var = (VariableElement) e;
            if (!var.asType().toString().equals("java.lang.String")) {
                messager.printMessage(Diagnostic.Kind.WARNING, var.asType().toString() + " is not of type String", e);
                continue;
            }
            if (var.getConstantValue() == null) {
                messager.printMessage(Diagnostic.Kind.ERROR, var.getSimpleName() + " is not final or no value is set", e);
                continue;
            }

            final String preferenceName = Preference.camelCaseName(var.getSimpleName().toString());
            Preference old = preferences.get(preferenceName);
            if (old != null) {
                messager.printMessage(Diagnostic.Kind.WARNING, preferenceName + " used here is ignored", var);
                messager.printMessage(Diagnostic.Kind.WARNING, "because it was already defined here", old.getElement());
                continue;
            }
            final String id = var.getConstantValue().toString();
            if(!preferenceIds.add(id))
                messager.printMessage(Diagnostic.Kind.WARNING, "preference id " + id + " is already in use");
            preferences.put(preferenceName, new Preference(preferenceName, id, var, defaultPreferenceType));
        }
    }

    public void write() throws IOException {
        mWriter.setIndent("    ");
        mWriter.emitPackage(getPackageName())
                .emitSingleLineComment("generated code, do not modify")
                .emitSingleLineComment("for more information see https://github.com/bleeding182/sharedpreferences-annotations")
                .emitEmptyLine()
                .emitImports(Context.class, SharedPreferences.class)
                .emitImports("android.content.SharedPreferences.Editor",
                        "android.content.SharedPreferences.OnSharedPreferenceChangeListener")
                .emitEmptyLine()
                .emitImports(Set.class)
                .emitEmptyLine()
                .beginType(className, "class", Modifier.PUBLIC,
                        null, mElement.getSimpleName().toString(), "SharedPreferences")
                .emitEmptyLine();

        mWriter.emitField("String", "PREFERENCES_NAME", Modifier.PUBLIC_FINAL_STATIC, "\"" + preferencesName + "\"");
        mWriter.emitField("SharedPreferences", PREFERENCES, Modifier.PRIVATE_FINAL)
                .emitEmptyLine();

        // default constructor with context using default preferences name
        mWriter.emitJavadoc("constructor using '%1$s' for the preferences name.\n@param %2$s the context to use",
                preferencesName, PAR_CONTEXT)
                .beginConstructor(Modifier.PUBLIC, "Context", PAR_CONTEXT)
                .emitStatement("this(%1$s, %2$s)",
                        PAR_CONTEXT, "PREFERENCES_NAME")
                .endConstructor()
                .emitEmptyLine();

        // constructor with name for preferences
        mWriter.emitJavadoc("constructor using <i>%2$s</i> for the preferences name.<br/>\n<i>It is strongly advised against using it, unless you know what you're doing.</i>\n" +
                        "@param %3$s the context to use\n@param %2$s the name for the preferences",
                preferencesName, PAR_NAME, PAR_CONTEXT)
                .beginConstructor(Modifier.PUBLIC, "Context", PAR_CONTEXT, "String", PAR_NAME)
                .emitStatement("this.%1s = %2$s.getSharedPreferences(%3$s, %2$s.MODE_PRIVATE)",
                        PREFERENCES, PAR_CONTEXT, PAR_NAME)
                .endConstructor();

        // constructor with preferences
        mWriter.emitJavadoc("constructor using the supplied SharedPreferences\n@param %1$s the SharedPreferences to use\n", "preferences")
                .beginConstructor(Modifier.PUBLIC, "SharedPreferences", "preferences")
                .emitStatement("this.%1s = preferences",
                        PREFERENCES, "preferences")
                .endConstructor();

        // implement SharedPreferences by just wrapping the shared preferences
        wrapSharedPreferencesInterface(Modifier.PUBLIC, editorName, PREFERENCES, SharedPreferences.class.getMethods());

        // creating accessors for the fields annotated
        for (Map.Entry<String, Preference> entry : preferences.entrySet()) {
            entry.getValue().writeGetter(mWriter);
            entry.getValue().writeSetter(mWriter);
        }

        // creating nested inner class for the editor
        mWriter.emitEmptyLine().beginType(editorName, "class", Modifier.PUBLIC_STATIC, null, SharedPreferences.Editor.class.getCanonicalName());
        mWriter.emitEmptyLine()
                .emitField(SharedPreferences.Editor.class.getCanonicalName(), EDITOR, Modifier.PRIVATE_FINAL)
                .emitEmptyLine();
        mWriter.beginConstructor(Modifier.PUBLIC,
                SharedPreferences.Editor.class.getCanonicalName(), PAR_EDITOR)
                .emitStatement("this.%1$s = %2$s", EDITOR, PAR_EDITOR)
                .endConstructor();
        wrapEditorInterface(Modifier.PUBLIC, editorName, EDITOR, SharedPreferences.Editor.class.getMethods());
        // creating accessors for the fields annotated
        for (Map.Entry<String, Preference> entry : preferences.entrySet()) {
            entry.getValue().writeChainSetter(mWriter, editorName, EDITOR);
        }
        mWriter.endType();

        mWriter.endType();
        mWriter.close();
    }

    private void wrapSharedPreferencesInterface(Set<javax.lang.model.element.Modifier> modifiersPublic, String editor, String wrappedElement, Method[] methods) throws IOException {
        for (Method method : methods) {
            mWriter.emitEmptyLine().emitAnnotation(Override.class);
            boolean isCustomWrapperNeeded = method.getReturnType().equals(SharedPreferences.Editor.class);
            final String params = beginMethod(modifiersPublic, editor, method, isCustomWrapperNeeded);

            if (method.getReturnType().equals(void.class))
                mWriter.emitStatement("%1$s.%2$s(%3$s)", wrappedElement, method.getName(), params);
            else {
                if (isCustomWrapperNeeded)
                    mWriter.emitStatement("return new %1$s(%2$s.%3$s(%4$s))", editor, wrappedElement, method.getName(), params);
                else
                    mWriter.emitStatement("return %1$s.%2$s(%3$s)", wrappedElement, method.getName(), params);
            }
            mWriter.endMethod();
        }
    }

    private void wrapEditorInterface(Set<javax.lang.model.element.Modifier> modifiersPublic, String editor, String wrappedElement, Method[] methods) throws IOException {
        for (Method method : methods) {
            mWriter.emitEmptyLine().emitAnnotation(Override.class);
            boolean isCustomWrapperNeeded = method.getReturnType().equals(SharedPreferences.Editor.class);
            final String params = beginMethod(modifiersPublic, editor, method, isCustomWrapperNeeded);

            if (method.getReturnType().equals(boolean.class))
                mWriter.emitStatement("return %1$s.%2$s(%3$s)", wrappedElement, method.getName(), params);
            else {
                mWriter.emitStatement("%1$s.%2$s(%3$s)", wrappedElement, method.getName(), params);
                if (!method.getReturnType().equals(void.class))
                    mWriter.emitStatement("return this");
            }
            mWriter.endMethod();
        }
    }

    private String beginMethod(Set<javax.lang.model.element.Modifier> modifiersPublic, String editor, Method method, boolean isCustomWrapperNeeded) throws IOException {
        String params = "";
        final String retType = isCustomWrapperNeeded ?
                editor : method.getGenericReturnType().getTypeName().replace('$', '.');
        if (method.getParameterCount() > 0) {
            String[] parameters = new String[method.getParameterCount() * 2];
            for (int i = 0; i < method.getParameterCount(); i++) {
                parameters[2 * i] = method.getGenericParameterTypes()[i].getTypeName().replace('$', '.');
                parameters[2 * i + 1] = method.getParameters()[i].getName();
                if (i > 0)
                    params += ", ";
                params += parameters[2 * i + 1];
            }
            mWriter.beginMethod(retType, method.getName(), modifiersPublic, parameters);
        } else {
            mWriter.beginMethod(retType, method.getName(), modifiersPublic);
        }
        return params;
    }


    public String getPackageName() {
        Element enclosingElement = mElement.getEnclosingElement();
        if (enclosingElement != null && enclosingElement instanceof PackageElement) {
            return ((PackageElement) enclosingElement).getQualifiedName().toString();
        }
        return "";
    }

}
