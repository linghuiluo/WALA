package com.ibm.wala.inc;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.strings.Atom;

/** @author Linghui Luo */
public class IncJavaSourceAnalysisScope extends JavaSourceAnalysisScope {
  static int count = 0;

  public IncJavaSourceAnalysisScope() {
    super();
  }

  public ClassLoaderReference createIncLoaderReference() {
    count++;
    String loaderName = "Incremental" + count;
    ClassLoaderReference loaderRef =
        new ClassLoaderReference(
            Atom.findOrCreateUnicodeAtom(loaderName),
            Atom.findOrCreateAsciiAtom("Java"),
            JavaSourceAnalysisScope.SOURCE);
    loadersByName.put(loaderRef.getName(), loaderRef);
    return loaderRef;
  }
}
