# Shared Preferences Annotations
This library is an Annotation Processor for creating a simple wrapper class around SharedPreferences for Android.

## How does it work
By annotating an interface the processor will generate a preference class, implementing the `SharedPreferences` interface.
All String variables defined in the interface will generate setter and getter methods for the properties, as well as chained methods
in the `edit()` call.

    @SharedPreference
    public interface Test {
      String PASSWORD = "password";
      static String USERNAME = "username";
      final static String random = "random";
    }

