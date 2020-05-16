-dontwarn io.popbrain.naked4u
-keepattributes InnerClasses, Deprecate, Exceptions, EnclosingMethod
-optimizationpasses 3
-verbose
-printmapping mapping.txt

-keep public enum io.popbrain.naked4u.LogType { *; }
-keep public class io.popbrain.naked4u.ViewableLogger { *; }
-keep public class io.popbrain.naked4u.BasicViewableLogger { *; }
-keep public class io.popbrain.naked4u.BaseBasicViewableLogger {
    public <init>(...);
    public *** <methods>;
}
-keep public interface io.popbrain.naked4u.NakedLogger { *; }
-keep public interface io.popbrain.naked4u.OnViewableLoggerListener { *; }
-assumenosideeffects class android.util.Log { <methods>; }