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

import com.github.bleeding182.sharedpreferences.annotations.SharedPreference;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;


/**
 * This class defines the annotation processor called by the apt tool.
 * It will verify that it is in fact an interface, then it will call the Helper class.
 */
@SupportedAnnotationTypes("com.github.bleeding182.sharedpreferences.annotations.SharedPreference")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SharedPreferencesAnnotationProcessor extends AbstractProcessor {

    /**
     * Process method called for every item annotated by {@link com.github.bleeding182.sharedpreferences.annotations.SharedPreference}.
     *
     * @param annotations the annotation types requested to be processed
     * @param roundEnv    - environment for information about the current and prior round
     * @return whether or not the set of annotation types are claimed by this processor
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(SharedPreference.class)) {
            if (e.getKind().isField() || e.getKind().isClass()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Just interfaces annotated by @SharedPreference are supported.", e);
                continue;
            }
            PreferenceHolder prefHolder;
            try {
                prefHolder = new PreferenceHolder((TypeElement) e, processingEnv.getFiler(), processingEnv.getMessager());
                prefHolder.write();
            } catch (IOException ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage(), e);
            }
        }
        return true;
    }
}