package com.github.bleeding182.sharedpreferences;

/**
 * @author David Medenjak on 1/18/2016.
 */

import com.github.bleeding182.sharedpreferences.annotations.SharedPreference;
import com.github.bleeding182.sharedpreferences.annotations.processor.SharedPreferencesAnnotationProcessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Set;

import javax.annotation.processing.Processor;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static java.lang.String.format;

@RunWith(Parameterized.class)
public class ImplementationTest {

    private final static String SP_FQN = SharedPreference.class.getCanonicalName();
    private Processor mProcessor;

    @Parameterized.Parameters(name = "Generation of {0}")
    public static Class[] params() {
        return new Class[]{
                int.class,
                Integer.class,
                float.class,
                Float.class,
                String.class,
                boolean.class,
                Boolean.class,
                long.class,
                Long.class,
                Set.class
        };
    }

    @Parameterized.Parameter
    public Class clazz;

    @Before
    public void init() {
        mProcessor = new SharedPreferencesAnnotationProcessor();
    }

    @Test
    public void successGeneratedSetterImplementation() {
        System.out.println("Testing " + clazz.getCanonicalName());
        assert_().about(javaSource())
                .that(forSourceLines("Preferences",
                        "package com;",
                        format("@%s interface Preferences {", SP_FQN),
                        format("void setSomeValue(%s value) { mPreferences.putFloat(\"SomeValue\", value); }", clazz.getCanonicalName()),
                        "}"))
                .processedWith(mProcessor)
                .compilesWithoutError()
                .and()
                .generatesSources(forSourceLines("com.SPPreferences",
                        "package com;",
                        "import android.content.SharedPreferences;",
                        "import javax.annotation.Generated;",
                        "@Generated(\"" + SharedPreferencesAnnotationProcessor.class.getCanonicalName() + "\"",
                        "public class SPPreferences implements Preferences {",
                        "private final SharedPreferences mPreferences;",
                        "public SPPreferences(SharedPreferences preferences) { mPreferences = preferences; }",

                        "}"));
    }


    @Test
    public void successGeneratedGetterSetterImplementation() {
        System.out.println("Testing " + clazz.getCanonicalName());
        assert_().about(javaSource())
                .that(forSourceLines("Preferences",
                        "package com;",
                        format("@%s interface Preferences {", SP_FQN),
                        format("%s getSomeValue();", clazz.getCanonicalName()),
                        format("void setSomeValue(%s value);", clazz.getCanonicalName()),
                        "}"))
                .processedWith(mProcessor)
                .compilesWithoutError()
                .and()
                .generatesSources(forSourceLines("com.SPPreferences",
                        "package com;",
                        "import android.content.SharedPreferences;",
                        "import javax.annotation.Generated;",
                        "@Generated(\"" + SharedPreferencesAnnotationProcessor.class.getCanonicalName() + "\"",
                        "public class SPPreferences implements Preferences {",
                        "private final SharedPreferences mPreferences;",
                        "public SPPreferences(SharedPreferences preferences) { mPreferences = preferences; }",
                        format("@Override public %s getSomeValue() { return mPreferences.get%s(\"SomeValue\", %s); }",
                                getReturnType(clazz), getSpMethodName(clazz), getDefaultValueForClass(clazz)),
                        format("@Override public void setSomeValue(%s value) { mPreferences.edit().put%s(\"SomeValue\", value).apply(); }",
                                getReturnType(clazz),
                                getSpMethodName(clazz)),
                        "}"));
    }

    public String getSpMethodName(Class clazz) {
        switch (clazz.getCanonicalName()) {
            case "float":
                return "Float";
            case "int":
                return "Int";
            case "java.lang.String":
                return "String";
            case "boolean":
                return "Boolean";
            case "long":
                return "Long";
            case "java.util.Set.Set<String>":
                return "StringSet";
            default:
                throw new NotImplementedException();
        }
    }

    public String getReturnType(Class clazz) {
        switch (clazz.getCanonicalName()) {
            case "float":
                return "float";
            case "int":
                return "int";
            case "java.lang.String":
                return "String";
            case "boolean":
                return "boolean";
            case "long":
                return "long";
            case "java.util.Set.Set<String>":
                return "Set<String>";
            default:
                throw new NotImplementedException();
        }
    }


    public String getDefaultValueForClass(Class clazz) {
        switch (clazz.getCanonicalName()) {
            case "int":
                return "0";
            case "float":
                return "0f";
            case "boolean":
                return "false";
            default:
                return "null";
        }
    }

}
