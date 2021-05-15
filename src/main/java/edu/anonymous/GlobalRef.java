package edu.anonymous;

import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

import java.util.HashMap;

public class GlobalRef
{
	public static InfoflowCFG infoflowCFG;
	public static HashMap<String, String> classSensorTypeMap = new HashMap<>();
}
