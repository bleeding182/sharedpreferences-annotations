package com.github.bleeding182.sharedpreferences.annotations.processor;

import com.github.bleeding182.sharedpreferences.annotations.processor.writer.Preference;
import com.github.bleeding182.sharedpreferences.annotations.processor.writer.PreferenceClassWriter;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;


/**
 * @author David Medenjak on 14.11.2015.
 */
public class SharedPreferenceManager {
    private HashSet<TypeElement> mPreferences = new HashSet<>();

    public void addPreference(TypeElement e) {
        if (!mPreferences.contains(e)) {
            mPreferences.add(e);
        }
    }


    public void writePreferences(Filer filer, Messager messager) throws IOException {
        for (TypeElement prefType : mPreferences) {
            Preference preference = new Preference(prefType);
            JavaFileObject jfo = filer.createSourceFile(preference.getPackage() + "." + preference.getImplementationName());
            JavaWriter mWriter = new JavaWriter(jfo.openWriter());
            PreferenceClassWriter writer = new PreferenceClassWriter(preference, mWriter);
            writer.write();

            writeModule(filer, preference);

        }
        mPreferences.clear();
    }

    private void writeModule(Filer filer, Preference preference) throws IOException {
        try {
            Class.forName("dagger.Module");
        } catch (ClassNotFoundException e) {
            // no dagger present
            return;
        }

        final String type = preference.getType().getSimpleName() + "Module";
        JavaFileObject jfo = filer.createSourceFile(preference.getPackage() + "." + type);
        JavaWriter writer = new JavaWriter(jfo.openWriter());
        writer.setIndent("    ");


        Set<Modifier> pubModifier = new LinkedHashSet<>();
        pubModifier.add(Modifier.PUBLIC);
        writer.emitPackage(preference.getPackage());

        writer.emitImports("dagger.Module", "dagger.Provides");
        writer.emitEmptyLine();

        writer.emitAnnotation("dagger.Module");
        writer.beginType(type, "class", pubModifier);
        writer.emitEmptyLine();

        writer.emitAnnotation("dagger.Provides");
        writer.beginMethod(preference.getType().getSimpleName().toString(),
                "provide" + preference.getType().getSimpleName(),
                pubModifier, preference.getImplementationName(), "preferences");

        writer.emitStatement("return %s", "preferences");
        writer.endMethod();

        writer.emitEmptyLine();

        writer.endType();
        writer.close();


    }
}
