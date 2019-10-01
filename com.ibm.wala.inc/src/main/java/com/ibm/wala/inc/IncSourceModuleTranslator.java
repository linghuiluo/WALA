package com.ibm.wala.inc;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceModuleTranslator;
import com.ibm.wala.classLoader.ModuleEntry;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.util.collections.HashMapFactory;

public class IncSourceModuleTranslator extends ECJSourceModuleTranslator{

	public IncSourceModuleTranslator(AnalysisScope scope, IncSourceLoaderImpl sourceLoader) {
		super(scope, sourceLoader);
	}

	@Override
	public void loadAllSources(Set<ModuleEntry> modules) {
		List<String> sources = new LinkedList<>();
	    Map<String, ModuleEntry> sourceMap = HashMapFactory.make();
	    //TODO need to override this to load only the changed source files.
	}
}
