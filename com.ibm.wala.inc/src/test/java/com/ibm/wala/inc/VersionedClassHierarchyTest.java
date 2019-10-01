package com.ibm.wala.inc;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.impl.ZeroCFABuilderFactory;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.JavaClass;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJClassLoaderFactory;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
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
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.CancelException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.ServerConfiguration;
import magpiebridge.projectservice.java.JavaProjectService;
import org.junit.Ignore;
import org.junit.Test;

public class VersionedClassHierarchyTest {

	AnalysisScope scope;
	VersionedClassHierarchy vcha;
	ClassLoaderFactory factory;

	@Test
	public void testIncLoading() {
		initialLoad();
		incLoad1();
		incLoad2();
	}
	
	public void initialLoad() {
		try {
			File projectSourcePath = new File(System.getProperty("user.dir") + "/testdata/CGTestProject1/src");
			scope = new IncJavaSourceAnalysisScope();

			// add standard libraries to scope
			String[] stdlibs = WalaProperties.getJ2SEJarFiles();
			for (String stdlib : stdlibs) {
				scope.addToScope(ClassLoaderReference.Primordial, new JarFile(stdlib));
			}

			// add source code path to scope
			scope.addToScope(IncJavaSourceAnalysisScope.SOURCE, new SourceDirectoryTreeModule(projectSourcePath));

			factory = new IncSourceLoaderFactory(scope.getExclusions());
			vcha = VersionedClassHierarchyFactory.make(null, scope, factory);
			System.out.println("VCHA Version: " + vcha.latestVersion);
			Iterator<IClass> it = vcha.getLoader(IncJavaSourceAnalysisScope.SOURCE).iterateAllClasses();
			while (it.hasNext()) {
				IClass c = it.next();
				if (c instanceof JavaClass)
					System.out.println(c.getName());
			}

		} catch (IllegalArgumentException | IOException e) {
			throw new RuntimeException(e);
		}
	}


	public void incLoad1() {
		File fileA = new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/A.java");
		File fileC = new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/C.java");
		File fileMain = new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/Main.java");
		scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileA, "A");
		scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileC, "C");
		scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileMain, "Main");
		factory = new IncSourceLoaderFactory(scope.getExclusions());
		vcha = VersionedClassHierarchyFactory.make(vcha, scope, factory);
		System.out.println("VCHA Version: " + vcha.getVersion());
		
		IClassLoader loader = vcha.getLoader(IncJavaSourceAnalysisScope.INCREMENTAL);
		Iterator<IClass> it = loader.iterateAllClasses();
		while (it.hasNext()) {
			IClass c = it.next();
			if (c instanceof JavaClass)
				System.out.println(c.getName());
		}

		IClass c = loader.lookupClass(TypeName.findOrCreate("LA"));
		if (c instanceof JavaClass)
			System.out.println(c.getName());
	}

	public void incLoad2()
	{
		File fileA = new File(System.getProperty("user.dir") + "/testdata/CGTestProject3/src/A.java");
		File fileMain = new File(System.getProperty("user.dir") + "/testdata/CGTestProject3/src/Main.java");
		scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileA, "A");
		scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileMain, "Main");
		factory = new IncSourceLoaderFactory(scope.getExclusions());
		vcha = VersionedClassHierarchyFactory.make(vcha, scope, factory);
		System.out.println("VCHA Version: " + vcha.getVersion());
		
		IClassLoader loader = vcha.getLoader(IncJavaSourceAnalysisScope.INCREMENTAL);
		Iterator<IClass> it = loader.iterateAllClasses();
		while (it.hasNext()) {
			IClass c = it.next();
			if (c instanceof JavaClass)
				System.out.println(c.getName());
		}

		IClass c = loader.lookupClass(TypeName.findOrCreate("LA"));
		if (c instanceof JavaClass)
			System.out.println(c.getName());
	}
	
}
