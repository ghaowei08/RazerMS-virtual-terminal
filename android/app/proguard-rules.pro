# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontnote
-dontshrink
-keeppackagenames my.com.softspace.SSMobileMPOSCore.*
-keeppackagenames my.com.softspace.SSMobileAndroidUtilEngine.*
-keeppackagenames my.com.softspace.SSMobileUIComponent.*
-keeppackagenames my.com.softspace.SSMobileUtilEngine.*
-keeppackagenames my.com.softspace.SSMobileServiceEngine.*
-keeppackagenames my.com.softspace.SSMobileHttpEngine.*
-keeppackagenames my.com.softspace.configuration.*
-keeppackagenames my.com.softspace.SSMobileReaderEngine.*
-keeppackagenames my.com.softspace.SSMobileThirdPartyEngine.*
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,*Annotation*,EnclosingMethod
-dontwarn android.**
-dontwarn rx.**
-dontwarn okio.**
-dontwarn com.**
-dontwarn org.**

# ===== KEEP ALL third party package =====

# keep for android support library
-keep class android.**{*;}
-keep interface android.**{*;}

# keep for android google sdk
-keep class com.google.**{*;}
-keep interface com.google.**{*;}

# keep for mozilla checkdigit feature
-keep class org.**{*;}
-keep interface org.**{*;}

# keep for ok http feature
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-keep class okio.**{*;}
-keep interface okio.**{*;}
-keep class okhttp3.**{*;}
-keep interface okhttp3.**{*;}
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase


# ===== Customization for each engines =====

# ----- SSMobileMPOSCore -----
-keep public class !my.com.softspace.SSMobileMPOSCore.**.internal.**, my.com.softspace.SSMobileMPOSCore.** {
    public protected <methods>;
    public <fields>;
}

# Keep DAO & DTO
-keep public class my.com.softspace.SSMobileMPOSCore.service.internal.dao.** {
    <fields>;
    public <methods>;
}
-keep public class my.com.softspace.SSMobileMPOSCore.service.dao.** {
    <fields>;
    public <methods>;
}

# Keep ENUM
-keep enum my.com.softspace.SSMobileMPOSCore.common.SSMobileCoreEnumType$** {*;}

# ----- SSMobileUIComponent -----
-keep public class !my.com.softspace.SSMobileUIComponent.**.internal.**, my.com.softspace.SSMobileUIComponent.** {
    public protected <methods>;
    public <fields>;
}

# ----- SSMobileAndroidUtilEngine -----
-keep public class !my.com.softspace.SSMobileAndroidUtilEngine.**.internal.**, my.com.softspace.SSMobileAndroidUtilEngine.** {
    public protected <methods>;
    public <fields>;
}

# ----- SSMobileUtilEngine -----
-keep public class !my.com.softspace.SSMobileUtilEngine.**.internal.**, my.com.softspace.SSMobileUtilEngine.** {
    public protected <methods>;
    public <fields>;
}

# ----- configuration -----
-keep public class !my.com.softspace.configuration.internal.**, my.com.softspace.configuration.** {
    public protected <methods>;
    public <fields>;
}

# ----- SSMobileHttpEngine -----
-keep public class !my.com.softspace.SSMobileHttpEngine.**.internal.**, my.com.softspace.SSMobileHttpEngine.** {
    public protected <methods>;
    public <fields>;
}

# ----- SSMobileServiceEngine -----
-keep public class !my.com.softspace.SSMobileServiceEngine.**.internal.**, my.com.softspace.SSMobileServiceEngine.** {
    <fields>;
    public <methods>;
}
-keep public class my.com.softspace.SSMobileServiceEngine.dao.** {
    <fields>;
    public <methods>;
}

# ----- SSMobileReaderEngine -----
-keep public class !my.com.softspace.SSMobileReaderEngine.**.internal.**, my.com.softspace.SSMobileReaderEngine.** {
    <fields>;
    public <methods>;
}

# ----- SSMobileThirdPartyEngine -----
-keep public class !my.com.softspace.SSMobileThirdPartyEngine.**.internal.**, my.com.softspace.SSMobileThirdPartyEngine.** {
    <fields>;
    public <methods>;
}

# ===== Others =====

# Remove debugging - Throwable_printStackTrace calls. Remove all invocations of
# Throwable.printStackTrace().
-assumenosideeffects public class java.lang.Throwable {
    public void printStackTrace();
}

# Remove debugging - Thread_dumpStack calls. Remove all invocations of
# Thread.dumpStack().
-assumenosideeffects public class java.lang.Thread {
    public static void dumpStack();
}

# Remove debugging - All logging API calls. Remove all invocations of the
# logging API whose return values are not used.
-assumenosideeffects public class java.util.logging.* {
    <methods>;
}

# Remove debugging - All Log4j API calls. Remove all invocations of the
# Log4j API whose return values are not used.
-assumenosideeffects public class org.apache.log4j.** {
    <methods>;
}

-keep public class sspog.SSPOG{*;}
-keep public class sspog.SCRPInfo{*;}
-keep public interface sspog.SCRPAdapter{*;}
-keep public interface sspog.SSPOGProvider{*;}
-dontwarn com.mastercard.terminalsdk.**
-keep class com.mastercard.terminalsdk.** {*;}
-keep class com.mastercard.terminalsdk.**$** {*;}
-keep interface com.mastercard.terminalsdk.** { *; }
-keep enum com.mastercard.terminalsdk.** { *; }
