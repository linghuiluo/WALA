package com.ibm.wala.inc;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.impl.ZeroCFABuilderFactory;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJClassLoaderFactory;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXCFABuilder;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.junit.Before;
import org.junit.Test;

public class CallGraphTest {

  AnalysisOptions options;
  IAnalysisCacheView cache;
  ClassHierarchy cha;
  AnalysisScope scope;
  SSAPropagationCallGraphBuilder cgBuilder;

  @Before
  public void pre() {
    try {
      scope = new JavaSourceAnalysisScope();

      // add standard libraries to scope
      String[] stdlibs = WalaProperties.getJ2SEJarFiles();
      for (String stdlib : stdlibs) {
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(stdlib));
      }

      // add source code to scope
      String dir = System.getProperty("user.dir");
      String projectDir1 = dir + "/src/test/resources/CGTestProject1/src";
      ClassLoaderReference classLoader = JavaSourceAnalysisScope.SOURCE;
      scope.addToScope(classLoader, new SourceDirectoryTreeModule(new File(projectDir1)));

      // construct cha
      ClassLoaderFactory factory = new ECJClassLoaderFactory(scope.getExclusions());
      cha = ClassHierarchyFactory.make(scope, factory);

      // compute entry points
      Iterable<? extends Entrypoint> entrypoints
          = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(classLoader, cha);

      options = new AnalysisOptions();
      options.setEntrypoints(entrypoints);
      options.setReflectionOptions(ReflectionOptions.NONE);

      cache = new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
    } catch (ClassHierarchyException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testZeroCFA() {
    try {
      cgBuilder = (SSAPropagationCallGraphBuilder) new ZeroCFABuilderFactory().make(options, cache, cha, scope);
      CallGraph cg = cgBuilder.makeCallGraph(options);
      CallGraphPrinter.print("0CFA", cg, true);
    } catch (IllegalArgumentException | CancelException e) {
      throw new RuntimeException(e);
    }

  }

  @Test
  public void testZeroOneCFA() {
    try {

      cgBuilder = (SSAPropagationCallGraphBuilder) ZeroXCFABuilder.make(Language.JAVA, cha, options, cache, null, null,
          ZeroXInstanceKeys.ALLOCATIONS | ZeroXInstanceKeys.SMUSH_MANY | ZeroXInstanceKeys.SMUSH_PRIMITIVE_HOLDERS
              | ZeroXInstanceKeys.SMUSH_STRINGS | ZeroXInstanceKeys.SMUSH_THROWABLES);
      CallGraph cg = cgBuilder.makeCallGraph(options);
      CallGraphPrinter.print("0-1-CFA", cg, true);

    } catch (IllegalArgumentException | CancelException e) {
      throw new RuntimeException(e);
    }
  }
}
