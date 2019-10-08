package com.ibm.wala.inc;

import java.util.Set;

import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceModuleTranslator;
import com.ibm.wala.classLoader.ModuleEntry;
import com.ibm.wala.ipa.callgraph.AnalysisScope;

public class IncSourceModuleTranslator extends ECJSourceModuleTranslator {

	public IncSourceModuleTranslator(AnalysisScope scope, IncSourceLoaderImpl sourceLoader) {
		super(scope, sourceLoader);
	}

	@Override
	public void loadAllSources(Set<ModuleEntry> modules) {
		super.loadAllSources(modules);
	}
}
