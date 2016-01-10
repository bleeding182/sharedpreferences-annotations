package com.github.bleeding182.sharedpreferences.annotations.processor.writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

/**
 * @author David Medenjak on 1/10/2016.
 */
public class Preference {
    private String mPackage;
    private HashMap<String, Setting> mSettings;
    private TypeElement mPrefType;

    public Preference(TypeElement prefType) {
        mPrefType = prefType;
    }

    public String getPackage() {
        Element element = mPrefType.getEnclosingElement();

        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return ((PackageElement) element).getQualifiedName().toString();
    }

    public void setPackage(String aPackage) {
        mPackage = aPackage;
    }

    public Collection<Setting> getSettings() {
        if (mSettings == null) {
            mSettings = new LinkedHashMap<>();

            for (Element element : mPrefType.getEnclosedElements()) {
                if (element.getKind() != ElementKind.METHOD) {
                    continue;
                }
                ExecutableElement method = (ExecutableElement) element;
                String methodName = method.getSimpleName().toString();
                final String preferenceName;
                if (methodName.startsWith("get") || methodName.startsWith("set") || methodName.startsWith("has")) {
                    if (Character.isUpperCase(methodName.charAt(3))) {
                        // remove prefix
                        preferenceName = methodName.substring(3);
                    } else {
                        throw new IllegalArgumentException("Preference name for " + methodName + " not deductible");
                    }
                } else if (methodName.startsWith("is")) {
                    if (Character.isAlphabetic(methodName.charAt(2))) {
                        preferenceName = methodName.substring(2);
                    } else {
                        throw new IllegalArgumentException("Preference name for " + methodName + " not deductible");
                    }
                } else {
                    throw new IllegalArgumentException("Preference name for " + methodName + " not deductible");
                }

                if (!mSettings.containsKey(preferenceName)) {
                    mSettings.put(preferenceName, new Setting(preferenceName));
                }
                Setting setting = mSettings.get(preferenceName);
                setting.putMethod(method);
                if(method.getReturnType().getKind() != TypeKind.VOID) {
                    setting.setType(method.getReturnType().toString());
                }
            }
        }

        return mSettings.values();
    }

    public String getInterface() {
        return mPrefType.getQualifiedName().toString();
    }

    public String getImplementationName() {
        return "SP" + mPrefType.getSimpleName();
    }

    public TypeElement getType() {
        return mPrefType;
    }


    /**
     * Whether the constructor should be annotated with <em>@Inject</em>
     *
     * @return true if the annotation should be applied
     */
    public boolean doAnnotateConstructorInject() {
        return true;
    }
}
