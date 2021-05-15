package edu.anonymous.method;

import soot.G;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;

import java.util.HashSet;
import java.util.Set;

public class SensorFieldExtraction {

    protected String appPath;
    protected String androidJarPath;
    public Set<String> sensorFields = new HashSet<>();

    public SensorFieldExtraction(String appPath, String androidJarPath){
        this.appPath = appPath;
        this.androidJarPath = androidJarPath;
    }

    public void run()
    {
        String[] args =
                {
                        "-process-dir", this.appPath,
                        "-android-jars", this.androidJarPath,
                        "-ire",
                        "-pp",
                        "-allow-phantom-refs",
                        "-w",
                        "-p", "cg", "enabled:false",
                };

        G.reset();

        SensorFieldTransformer transformer = new SensorFieldTransformer();

        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_output_format(Options.output_format_none);
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.SensorFieldTransformer", transformer));

        soot.Main.main(args);

        this.sensorFields.addAll(transformer.getSensorFields());
    }
}
