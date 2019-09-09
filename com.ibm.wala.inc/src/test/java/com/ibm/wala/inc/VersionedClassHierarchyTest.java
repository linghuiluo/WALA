package com.ibm.wala.inc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.impl.ZeroCFABuilderFactory;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.propagation.Change;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.CancelCHAConstructionException;
import com.ibm.wala.util.CancelException;

public class VersionedClassHierarchyTest {
	
	@Test
	public void test()
	{
		
		try {
		AnalysisScope scope = new JavaSourceAnalysisScope();
		ClassLoaderFactory factory = null;
		VersionedClassHierarchy vcha= VersionedClassHierarchyFactory.make(scope, factory);
		AnalysisOptions options = new AnalysisOptions();
		Iterable<? extends Entrypoint> entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope.getApplicationLoader(), vcha);
		options.setEntrypoints(entrypoints);
		options.setReflectionOptions(ReflectionOptions.NONE);
		IAnalysisCacheView cache = new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
		SSAPropagationCallGraphBuilder builder = (SSAPropagationCallGraphBuilder) new ZeroCFABuilderFactory().make(options, cache, vcha, scope);
		builder.makeCallGraph(options);
		//
		
		//magpieBridge sends the files which changed 
		
		List<Module> fileModules = null;
		
		//magpieBridge sends the ranges of changes
		List<Position> ranges= null;
		
		
		
		//
		
		
		
		IncClassLoader incLoader = new IncClassLoader();
	
		incLoader.init(fileModules);
		
		List<Change> changes = incLoader.getChanges(ranges);
		
		vcha.addLatestIncrement(incLoader);
		
		builder.adaptIncrements(changes);

		CallGraph cg = builder.getCallGraph();
		
	
		
		} catch (CancelCHAConstructionException | IOException | IllegalArgumentException | CancelException e) {
			throw new RuntimeException(e);
		}
	}
}
