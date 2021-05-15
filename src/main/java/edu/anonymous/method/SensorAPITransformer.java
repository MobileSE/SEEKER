package edu.anonymous.method;

import edu.anonymous.utils.ApplicationClassFilter;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.util.Chain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SensorAPITransformer extends SceneTransformer {

    public static Set<String> sensorAPIs = new HashSet<>();

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        Chain<SootClass> sootClasses = Scene.v().getClasses();

        for (Iterator<SootClass> iter = sootClasses.snapshotIterator(); iter.hasNext();)
        {
            SootClass sc = iter.next();

            if(ApplicationClassFilter.isSensorClass(sc.getName())){
                if(sc.getMethods().size() > 0){
                    sc.getMethods().forEach(scMethod->{
                        if(!scMethod.getSignature().contains("<init>")){
                            sensorAPIs.add(scMethod.getSignature());
                        }
                    });
                }
            }

        }
    }
}
