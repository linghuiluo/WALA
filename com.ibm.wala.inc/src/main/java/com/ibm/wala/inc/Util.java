package com.ibm.wala.inc;

public class Util {

  //  public static Iterable<Entrypoint> makeMainEntrypoints(IClassHierarchy cha) {
  //    if (cha == null) {
  //      throw new IllegalArgumentException("cha is null");
  //    }
  //    final Atom mainMethod = Atom.findOrCreateAsciiAtom("main");
  //    final HashSet<Entrypoint> result = HashSetFactory.make();
  //    for (IClass klass : cha) {
  //      if (klass.getClassLoader().getReference().equals(clr)) {
  //        MethodReference mainRef =
  //            MethodReference.findOrCreate(
  //                klass.getReference(),
  //                mainMethod,
  //                Descriptor.findOrCreateUTF8("([Ljava/lang/String;)V"));
  //        IMethod m = klass.getMethod(mainRef.getSelector());
  //        if (m != null) {
  //          System.out.println(
  //              Util.class + "- Found main method in " + ((JavaClass) klass).getSourceURL());
  //          result.add(new DefaultEntrypoint(m, cha));
  //        }
  //      }
  //    }
  //    if (result.isEmpty()) {
  //      if (clr.getParent().equals(JavaSourceAnalysisScope.SOURCE))
  //        return makeMainEntrypointsFromIncSourceLoader(clr.getParent(), cha);
  //    }
  //    return result::iterator;
  //  }
}
