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

import com.github.bleeding182.sharedpreferences.annotations.SharedPreference;


@SupportedAnnotationTypes("com.github.bleeding182.sharedpreferences.annotations.SharedPreference")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SharedPreferencesAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(SharedPreference.class)) {
            if (!e.getKind().isInterface()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Just annotated interfaces are supported.", e);
                continue;
            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + e.getSimpleName());
            PreferenceHolder prefHolder = null;
            try {
                prefHolder = new PreferenceHolder((TypeElement) e, processingEnv.getFiler(), processingEnv.getMessager());
                prefHolder.write();
            } catch (IOException ex) {
            }
        }
        return true;
    }
}