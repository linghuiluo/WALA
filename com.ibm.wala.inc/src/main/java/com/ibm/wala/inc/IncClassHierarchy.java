package com.ibm.wala.inc;

import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.JavaClass;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchy.MissingSuperClassHandling;
import com.ibm.wala.ipa.cha.ClassHierarchy.Node;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.MonitorUtil.IProgressMonitor;
import com.ibm.wala.util.collections.Iterator2Iterable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** @author Linghui Luo */
public class IncClassHierarchy extends ClassHierarchy {

  int updateCount = 0;
  IncJavaSourceAnalysisScope scope;

  public IncClassHierarchy(
      IncJavaSourceAnalysisScope scope,
      ClassLoaderFactory factory,
      Collection<Language> languages,
      IProgressMonitor progressMonitor,
      Map<TypeReference, Node> map,
      MissingSuperClassHandling superClassHandling)
      throws ClassHierarchyException, IllegalArgumentException {
    super(scope, factory, languages, progressMonitor, map, superClassHandling);
    this.scope = scope;
  }

  /**
   * update a list of changed files. New classes can be added or existing classes will be updated.
   *
   * @param files
   * @return
   */
  public IncClassHierarchy update(List<Module> files) {
    updateCount++;
    try {
      // create a new incremental class loader
      ClassLoaderReference loaderRef = this.scope.createIncLoaderReference();
      IClassLoader incloader = this.getFactory().getLoader(loaderRef, this, this.getScope());
      // add all source file to scope, this ensures the ECJSourceModuleTranslator computes the right
      // source directories from the scope
      for (Module file : files) {
        this.scope.addToScope(loaderRef, file);
      }
      incloader.init(files); // load all source files
      for (IClass klass : Iterator2Iterable.make(incloader.iterateAllClasses())) {
        updateClass(klass);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public IncClassHierarchy remove(List<TypeName> klasses) {
    // TODO: remove klasses from map and nameMap
    return this;
  }

  private void updateClass(IClass klass) {
    TypeName type = klass.getReference().getName();
    IClass lookup = this.lookupClass(type);
    if (lookup == null) {

      this.addClass(klass);
      System.err.println("ADD class " + klass.getName());
    } else {

      this.replaceClass(klass);
      System.err.println("replace class " + klass.getName());
    }
  }

  private void replaceClass(IClass klass) {
    Node updatedKlass = new Node(klass);
    this.map.put(klass.getReference(), updatedKlass);
    this.nameMap.put(klass.getReference().getName(), updatedKlass);
  }

  private IClass lookupClass(TypeName t) {
    Node node = this.nameMap.get(t);
    if (node != null) {
      return node.getJavaClass();
    } else {
      return null;
    }
  }

  public void printAllClasses() {
    Iterator<IClass> it = this.iterator();
    while (it.hasNext()) {
      IClass k = it.next();
      if (k instanceof JavaClass) {
        JavaClass klass = (JavaClass) k;
        System.out.println(
            "VersionedClassHierarchy - classs in vcha " + klass.getSourceURL().toString());
      }
    }
  }
}
