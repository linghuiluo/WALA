package com.ibm.wala.inc;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.impl.ZeroCFABuilderFactory;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import org.junit.Test;

public class VersionedClassHierarchyTest {

  IncJavaSourceAnalysisScope scope;
  VersionedClassHierarchy vcha;
  ClassLoaderFactory factory;

  @Test
  public void testIncLoading() {
    initialLoad();
    buildCallGraph("First");
    System.out.println();
    incLoad1();
    buildCallGraph("Second");
    System.out.println();
    incLoad2();
    buildCallGraph("Third");
    System.out.println();
  }

  public void initialLoad() {
    try {
      File projectSourcePath =
          new File(System.getProperty("user.dir") + "/testdata/CGTestProject1/src");
      scope = new IncJavaSourceAnalysisScope();

      // add standard libraries to scope
      String[] stdlibs = WalaProperties.getJ2SEJarFiles();
      for (String stdlib : stdlibs) {
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(stdlib));
      }

      // add source code path to scope
      scope.addToScope(
          IncJavaSourceAnalysisScope.SOURCE, new SourceDirectoryTreeModule(projectSourcePath));

      factory = new IncSourceLoaderFactory(scope.getExclusions());
      vcha = VersionedClassHierarchyFactory.make(null, scope, factory);
      System.out.println("VCHA Version: " + vcha.latestVersion);
    } catch (IllegalArgumentException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void incLoad1() {
    // File projectSourcePath = new File(System.getProperty("user.dir") +
    // "/testdata/CGTestProject2/src");
    // scope.resetModules(IncJavaSourceAnalysisScope.SOURCE);
    // scope.addToScope(IncJavaSourceAnalysisScope.SOURCE, new
    // SourceDirectoryTreeModule(projectSourcePath));
    File fileA = new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/A.java");
    File fileC = new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/C.java");
    File fileMain =
        new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/Main.java");
    // TODO give the ASTParser all changed classes at once.
    scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileA, "A");
    scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileC, "C");
    scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileMain, "Main");
    vcha = VersionedClassHierarchyFactory.make(vcha, scope, factory);
    System.out.println("VCHA Version: " + vcha.getVersion());
  }

  public void incLoad2() {
    File projectSourcePath =
        new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src");
    // scope.resetModules(IncJavaSourceAnalysisScope.SOURCE);
    scope.addToScope(
        IncJavaSourceAnalysisScope.SOURCE, new SourceDirectoryTreeModule(projectSourcePath));
    ;
    scope.resetModules(IncJavaSourceAnalysisScope.INCREMENTAL);
    File fileA = new File(System.getProperty("user.dir") + "/testdata/CGTestProject3/src/A.java");
    File fileMain =
        new File(System.getProperty("user.dir") + "/testdata/CGTestProject3/src/Main.java");
    scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileA, "A");
    scope.addSourceFileToScope(IncJavaSourceAnalysisScope.INCREMENTAL, fileMain, "Main");
    vcha = VersionedClassHierarchyFactory.make(vcha, scope, factory);
    System.out.println("VCHA Version: " + vcha.getVersion());
  }

  public void buildCallGraph(String name) {
    // compute entry points
    Iterable<? extends Entrypoint> entrypoints =
        Util.makeMainEntrypointsFromIncSourceLoader(IncJavaSourceAnalysisScope.INCREMENTAL, vcha);
    AnalysisOptions options = new AnalysisOptions();
    options.setEntrypoints(entrypoints);
    options.setReflectionOptions(ReflectionOptions.NONE);
    IAnalysisCacheView cache = new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
    SSAPropagationCallGraphBuilder cgBuilder =
        (SSAPropagationCallGraphBuilder)
            new ZeroCFABuilderFactory().make(options, cache, vcha, scope);
    try {
      CallGraph cg = cgBuilder.makeCallGraph(options);
      CallGraphPrinter.print(name + "_0CFA", cg, true);
    } catch (IllegalArgumentException | CancelException e) {
      throw new RuntimeException(e);
    }
  }
}
