package com.ibm.wala.inc;

import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import java.util.concurrent.ConcurrentHashMap;

/** @author Linghui Luo */
public class IncClassHierarchyFactory extends ClassHierarchyFactory {

  public static IncClassHierarchy make(
      IncClassHierarchy previous, IncJavaSourceAnalysisScope scope, ClassLoaderFactory factory) {
    if (scope == null) {
      throw new IllegalArgumentException("null scope");
    }
    if (factory == null) {
      throw new IllegalArgumentException("null factory");
    }
    try {
      return new IncClassHierarchy(
          scope,
          factory,
          scope.getLanguages(),
          null,
          new ConcurrentHashMap<>(),
          ClassHierarchy.MissingSuperClassHandling.NONE);
    } catch (ClassHierarchyException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }
}
