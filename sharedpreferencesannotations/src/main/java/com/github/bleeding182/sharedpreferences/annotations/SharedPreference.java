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
 * Annotation to generate a default wrapper class for the annotated interface.
 * Supply a String value
 * to change the name of the genereated class to <i>value</i>Prefs and <i>value</i>Editor.
 * <p/>
 * <p>By not specifying a value <i>DefaultPrefs</i> and <i>DefaultEditor</i> will be generated.</p>
 * <p/>
 * <p>Additionally you may change the class name suffixes by setting {@link #preferencesSuffix()}
 * or {@link #editorSuffix()}.</p>
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SharedPreference {
    /**
     * the prefix for the generated preferences and editor.
     *
     * @return the prefix, the name of the annotated interface by default.
     */
    String value() default "";

    /**
     * the suffix for the preferences.
     *
     * @return the suffix, "Prefs" by default.
     */
    String preferencesSuffix() default "Prefs";

    /**
     * the suffix for the editor.
     *
     * @return the suffix, "Editor" by default.
     */
    String editorSuffix() default "Editor";
}
