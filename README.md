***This library is currently a work in progress, busy working on v1.0.0***

# Shared Preferences Annotations
This library is an Annotation Processor for creating a simple wrapper class around SharedPreferences for Android.

This project is published under *MIT License*. For more information see `LICENSE`.

## Modules
The  library consists of 2 modules, one containing the Annotations, the other containing the Annotation Processor,
so that unnecessary files don't get compiled with the rest.

## Functions
All of `SharedPreferences` data types are supported and can be used. Dynamic values can still be used, as the generated class also implements the `SharedPreferences` interface.

Chaining it all together `edit().putX(x).putString("custom", "string").putY(y).apply()` works exactly as one would suspect.

## How does it work
By annotating an interface the processor will generate a preference class, implementing the `SharedPreferences` interface.
All String variables defined in the interface will generate setter and getter methods for the properties, as well as chained methods
in the `edit()` call.

    @SharedPreference
    public interface Test {
      String PASSWORD = "password"; // all fields in interfaces are final static
      static String USERNAME = "username"; // this will create accessors for a string preference "username"
      
      @Type(PreferenceType.INTEGER)
      final static String random = "random";
      
      @Type(PreferenceType.BOOLEAN)
      final static String random = "random";
    }

Further annotations for customization options are available to change the data type (defaults to String) and making other settings.  
Non-String Fields will be ignored, but a warning will be issued.

## Include in Project

