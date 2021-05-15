package edu.anonymous;

import edu.anonymous.method.SensorType;
import edu.anonymous.utils.SootHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.xmlpull.v1.XmlPullParserException;
import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.results.DataFlowResult;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JimpleLocal;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, XmlPullParserException {
        String apkPath = args[0];
        String forceAndroidJar = args[1];

        long startTime = System.currentTimeMillis();
        System.out.println("==>START TIME:" + startTime);

        initICFG(apkPath, forceAndroidJar);

        long afterIcfgTime = System.currentTimeMillis();
        System.out.println("==>AFTER ICFG INIT TIME:" + afterIcfgTime);

        //data flow analysis
        String destinationPath = "res/SourcesAndSinks.txt";
        InfoflowResults infoflowResults = dataFlowAnalysis(apkPath, forceAndroidJar, destinationPath);

        long finalTime = System.currentTimeMillis();
        System.out.println("==>FINAL TIME:" + finalTime);
    }

    private static void initICFG(String apkPath, String forceAndroidJar) {
        SetupApplication analyser = new SetupApplication(forceAndroidJar, apkPath);
        analyser.getConfig().setMergeDexFiles(true);
        Scene.v().loadNecessaryClasses();
        analyser.constructCallgraph();
        GlobalRef.infoflowCFG = new InfoflowCFG();
    }

    public static InfoflowResults dataFlowAnalysis(String apkPath, String forceAndroidJar, String destinationPath) throws IOException, XmlPullParserException {
        InfoflowResults infoflowResults = analyzeAPKFile(apkPath, destinationPath, false);

        if (CollectionUtils.isNotEmpty(infoflowResults.getResultSet())) {
            infoflowResults.getResultSet().forEach(dataLeak -> {
                System.out.println("------------------------------Data Leaks----------------------------------");
                System.out.println("[SOURCE]" + dataLeak.getSource());
                SootMethod sensorDeclareMethod = GlobalRef.infoflowCFG.getMethodOf(dataLeak.getSource().getStmt());
                if (null != sensorDeclareMethod && sensorDeclareMethod.getSignature().contains("onSensorChanged")) {
                    System.out.println("[Method of SOURCE]" + sensorDeclareMethod.getSignature());

                    List<String> sensorTypes = getSensorType(dataLeak, sensorDeclareMethod);

                    System.out.println("[Sensor Type]"+sensorTypes);
                }
                System.out.println("[SINK]" + dataLeak.getSink());
            });
        }
        return infoflowResults;
    }

    private static List<String> getSensorType(DataFlowResult dataLeak, SootMethod sensorDeclareMethod) {
        if(!sensorDeclareMethod.hasActiveBody()){
            return new ArrayList<>();
        }

        /**
         * preprocessing:store units and their index into allUnits
         */
        int unitNum = 0;
        HashMap<Unit, Integer> allUnits = new HashMap<>();
        for (Unit unit : sensorDeclareMethod.getActiveBody().getUnits()) {
            allUnits.put(unit, unitNum);
            unitNum++;
        }

        /**
         * check how sensorTypeObject is used in apk
         */
        boolean switchFlag = false;
        List<Integer> switchStmts = new ArrayList<>();
        Integer targetStmtInteger = allUnits.get(dataLeak.getSource().getStmt());
        HashMap<Integer, Integer> switchValusUnitMap = new HashMap<>();
        JimpleLocal sensorTypeObject = null;
        for (Unit unit : sensorDeclareMethod.getActiveBody().getUnits()) {
            if (unit instanceof JAssignStmt && ((JAssignStmt) unit).getRightOp().toString().contains("<android.hardware.Sensor: int getType()>()")) {
                sensorTypeObject = (JimpleLocal) ((JAssignStmt) unit).getLeftOpBox().getValue();
            }

            //if sensorType is been used in SwitchStmt
            if (null != sensorTypeObject && unit instanceof JLookupSwitchStmt && ((JLookupSwitchStmt) unit).getKeyBox().getValue().equivTo(sensorTypeObject)) {
                switchFlag = true;
                for (int lookUpIndex = 0; lookUpIndex < ((JLookupSwitchStmt) unit).getLookupValues().size(); lookUpIndex++) {
                    switchValusUnitMap.put(((JLookupSwitchStmt) unit).getLookupValue(lookUpIndex), allUnits.get(((JLookupSwitchStmt) unit).getTargetBox(lookUpIndex).getUnit()));
                    switchStmts.add(allUnits.get(((JLookupSwitchStmt) unit).getTargetBox(lookUpIndex).getUnit()));
                }
            }
        }

        /**
         * If sensorTypeObject == null, there must has only one sensor type.
         * So we extract sensor type from onCreate() Method.
         */
        List<String> sensorTypes = new ArrayList<>();
        if(sensorTypeObject == null){
            List<SootMethod> sensorDeclareMethods = SootHelper.getAllReachableMethods(sensorDeclareMethod.getDeclaringClass());
            //List<SootMethod> sensorDeclareMethods = sensorDeclareMethod.getDeclaringClass().getMethods();

            boolean findFlag = false;
            if(CollectionUtils.isNotEmpty(sensorDeclareMethods)){
                for(SootMethod sootMethod : sensorDeclareMethods){
                    //if(sootMethod.getName().equals("onCreate") && sootMethod.hasActiveBody()){
                    if(sootMethod.hasActiveBody()){
                        for(Unit unit : sootMethod.getActiveBody().getUnits()){
                            if(unit.toString().contains("<android.hardware.SensorManager: android.hardware.Sensor getDefaultSensor(int)>")){

                                int sensorNo = Integer.valueOf(unit.toString().substring(unit.toString().lastIndexOf(">") + 1).replaceAll("\\(","").replaceAll("\\)", "").replace(";",""));
                                sensorTypes.add(SensorType.getSensorType(sensorNo).getSensorName());
                                findFlag = true;
                                return sensorTypes;
                            }
                        }
                    }
                }
            }

            if(!findFlag){
                if(CollectionUtils.isNotEmpty(sensorDeclareMethods)){
                    for(SootMethod sootMethod : sensorDeclareMethods){
                        if(sootMethod.hasActiveBody()){
                            for(Unit unit : sootMethod.getActiveBody().getUnits()){
                                if(unit.toString().contains("getSensorList")){

                                    int sensorNo = Integer.valueOf(unit.toString().substring(unit.toString().lastIndexOf(">") + 1).replaceAll("\\(","").replaceAll("\\)", "").replace(";",""));
                                    sensorTypes.add(SensorType.getSensorType(sensorNo).getSensorName());
                                    findFlag = true;
                                    return sensorTypes;
                                }
                            }
                        }
                    }
                }
            }

            if(!findFlag) {
                if (CollectionUtils.isNotEmpty(Scene.v().getApplicationClasses())) {
                    for(SootClass sc : Scene.v().getApplicationClasses()){
                        if(sc.getMethodCount() > 0){
                            for (SootMethod sootMethod : sc.getMethods()) {
                                //if(sootMethod.getName().equals("onCreate") && sootMethod.hasActiveBody()){
                                if (sootMethod.hasActiveBody()) {
                                    for (Unit unit : sootMethod.getActiveBody().getUnits()) {
                                        if (unit.toString().contains("<android.hardware.SensorManager: android.hardware.Sensor getDefaultSensor(int)>")) {

                                            int sensorNo = Integer.valueOf(unit.toString().substring(unit.toString().lastIndexOf(">") + 1).replaceAll("\\(", "").replaceAll("\\)", "").replace(";", ""));
                                            sensorTypes.add(SensorType.getSensorType(sensorNo).getSensorName());
                                            findFlag = true;
                                            return sensorTypes;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(!findFlag){
                System.out.println("Need Check:"+sensorDeclareMethod.getSignature());
            }
        }

        /**
         * Process sensorType-switch
         */
        if(switchFlag){
            Collections.sort(switchStmts);
            Integer switchStmtNo = 0;
            boolean flagSwitchStmtNo = false;
            for (int i = 0; i < switchStmts.size(); i++) {
                if (targetStmtInteger == switchStmts.get(i)) {
                    switchStmtNo = switchStmts.get(i);
                    flagSwitchStmtNo = true;
                    break;
                }
                if (targetStmtInteger < switchStmts.get(i)) {
                    switchStmtNo = switchStmts.get(i - 1);
                    flagSwitchStmtNo = true;
                    break;
                }
            }
            if(!flagSwitchStmtNo){
                switchStmtNo = switchStmts.get(switchStmts.size() - 1);
            }

            //resolve switchStmtNo's corresponding sensor type
            for (Map.Entry<Integer, Integer> entry : switchValusUnitMap.entrySet()) {
                if (switchStmtNo.equals(entry.getValue())) {
                    sensorTypes.add(SensorType.getSensorType(entry.getKey()).getSensorName());
                }
            }
            return sensorTypes;
        }else{
            /**
             *
             * Process sensorType-[IF-Else]
             * example:
             * if(var1.sensor.getType() == 19){
             *  xxx;
             * }
             */
            List<Integer> ifStmts = new ArrayList<>();
            HashMap<Integer, Integer> ifValusUnitMap = new HashMap<>();
            for (Unit unit : sensorDeclareMethod.getActiveBody().getUnits()) {
                if(unit instanceof JIfStmt && ((JIfStmt) unit).getCondition().getUseBoxes().size() > 0 && ((JIfStmt) unit).getCondition().toString().contains("!=")){
                    boolean sensorTypeIfStmt = false;
                    boolean sensorTypeNonZero = false;
                    for(ValueBox vb : ((JIfStmt) unit).getCondition().getUseBoxes()){
                        if(vb.getValue().equivTo(sensorTypeObject)){
                            sensorTypeIfStmt = true;
                        }
                        if(vb.getValue() instanceof IntConstant && ((IntConstant) vb.getValue()).value != 0){
                            sensorTypeNonZero = true;
                        }
                    }

                    if(sensorTypeIfStmt && sensorTypeNonZero){
                        Integer ifConditionNumber = 0;
                        for(ValueBox vb : ((JIfStmt) unit).getCondition().getUseBoxes()){
                            if(vb.getValue() instanceof IntConstant){
                                ifConditionNumber = ((IntConstant) vb.getValue()).value;
                            }
                        }
                        ifValusUnitMap.put(allUnits.get(unit), ifConditionNumber);
                        ifStmts.add(allUnits.get(unit));
                    }
                }
            }

            Collections.sort(ifStmts);
            Integer ifStmtNo = 0;
            boolean flagIfStmtNo = false;
            for (int i = 0; i < ifStmts.size(); i++) {
                if (targetStmtInteger == ifStmts.get(i)) {
                    ifStmtNo = ifStmts.get(i);
                    flagIfStmtNo = true;
                    break;
                }
                if (targetStmtInteger < ifStmts.get(i)) {
                    ifStmtNo = ifStmts.get(i - 1);
                    flagIfStmtNo = true;
                    break;
                }
            }
            if(!flagIfStmtNo){
                ifStmtNo = ifStmts.get(ifStmts.size() - 1);
            }

            sensorTypes.add(SensorType.getSensorType(ifValusUnitMap.get(ifStmtNo)).getSensorName());

            return sensorTypes;
        }
    }

    public static InfoflowResults analyzeAPKFile(String fileName, String destinationPath, final boolean enableImplicitFlows)
            throws IOException, XmlPullParserException {
        return analyzeAPKFile(fileName, destinationPath, null, new AnalysisConfigurationCallback() {

            @Override
            public void configureAnalyzer(InfoflowAndroidConfiguration config) {
                config.setImplicitFlowMode(
                        enableImplicitFlows ? InfoflowConfiguration.ImplicitFlowMode.AllImplicitFlows : InfoflowConfiguration.ImplicitFlowMode.NoImplicitFlows);
            }

        });
    }

    /**
     * Interface that allows test cases to configure the analyzer for DroidBench
     *
     * @author Steven Arzt
     */
    public interface AnalysisConfigurationCallback {

        /**
         * Method that is called to give the test case the chance to change the analyzer
         * configuration
         *
         * @param config The configuration object used by the analyzer
         */
        public void configureAnalyzer(InfoflowAndroidConfiguration config);

    }

    public static InfoflowResults analyzeAPKFile(String fileName, String destinationPath, String iccModel,
                                                 AnalysisConfigurationCallback configCallback) throws IOException, XmlPullParserException {
        String androidJars = System.getenv("ANDROID_JARS");
        if (androidJars == null)
            androidJars = System.getProperty("ANDROID_JARS");
        if (androidJars == null)
            throw new RuntimeException("Android JAR dir not set");
        System.out.println("Loading Android.jar files from " + androidJars);

        SetupApplication setupApplication = new SetupApplication(androidJars,
                fileName);

        // Find the taint wrapper file
        File taintWrapperFile = new File("EasyTaintWrapperSource.txt");
        if (!taintWrapperFile.exists())
            taintWrapperFile = new File("res/EasyTaintWrapperSource.txt");

        // Make sure to apply the settings before we calculate entry points
        if (configCallback != null)
            configCallback.configureAnalyzer(setupApplication.getConfig());

        setupApplication.setTaintWrapper(new EasyTaintWrapper(taintWrapperFile));
        setupApplication.getConfig().setEnableArraySizeTainting(true);

        if (iccModel != null && iccModel.length() > 0) {
            setupApplication.getConfig().getIccConfig().setIccModel(iccModel);
        }

        setupApplication.getConfig().getPathConfiguration().setPathReconstructionMode(InfoflowConfiguration.PathReconstructionMode.Precise);
//        setupApplication.getConfig().getPathConfiguration().setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextInsensitiveSourceFinder);
//        setupApplication.getConfig().setStaticFieldTrackingMode(InfoflowConfiguration.StaticFieldTrackingMode.None);
//        setupApplication.getConfig().setAliasingAlgorithm(InfoflowConfiguration.AliasingAlgorithm.None);

        //set multi-dex
        setupApplication.getConfig().setMergeDexFiles(true);

        setupApplication.getConfig().setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
        setupApplication.getConfig().setSootIntegrationMode(InfoflowConfiguration.SootIntegrationMode.UseExistingInstance);

        return setupApplication.runInfoflow(destinationPath);
    }

}
