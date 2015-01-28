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

package com.github.bleeding182.sharedpreferences.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation to generate a default wrapper class for the annotated interface.
 * Supply a String value
 * to change the name of the genereated class to <i>value</i>Prefs and <i>value</i>Editor.
 * </p>
 * <p>By not specifying a value <i>DefaultPrefs</i> and <i>DefaultEditor</i> will be generated.</p>
 * <p>Additionally you may change the class name suffixes by setting {@link #preferencesSuffix()}
 * or {@link #editorSuffix()}.</p>
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SharedPreference {
    /**
     * Constant used by the annotation processor to identify empty fields.
     */
    final public static String EMPTY = "*empty*";

    /**
     * <p>Defines the name of the generated wrapper Class.
     * It is the prefix with {@link #preferencesSuffix()} and {@link #editorSuffix()} appended accordingly.</p>
     * <p>If no value is set it defaults to the interfaces name.</p>
     * <p>For easy access it can be set by {@code @SharedPreference("Custom")} which will generate
     * {@code CustomPrefs} and {@code CustomEditor} </p>
     *
     * @return the prefix of the generated classe. By default the name of the annotated interface.
     */
    String value() default EMPTY;

    /**
     * The suffix for the generated preferences class.
     *
     * @return the suffix, {@code "Prefs"} by default.
     */
    String preferencesSuffix() default "Prefs";

    /**
     * The suffix for the generated editor inner class.
     *
     * @return the suffix, {@code "Editor"} by default.
     */
    String editorSuffix() default "Editor";

    /**
     * Set this property to move the generated class to another package.
     *
     * @return the packagename of the generated class. By default the package of the annotated interface will be used.
     */
    String packagename() default EMPTY;
}
