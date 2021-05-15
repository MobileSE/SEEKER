package edu.anonymous.utils;

import org.apache.commons.collections4.CollectionUtils;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SootHelper {

    public static List<SootMethod> getAllReachableMethods(SootClass sc) {
        // Get list of reachable methods declared in this class
        List<SootMethod> allMethods = new ArrayList<SootMethod>();
        Iterator methodsIt = sc.methodIterator();
        while (methodsIt.hasNext()) {
            SootMethod method = (SootMethod) methodsIt.next();
            allMethods.add(method);
        }
        // Add reachable methods declared in superclasses
        SootClass superclass = sc;
        if (superclass.hasSuperclass()) {
            superclass = superclass.getSuperclass();
        }
        while (superclass.hasSuperclass())
        {
            Iterator scMethodsIt = superclass.methodIterator();
            while (scMethodsIt.hasNext()) {
                SootMethod scMethod = (SootMethod) scMethodsIt.next();
                allMethods.add(scMethod);
            }
            superclass = superclass.getSuperclass();
        }

        //Add reachable methods declared in outerclasses
        if (sc.hasOuterClass()) {
            SootClass outerClass = sc.getOuterClass();
            Iterator scMethodsIt = outerClass.methodIterator();
            while (scMethodsIt.hasNext()) {
                SootMethod scMethod = (SootMethod) scMethodsIt.next();
                allMethods.add(scMethod);
            }
        }

        return allMethods;
    }

    public static SootMethod getMethod(List<SootMethod> sootMethods, String name, List<Type> parameterTypes){
        if(CollectionUtils.isEmpty(sootMethods)){
            return null;
        }

        for(int i=0; i<sootMethods.size(); i++){
            SootMethod currentMethod = sootMethods.get(i);
            if (name.equals(currentMethod.getName()) && parameterTypes.equals(currentMethod.getParameterTypes())) {
                if (currentMethod != null) {
                    return currentMethod;
                }
            }
        }

        return null;
    }

}
