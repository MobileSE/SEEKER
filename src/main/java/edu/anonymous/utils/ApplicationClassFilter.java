package edu.anonymous.utils;

import org.apache.commons.lang3.StringUtils;
import soot.SootClass;
import soot.jimple.InvokeExpr;

import java.util.List;

public class ApplicationClassFilter {

    private static List<String> classNames;

    /**
     * @param sootClass
     * @return
     */
    public static boolean isApplicationClass(SootClass sootClass) {
        return isApplicationClass(sootClass.getPackageName());
    }

    /**
     * @return
     */
    public static boolean isApplicationClass(String clsName) {
        if (StringUtils.isBlank(clsName)) {
            return false;
        }
        if (clsName.startsWith("com.google.")
                || clsName.startsWith("soot.")
                || clsName.startsWith("android.")
                || clsName.startsWith("java.")
                || clsName.startsWith("com.facebook.")
                || clsName.startsWith("org.apache.")
        ) {
            return false;
        }
        return true;
    }

    public static boolean containsClassInSystemPackage(String className) {
        return className.contains("android.") || className.contains("java.") || className.contains("javax.")
                || className.contains("sun.") || className.contains("org.omg.")
                || className.contains("org.w3c.dom.") || className.contains("com.google.")
                || className.contains("com.android.") || className.contains("org.apache.")
                || className.contains("soot.")
                || className.contains("androidx.");
    }

    public static boolean isClassInSystemPackage(String className) {
        return className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.")
                || className.startsWith("sun.") || className.startsWith("org.omg.")
                || className.startsWith("org.w3c.dom.") || className.startsWith("com.google.")
                || className.startsWith("com.android.") || className.startsWith("org.apache.")
                || className.startsWith("soot.")
                || className.startsWith("androidx.");
    }

    public static boolean isClassSystemPackage(String className) {
        return className.startsWith("<android.") || className.startsWith("<java.") || className.startsWith("<javax.")
                || className.startsWith("<sun.") || className.startsWith("<org.omg.")
                || className.startsWith("<org.w3c.dom.") || className.startsWith("<com.google.")
                || className.startsWith("<com.android.") || className.startsWith("<org.apache.")
                || className.startsWith("<soot.")
                || className.startsWith("<androidx.");
    }

    public static boolean isAndroidSystemPackage(String className) {
        return className.startsWith("android.")
                || className.startsWith("com.android.")
                || className.startsWith("androidx.")
                || className.startsWith("org.easymock");
    }

    public static boolean isAndroidSystemAPI(String className) {
        return className.startsWith("<android.")
                || className.startsWith("<com.android.")
                || className.startsWith("<androidx.");
    }

    public static boolean isAndroidLifeCycleMethod(String methodName) {
        return methodName.contains("onCreate")
                || methodName.contains("onStart")
                || methodName.contains("onResume")
                || methodName.contains("onPause")
                || methodName.contains("onStop")
                || methodName.contains("onDestroy")
                || methodName.contains("<init>")
                || methodName.contains("finish()")
                ;
    }

    public static boolean isAndroidUIMethod(String unitString) {
        return unitString.startsWith("<android.widget")
                || unitString.startsWith("<android.view")
                || unitString.startsWith("<android.webkit")
                || unitString.startsWith("<android.content.res.Resources")
                || unitString.startsWith("<android.app.Dialog")
                || unitString.startsWith("<android.app.AlertDialog")
                ;
    }

    public static boolean isAndroidSystemAPI(InvokeExpr invokeExpr) {
        try {
            return invokeExpr.getMethod().getDeclaringClass().hasSuperclass()
                    && !invokeExpr.getMethod().getDeclaringClass().getSuperclassUnsafe().getName().equals("java.lang.Object")
                    && (invokeExpr.getMethod().getDeclaringClass().getMethodByName(invokeExpr.getMethod().getName()) != null);
        } catch (RuntimeException e) {
            return true;
        }
    }

    public static boolean isJavaBasicType(String className) {
        return className.startsWith("java.lang.String")
                || className.startsWith("java.lang.Boolean")
                || className.startsWith("java.lang.Byte")
                || className.startsWith("java.lang.Character")
                || className.startsWith("java.lang.Double")
                || className.startsWith("java.lang.Float")
                || className.startsWith("java.lang.Integer")
                || className.startsWith("java.lang.Long")
                || className.startsWith("java.lang.Short")
                ;
    }

    public static boolean isSensorClass(String className) {
        return
                className.equals("android.hardware.Sensor")
                || className.equals("android.hardware.HardwareBuffer")
                || className.equals("android.hardware.SensorAdditionalInfo")
                || className.equals("android.hardware.SensorDirectChannel")
                || className.equals("android.hardware.SensorEvent")
                || className.equals("android.hardware.SensorManager")
                || className.equals("android.hardware.TriggerEvent")
                || className.equals("android.hardware.GeomagneticField")
                || className.equals("android.hardware.ConsumerIrManager.CarrierFrequencyRange")
                || className.equals("android.hardware.ConsumerIrManager")
                || className.equals("android.hardware.Camera")
                || className.equals("android.hardware.Camera.Area")
                || className.equals("android.hardware.Camera.CameraInfo")
                || className.equals("android.hardware.Camera.Face")
                || className.equals("android.hardware.Camera.Parameters")
                || className.equals("android.hardware.Camera.Size")

                ;
    }

}