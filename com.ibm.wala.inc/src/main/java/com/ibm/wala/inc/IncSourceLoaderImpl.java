package com.ibm.wala.inc;

import com.ibm.wala.cast.java.translator.SourceModuleTranslator;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceLoaderImpl;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.ModuleEntry;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.propagation.Change;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** @author Linghui Luo */
public class IncSourceLoaderImpl extends ECJSourceLoaderImpl {

  private int version;

  public IncSourceLoaderImpl(
      ClassLoaderReference loaderRef, IClassLoader parent, IClassHierarchy cha) {
    super(loaderRef, parent, cha);
    this.version = VersionedClassHierarchy.latestVersion + 1;
  }

  public int getVersion() {
    return this.version;
  }

  public List<Change> getChanges(List<Position> ranges) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected SourceModuleTranslator getTranslator() {
    return new IncSourceModuleTranslator(cha.getScope(), this);
  }

  @Override
  public void init(List<Module> modules) throws IOException {
    if (modules.isEmpty()) super.init(modules);
    else {
      // load all source files at once.
      Set<ModuleEntry> sourceFiles = new HashSet<>();
      for (Module m : modules) {
        if (m instanceof SourceFileModule) {
          sourceFiles.addAll(super.getSourceFiles(m));
        }
      }
      loadAllSources(sourceFiles);
    }
  }

  @Override
  public String toString() {
    return "Incremental Java Source Loader (classes " + loadedClasses.values() + ')';
  }

  @Override
  public IClass lookupClass(TypeName className) {
    if (loadedClasses.containsKey(className)) return loadedClasses.get(className);
    else {
      VersionedClassHierarchy vcha = (VersionedClassHierarchy) cha;
      IClass result = null;
      if (vcha.hasPrevious())
        result =
            vcha.getPrevious()
                .getLoader(IncJavaSourceAnalysisScope.INCREMENTAL)
                .lookupClass(className);
      else result = vcha.getLoader(IncJavaSourceAnalysisScope.SOURCE).lookupClass(className);
      return result;
    }
  }

  // public Collection<IClass> getAllClasses()
  // {
  // Collection<IClass> latestClasses= loadedClasses.values();
  // Iterator<IClass> it=this.getParent().iterateAllClasses();
  // //todo
  // return null;
  // }

}
