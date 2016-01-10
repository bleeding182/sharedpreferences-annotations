package com.github.bleeding182.sharedpreferences.annotations.processor;

import com.github.bleeding182.sharedpreferences.annotations.processor.writer.Preference;
import com.github.bleeding182.sharedpreferences.annotations.processor.writer.PreferenceClassWriter;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.HashSet;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
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
        }
    }
}
