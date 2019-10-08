package com.ibm.wala.inc;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.impl.ZeroCFABuilderFactory;
import com.ibm.wala.cast.java.client.impl.ZeroOneCFABuilderFactory;
import com.ibm.wala.cast.java.client.impl.ZeroOneContainerCFABuilderFactory;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJClassLoaderFactory;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import org.junit.Test;

public class CallGraphTest {

  AnalysisOptions options;
  IAnalysisCacheView cache;
  ClassHierarchy cha;
  AnalysisScope scope;
  SSAPropagationCallGraphBuilder cgBuilder;

  @Test
  public void createAllCallgraphs() {
    testZeroCFA("CGTestProject1");
    testZeroCFA("CGTestProject2");
    testZeroCFA("CGTestProject3");
  }

  public void buildCHA(String projectName) {
    try {
      scope = new JavaSourceAnalysisScope();

      // add standard libraries to scope
      String[] stdlibs = WalaProperties.getJ2SEJarFiles();
      for (String stdlib : stdlibs) {
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(stdlib));
      }

      // add source code to scope
      String dir = System.getProperty("user.dir");
      String projectDir = dir + "/testdata/" + projectName + "/src";
      ClassLoaderReference classLoader = JavaSourceAnalysisScope.SOURCE;
      scope.addToScope(classLoader, new SourceDirectoryTreeModule(new File(projectDir)));

      // construct cha
      ClassLoaderFactory factory = new ECJClassLoaderFactory(scope.getExclusions());
      cha = ClassHierarchyFactory.make(scope, factory);

      // compute entry points
      Iterable<? extends Entrypoint> entrypoints =
          com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(classLoader, cha);

      options = new AnalysisOptions();
      options.setEntrypoints(entrypoints);
      options.setReflectionOptions(ReflectionOptions.NONE);

      cache = new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
    } catch (ClassHierarchyException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  // dot -Tpdf 0CFA_callgraph.dot -o 0CFA_callgraph.pdf
  public void testZeroCFA(String projectName) {
    try {
      buildCHA(projectName);
      cgBuilder =
          (SSAPropagationCallGraphBuilder)
              new ZeroCFABuilderFactory().make(options, cache, cha, scope);
      CallGraph cg = cgBuilder.makeCallGraph(options);
      CallGraphPrinter.print(projectName + "_0CFA", cg, true);
    } catch (IllegalArgumentException | CancelException e) {
      throw new RuntimeException(e);
    }
  }

  public void testZeroOneContainerCFA(String projectName) {
    try {
      buildCHA(projectName);
      // this cg builder considers context-sensitivity for java util collections
      cgBuilder =
          (SSAPropagationCallGraphBuilder)
              new ZeroOneContainerCFABuilderFactory().make(options, cache, cha, scope);
      CallGraph cg = cgBuilder.makeCallGraph(options);
      CallGraphPrinter.print("0-1-container-CFA", cg, true);

    } catch (IllegalArgumentException | CancelException e) {
      throw new RuntimeException(e);
    }
  }

  public void testZeroOneCFA(String projectName) {
    try {
      buildCHA(projectName);
      cgBuilder = new ZeroOneCFABuilderFactory().make(options, cache, cha, scope);
      CallGraph cg = cgBuilder.makeCallGraph(options);
      CallGraphPrinter.print("0-1-CFA", cg, true);
    } catch (IllegalArgumentException | CancelException e) {
      throw new RuntimeException(e);
    }
  }
}
