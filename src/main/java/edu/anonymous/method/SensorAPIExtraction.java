package edu.anonymous.method;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.options.Options;

import java.util.Collections;

public class SensorAPIExtraction {

    public static void main(String[] args){
        String apkPath = args[0];
        String path2AndroidJar = args[1];
        String outputPath = "instrumented_app";

        apply(apkPath, path2AndroidJar, outputPath);

        for(String s : SensorAPITransformer.sensorAPIs){
            System.out.println(s);
        }
    }

    private static void apply(String apkPath, String path2AndroidJar, String outputPath) {
        G.reset();

        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_force_android_jar(path2AndroidJar);
        Options.v().set_process_dir(Collections.singletonList(apkPath));
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_allow_phantom_elms(true);
        Options.v().set_keep_line_number(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_output_dir(outputPath);
        Options.v().set_prepend_classpath(true);
        Options.v().setPhaseOption("cg.cha", "on");
        Options.v().set_force_overwrite(true);
        Options.v().set_full_resolver(true);

        Scene.v().loadNecessaryClasses();
        Scene.v().loadBasicClasses();
        Scene.v().loadDynamicClasses();

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.SensorAPITransformer", new SensorAPITransformer()));

        PackManager.v().runPacks();
        PackManager.v().writeOutput();
    }
}
