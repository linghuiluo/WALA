package com.ibm.wala.inc;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.impl.ZeroCFABuilderFactory;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceDirectoryTreeModule;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.MethodTargetSelector;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.properties.WalaProperties;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import org.junit.Test;

public class IncClassHierarchyTest {

  IncJavaSourceAnalysisScope scope;
  IncClassHierarchy icha;
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
      icha = IncClassHierarchyFactory.make(null, scope, factory);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void incLoad1() {
    File fileA = new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/A.java");
    File fileC = new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/C.java");
    File fileMain =
        new File(System.getProperty("user.dir") + "/testdata/CGTestProject2/src/Main.java");
    List<Module> files = new ArrayList<>();
    files.add(new SourceFileModule(fileA, "A", null));
    files.add(new SourceFileModule(fileC, "C", null));
    files.add(new SourceFileModule(fileMain, "Main", null));

    icha.update(files);
  }

  public void incLoad2() {
    File fileA = new File(System.getProperty("user.dir") + "/testdata/CGTestProject3/src/A.java");
    File fileMain =
        new File(System.getProperty("user.dir") + "/testdata/CGTestProject3/src/Main.java");

    List<Module> files = new ArrayList<>();
    files.add(new SourceFileModule(fileA, "A", null));
    files.add(new SourceFileModule(fileMain, "Main", null));

    icha.update(files);
  }

  public void buildCallGraph(String name) {
    // compute entry points
    Iterable<? extends Entrypoint> entrypoints = Util.makeMainEntrypoints(icha);
    AnalysisOptions options = new AnalysisOptions();
    options.setEntrypoints(entrypoints);
    options.setReflectionOptions(ReflectionOptions.NONE);

    IAnalysisCacheView cache = new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
    SSAPropagationCallGraphBuilder cgBuilder =
        (SSAPropagationCallGraphBuilder)
            new ZeroCFABuilderFactory().make(options, cache, icha, scope);

    MethodTargetSelector previous = options.getMethodTargetSelector();
    options.setSelector(
        new MethodTargetSelector() {

          @Override
          public IMethod getCalleeTarget(CGNode caller, CallSiteReference site, IClass receiver) {
            IMethod old = previous.getCalleeTarget(caller, site, receiver);
            // receiver can be null, e.g. static methods.
            if (old == null) {
              IClass latest =
                  icha.lookupClass(site.getDeclaredTarget().getDeclaringClass().getName());
              if (latest != null) {
                // this is the case that a class is updated, the old class is removed from icha
                IMethod m = latest.getMethod(site.getDeclaredTarget().getSelector());
                return m;
              } else {
                return null;
              }
            } else {
              IClass latest = icha.lookupClass(old.getDeclaringClass().getName());
              if (latest == old.getDeclaringClass()) {
                return old;
              } else {
                return latest.getMethod(old.getSelector());
              }
            }
          }
        });
    try {
      CallGraph cg = cgBuilder.makeCallGraph(options);
      CallGraphPrinter.print(name + "_0CFA", cg, true);
    } catch (IllegalArgumentException | CancelException e) {
      throw new RuntimeException(e);
    }
  }
}
