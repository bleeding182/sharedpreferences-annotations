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

package com.github.bleeding182.sharedpreferences;

/**
 * The usable types of the properties in the preferences. Used to set the return type
 * and parameters.
 * <p>
 * {@link #BOOLEAN}</p><p>
 * {@link #FLOAT}</p><p>
 * {@link #INTEGER}</p><p>
 * {@link #LONG}</p><p>
 * {@link #STRING}</p><p>
 * {@link #STRING_SET}</p>
 *
 * @author David Medenjak
 * @version 1.0
 */
public enum PreferenceType {
    /**
     * Standard java boolean.
     *
     * @see java.lang.Boolean
     */
    BOOLEAN,
    /**
     * Standard java floating point number.
     *
     * @see java.lang.Float
     */
    FLOAT,
    /**
     * Standard java number.
     *
     * @see java.lang.Integer
     */
    INTEGER,
    /**
     * Standard java long.
     *
     * @see java.lang.Long
     */
    LONG,
    /**
     * Standard java String.
     *
     * @see java.lang.String
     */
    STRING,
    /**
     * A set of Strings.
     *
     * @see java.lang.String
     * @see java.util.Set
     */
    STRING_SET;

    /**
     * Method to supply the spelling for the type as a return type.
     *
     * @return the type as String usable for method declarations.
     */
    public String getReturnType() {
        switch (this) {
            case BOOLEAN:
                return "boolean";
            case FLOAT:
                return "float";
            case INTEGER:
                return "int";
            case LONG:
                return "long";
            case STRING:
                return "String";
            case STRING_SET:
                return "Set<String>";
        }
        return "Object";
    }

    /**
     * Method to supply the type as a String used for the getter methods. e.g. <em>getString()</em>
     *
     * @return the type as String, CamelCase.
     */
    public String getFullName() {
        switch (this) {
            case BOOLEAN:
                return "Boolean";
            case FLOAT:
                return "Float";
            case INTEGER:
                return "Int";
            case LONG:
                return "Long";
            case STRING:
                return "String";
            case STRING_SET:
                return "StringSet";
        }
        return "Object";
    }
}
