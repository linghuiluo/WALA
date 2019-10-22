package com.ibm.wala.inc;

import com.ibm.wala.cast.java.translator.jdt.ecj.ECJClassLoaderFactory;
import com.ibm.wala.classLoader.ClassLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.SetOfClasses;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** @author Linghui Luo */
public class IncSourceLoaderFactory extends ECJClassLoaderFactory {

  private Map<ClassLoaderReference, IClassLoader> loaders;

  public IncSourceLoaderFactory(SetOfClasses exclusions) {
    super(exclusions);
    this.loaders = new HashMap<>();
  }

  @Override
  protected IClassLoader makeNewClassLoader(
      ClassLoaderReference classLoaderReference,
      IClassHierarchy cha,
      IClassLoader parent,
      AnalysisScope scope)
      throws IOException {
    if (classLoaderReference.getName().toString().startsWith("Incremental")) {
      ClassLoaderImpl cl = makeIncClassLoader(classLoaderReference, cha, parent);
      this.loaders.put(classLoaderReference, cl);
      return cl;
    } else {
      if (this.loaders.containsKey(classLoaderReference)) {
        return this.loaders.get(classLoaderReference);
      } else {
        IClassLoader cl = super.makeNewClassLoader(classLoaderReference, cha, parent, scope);
        this.loaders.put(classLoaderReference, cl);
        return cl;
      }
    }
  }

  @Override
  public IClassLoader getLoader(
      ClassLoaderReference classLoaderReference, IClassHierarchy cha, AnalysisScope scope)
      throws IOException {
    if (classLoaderReference.getName().toString().startsWith("Incremental")) {
      ClassLoaderReference parentRef = classLoaderReference.getParent();
      IClassLoader parent = null;
      if (parentRef != null) {
        parent = getLoader(parentRef, cha, scope);
      }
      return makeNewClassLoader(classLoaderReference, cha, parent, scope);
    } else return super.getLoader(classLoaderReference, cha, scope);
  }

  protected ClassLoaderImpl makeIncClassLoader(
      ClassLoaderReference classLoaderReference, IClassHierarchy cha, IClassLoader parent) {
    return new IncSourceLoader(classLoaderReference, parent, cha);
  }
}
