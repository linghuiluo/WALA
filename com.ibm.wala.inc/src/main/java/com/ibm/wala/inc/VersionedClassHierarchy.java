package com.ibm.wala.inc;

import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.JavaClass;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.CancelCHAConstructionException;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.MonitorUtil.IProgressMonitor;
import com.ibm.wala.util.NullProgressMonitor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/** @author Linghui Luo */
public class VersionedClassHierarchy extends ClassHierarchy {

  public static int latestVersion = 0;
  private VersionedClassHierarchy previous;
  private int version;

  public VersionedClassHierarchy(
      VersionedClassHierarchy previous,
      AnalysisScope scope,
      ClassLoaderFactory factory,
      Collection<Language> languages,
      IProgressMonitor progressMonitor,
      Map<TypeReference, Node> map,
      MissingSuperClassHandling superClassHandling)
      throws ClassHierarchyException, IllegalArgumentException {

    super(scope, factory, languages, progressMonitor, map, superClassHandling);
    Iterator<IClass> it = this.iterator();
    while (it.hasNext()) {
      IClass k = it.next();
      if (k instanceof JavaClass) {
        JavaClass klass = (JavaClass) k;
        System.out.println(
            "VersionedClassHierarchy - classs in vcha " + klass.getSourceURL().toString());
      }
    }
    this.previous = previous;
    this.latestVersion++;
    this.version = this.latestVersion;
  }

  public void addLatestIncrement(IClassLoader cl) throws CancelCHAConstructionException {
    this.addAllClasses(cl, new NullProgressMonitor());
  }

  public VersionedClassHierarchy getPrevious() {
    return this.previous;
  }

  public int getVersion() {
    return this.version;
  }

  public boolean hasPrevious() {
    return this.previous != null;
  }
}
