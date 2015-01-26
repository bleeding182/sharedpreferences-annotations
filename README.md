# Shared Preferences Annotations
This library is used to create a simple wrapper class around SharedPreferences for Android,
supporting custom accessor methods by using an annotation processor.

## Functions
* Easy access to custom preferences.
* Compatibility with other classes / libraries by implementing `SharedPreferences`.
* Chained calls with `edit()` including all custom fields.

## How does it work
Annotate an interface with `@SharedPreference` and it will generate a class to access in
the interface defined properties.

The field name is used for the accessor methods names, the String value of the field is the key of the preference.

    @SharedPreference
    public interface Test {
        final static String USERNAME = "username";
        static String PASSWORD = "password";
        @Type(PreferenceType.BOOLEAN)
        final String shown = "shown";
    
        String MY_COOL_SETTING = "setting";
    }
    
This will generate lots of code,
including javadoc for custom fields and implementations for the interfaces.

*e.g.* MY_COOL_SETTING will generate *getMyCoolSetting*, and *setMyCoolSetting*.  
'_' acts as a seperator, introducing CamelCase in the accessors.

An extract of the generated code (stripped of javaDoc)

    public class TestPrefs
        implements Test, SharedPreferences {
            ...
            
        public String getMyCoolSetting(String defaultValue) {
            return getString("setting", defaultValue);
        }
        
        public void setMyCoolSetting(String value) {
            edit().putString("myCoolSetting", value).apply();
        }
        ...
        public boolean isShown(boolean defaultValue) {
            return getBoolean("shown", defaultValue);
        }
        
        public void setShown(boolean value) {
            edit().putBoolean("shown", value).apply();
        }
            ...
        public static class TestEditor
            implements Editor {
                
                ...
                
            public TestEditor setShown(boolean value) {
                mEditor.putBoolean("shown", value);
                return this;
            }
            
                ...
        }
    }

## Customization
All naming is customizable, there are more preferences and options to use. Just see the Javadoc.

## Include in Project
Add the following lines to your `build.gradle`

    buildscript {
        repositories {
            mavenCentral()
        }
    
        dependencies {
            classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
        }
    }
    apply plugin: 'com.android.application'
    apply plugin: 'com.neenbedankt.android-apt'
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        apt 'com.github.bleeding182.sharedpreferences:processor:1.0.0'
        compile 'com.github.bleeding182.sharedpreferences:annotations:1.0.0'
    }
