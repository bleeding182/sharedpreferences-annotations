package com.github.bleeding182.sharedpreferences.annotations.processor.writer;

import java.util.ArrayList;

import javax.lang.model.element.ExecutableElement;

/**
 * @author David Medenjak on 1/10/2016.
 */
public class Setting {
    private String mType;
    private String mName;
    private ArrayList<ExecutableElement> mMethods = new ArrayList<>();

    public Setting(String preferenceName) {
        mName = preferenceName;
    }

    public ArrayList<ExecutableElement> getMethods() {
        return mMethods;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public void putMethod(ExecutableElement method) {
        mMethods.add(method);
    }

    public Object getName() {
        return mName;
    }
}
