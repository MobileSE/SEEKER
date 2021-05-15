package edu.anonymous.method;

import edu.anonymous.utils.ApplicationClassFilter;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SensorFieldTransformer extends SceneTransformer {

    public static Set<String> sensorFields = new HashSet<>();

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        Chain<SootClass> sootClasses = Scene.v().getClasses();

        for (Iterator<SootClass> iter = sootClasses.snapshotIterator(); iter.hasNext();)
        {
            SootClass sc = iter.next();

            if(ApplicationClassFilter.isSensorClass(sc.getName())){
                for(SootField sootField : sc.getFields()){
                    sensorFields.add(sootField.toString());
                }
            }
        }
    }

    public static Set<String> getSensorFields() {
        return sensorFields;
    }
}
