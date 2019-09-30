package com.ibm.wala.inc;

import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceLoaderImpl;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.propagation.Change;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import java.util.List;

public class IncClassLoader extends ECJSourceLoaderImpl {

  private int version;

  public IncClassLoader(ClassLoaderReference loaderRef, IClassLoader parent, IClassHierarchy cha) {
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
}
