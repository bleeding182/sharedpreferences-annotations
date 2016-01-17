package com.github.bleeding182.sharedpreferences;

import com.github.bleeding182.sharedpreferences.annotations.SharedPreference;
import com.github.bleeding182.sharedpreferences.annotations.processor.SharedPreferencesAnnotationProcessor;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.processing.Processor;
import javax.tools.StandardLocation;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

;

/**
 * @author David Medenjak on 1/17/2016.
 */
@RunWith(JUnit4.class)
public class IntegrationTest {

    private final static String SP_FQN = SharedPreference.class.getCanonicalName();
    private Processor mProcessor;

    @Before
    public void init() {
        mProcessor = new SharedPreferencesAnnotationProcessor();
    }

    @Test
    public void annotatedClass_failsExpectedInterface() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forSourceString("Preferences",
                        String.format("@%s class Preferences {}", SP_FQN)))
                .processedWith(mProcessor)
                .failsToCompile()
                .withErrorCount(1)
                .withErrorContaining("Only interfaces annotated with @SharedPreference are supported.");
    }

    @Test
    @Ignore(value = "Default package lookup not supported yet")
    public void annotatedInterfaceDefaultPackage_successGeneratedSource() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forSourceString("Preferences",
                        String.format("@%s interface Preferences {}", SP_FQN)))
                .processedWith(mProcessor)
                .compilesWithoutError()
                .and()
                .generatesFileNamed(StandardLocation.SOURCE_OUTPUT, "", "SPPreferences.java");
    }

    @Test
    public void annotatedInterfaceWithPackage_successGeneratedSource() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forSourceString("Preferences",
                        String.format("package com; @%s interface Preferences {}", SP_FQN)))
                .processedWith(mProcessor)
                .compilesWithoutError()
                .and()
                .generatesFileNamed(StandardLocation.SOURCE_OUTPUT, "com", "SPPreferences.java");
    }

    @Test
    public void successGeneratedSource() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forSourceString("Preferences",
                        String.format("package com; @%s interface Preferences {}", SP_FQN)))
                .processedWith(mProcessor)
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forSourceLines("com.SPPreferences",
                        "package com;",
                        "import android.content.SharedPreferences;",
                        "public class SPPreferences implements Preferences {",
                        "private final SharedPreferences mPreferences;",
                        "public SPPreferences(SharedPreferences preferences) { mPreferences = preferences; }",
                        "}"));
    }

}
