package com.ibm.wala.inc;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.impl.ZeroCFABuilderFactory;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJClassLoaderFactory;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.ClassLoaderFactory;
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
import com.ibm.wala.util.CancelException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.ServerConfiguration;
import magpiebridge.projectservice.java.JavaProjectService;
import org.junit.Test;

public class VersionedClassHierarchyTest {

  @Test
  public void test() {

    MagpieServer magpieServer = new MagpieServer(new ServerConfiguration());
    String language = "java";
    IProjectService ps = new JavaProjectService();
    magpieServer.addProjectService(language, ps);
    ServerAnalysis analysis =
        new ServerAnalysis() {
          VersionedClassHierarchy vcha;
          SSAPropagationCallGraphBuilder builder;
          CallGraph cg;

          @Override
          public String source() {
            return null;
          }

          @Override
          public void analyze(Collection<Module> files, MagpieServer server) {

            //

            // magpieBridge sends the files which changed

            // magpieBridge sends the ranges of changes
            List<Position> ranges = null;

            //

            IncClassLoader incLoader =
                new IncClassLoader(
                    ClassLoaderReference.Incremental,
                    vcha.getLoader(ClassLoaderReference.Application),
                    vcha);

            try {
              incLoader.init(new ArrayList<>(files));

              List<Change> changes = incLoader.getChanges(ranges);

              vcha.addLatestIncrement(incLoader);

              builder.adaptIncrements(changes);

              CallGraph cg = builder.getCallGraph();

              Collection<AnalysisResult> results = Collections.emptyList();
              server.consume(results, source());

            } catch (CancelCHAConstructionException | IOException e) {

              throw new RuntimeException(e);
            }
          }

          @Override
          public void prepare(IProjectService ps) {
            try {
              AnalysisScope scope = new JavaSourceAnalysisScope();

              // add standard libraries to scope
              String[] stdlibs = WalaProperties.getJ2SEJarFiles();
              for (String stdlib : stdlibs) {
                scope.addToScope(ClassLoaderReference.Primordial, new JarFile(stdlib));
              }

              JavaProjectService javaPs = (JavaProjectService) ps;

              Set<Path> sourcePath = javaPs.getSourcePath();

              // add the source directory to scope

              for (Path path : sourcePath) {
                scope.addToScope(
                    JavaSourceAnalysisScope.SOURCE, new SourceDirectoryTreeModule(path.toFile()));
              }

              ClassLoaderFactory factory = new ECJClassLoaderFactory(scope.getExclusions());
              vcha = VersionedClassHierarchyFactory.make(scope, factory);
              AnalysisOptions options = new AnalysisOptions();
              Iterable<? extends Entrypoint> entrypoints =
                  com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(
                      scope.getApplicationLoader(), vcha);
              options.setEntrypoints(entrypoints);
              options.setReflectionOptions(ReflectionOptions.NONE);
              IAnalysisCacheView cache = new AnalysisCacheImpl(AstIRFactory.makeDefaultFactory());
              builder =
                  (SSAPropagationCallGraphBuilder)
                      new ZeroCFABuilderFactory().make(options, cache, vcha, scope);
              builder.makeCallGraph(options);
              cg = builder.getCallGraph();
            } catch (IllegalArgumentException | CancelException | IOException e) {
              throw new RuntimeException(e);
            }
          }
        };

    magpieServer.addAnalysis(language, analysis);
  }
}
