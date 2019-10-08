package com.ibm.wala.inc;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.strings.Atom;
import java.util.List;

/** @author Linghui Luo */
public class IncJavaSourceAnalysisScope extends JavaSourceAnalysisScope {
  public static final ClassLoaderReference INCREMENTAL =
      new ClassLoaderReference(
          Atom.findOrCreateUnicodeAtom("Incremental"),
          Atom.findOrCreateAsciiAtom("Java"),
          JavaSourceAnalysisScope.SOURCE);

  public IncJavaSourceAnalysisScope() {
    super();
    initIncremental();
  }

  /** Create incremental class loader for java analysis */
  protected void initIncremental() {
    ClassLoaderReference incremental =
        new ClassLoaderReference(
            INCREMENTAL.getName(), INCREMENTAL.getLanguage(), loadersByName.get(APPLICATION));
    loadersByName.put(INCREMENTAL.getName(), incremental);
  }

  /** Return the information regarding the incremental loader. */
  public ClassLoaderReference getIncrementalLoaderReference() {
    return getLoader(INCREMENTAL.getName());
  }

  /** Reset the module list of giving loader * */
  public void resetModules(ClassLoaderReference loader) {
    List<Module> s = getModules(loader);
    s.clear();
  }
}
