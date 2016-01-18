package com.github.bleeding182.sharedpreferences.annotations.processor.writer;


import com.github.bleeding182.sharedpreferences.annotations.processor.SharedPreferencesAnnotationProcessor;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * @author David Medenjak on 1/10/2016.
 */
public class PreferenceClassWriter {

    public static final String PREFERENCES = "mPreferences";
    private Preference mPreference;
    private final JavaWriter mWriter;
    private String mIndent = "    ";

    public PreferenceClassWriter(Preference preference, JavaWriter writer) {
        mPreference = preference;

        mWriter = writer;
    }

    public void write() throws IOException {
        mWriter.setIndent(mIndent);

        emitPackageAndHeader();
        emitImports();

        Set<Modifier> modifier = new LinkedHashSet<>();
        modifier.add(Modifier.PUBLIC);
        String implementedPreference = mWriter.compressType(mPreference.getInterface());


        mWriter.emitAnnotation("javax.annotation.Generated(\"" + SharedPreferencesAnnotationProcessor.class.getCanonicalName() + "\")");
        mWriter.beginType(mPreference.getImplementationName(), "class", modifier, null, implementedPreference);
        mWriter.emitEmptyLine();

        emitConstructor(modifier);


        for (Setting setting : mPreference.getSettings()) {
            ArrayList<ExecutableElement> names = setting.getMethods();

            if (setting.getType() == null) {
                continue;
            }

            String type = mWriter.compressType(setting.getType());
            if (type == null) {
                throw new IllegalStateException("type for " + setting.getMethods().get(0) + " could not be found");
            }
            String typePreferenceName = getSharedPreferenceSettingNameForType(type);

            for (ExecutableElement method : names) {
                mWriter.emitAnnotation(Override.class);
                boolean isGetter = method.getReturnType().getKind() != TypeKind.VOID;

                List<String> parameters = new ArrayList<>();
                for (VariableElement element : method.getParameters()) {
                    parameters.add(element.asType().toString());
                    parameters.add(element.getSimpleName().toString());
                }

                mWriter.beginMethod(isGetter ? type : "void",
                        method.getSimpleName().toString(),
                        modifier, parameters, null);

                if (isGetter) {
                    mWriter.emitStatement("return %s.get%s(\"%s\", %s)", PREFERENCES, typePreferenceName,
                            setting.getName(), parameters.size() > 0 ?
                                    parameters.get(1) : getDefaultValue(method.getReturnType().getKind()));
                } else {
                    mWriter.emitStatement("%s.edit().put%s(\"%s\", %s).apply()", PREFERENCES, typePreferenceName,
                            setting.getName(), parameters.get(1));
                }

                mWriter.endMethod();
                mWriter.emitEmptyLine();
            }
        }

        mWriter.endType();
        mWriter.close();
    }

    private String getDefaultValue(TypeKind kind) {
        switch (kind) {
            case BOOLEAN:
                return "false";
            case INT:
                return "0";
            case FLOAT:
                return "0f";
            case LONG:
                return "0l";
            default:
                return "null";
        }
    }

    private String getSharedPreferenceSettingNameForType(String type) {
        switch (type) {
            case "boolean":
                return "Boolean";
            case "float":
                return "Float";
            case "java.util.Set<java.lang.String>":
            case "java.util.Set<String>":
            case "Set<String>":
            case "Set<java.lang.String>":
                return "StringSet";
            case "int":
                return "Int";
            case "long":
                return "Long";
            default:
                return "String";
        }
    }

    private void emitConstructor(Set<Modifier> modifier) throws IOException {
        final String prefParameter = "preferences";

        Set<Modifier> privateModifier = new LinkedHashSet<>();
        privateModifier.add(Modifier.PRIVATE);
        privateModifier.add(Modifier.FINAL);
        mWriter.emitField("SharedPreferences", PREFERENCES, privateModifier);
        mWriter.emitEmptyLine();

        try {
            // http://stackoverflow.com/a/34597701/1837367
            Class.forName("javax.inject.Inject");
            mWriter.emitAnnotation("Inject");
        } catch (ClassNotFoundException e) {
            // no inject present
        }

        mWriter.beginConstructor(modifier, "SharedPreferences", prefParameter);
        mWriter.emitStatement("%s = %s", PREFERENCES, prefParameter);
        mWriter.endConstructor();
        mWriter.emitEmptyLine();
    }

    private void emitImports() throws IOException {
        mWriter.emitImports("android.content.SharedPreferences");
        mWriter.emitEmptyLine();

        for (Setting setting : mPreference.getSettings()) {
            String type = setting.getType();
            if (type != null && type.equals("java.util.Set<java.lang.String>")) {
                mWriter.emitImports(Set.class);
                mWriter.emitEmptyLine();
                break;
            }
        }

        if (mPreference.getInterface().length() == mWriter.compressType(mPreference.getInterface()).length()
                && !mPreference.getPackage().isEmpty()) {
            mWriter.emitImports((mPreference.getInterface()));
            mWriter.emitEmptyLine();
        }

        mWriter.emitImports("javax.annotation.Generated");
        try {
            Class.forName("javax.inject.Inject");
            mWriter.emitImports("javax.inject.Inject");
            mWriter.emitEmptyLine();
        } catch (ClassNotFoundException e) {
            // no inject present
        }
    }

    private void emitPackageAndHeader() throws IOException {
        String packageName = mPreference.getPackage();
        mWriter.emitPackage(packageName)
                .emitEmptyLine();
    }
}
