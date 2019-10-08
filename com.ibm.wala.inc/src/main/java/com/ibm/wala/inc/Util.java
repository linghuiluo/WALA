package com.ibm.wala.inc;

import java.util.HashSet;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.JavaClass;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.strings.Atom;

public class Util {

	public static Iterable<Entrypoint> makeMainEntrypointsFromIncSourceLoader(ClassLoaderReference clr,
			IClassHierarchy cha) {
		if (cha == null) {
			throw new IllegalArgumentException("cha is null");
		}
		final Atom mainMethod = Atom.findOrCreateAsciiAtom("main");
		final HashSet<Entrypoint> result = HashSetFactory.make();
		for (IClass klass : cha) {
			if (klass.getClassLoader().getReference().equals(clr)) {
				MethodReference mainRef = MethodReference.findOrCreate(klass.getReference(), mainMethod,
						Descriptor.findOrCreateUTF8("([Ljava/lang/String;)V"));
				IMethod m = klass.getMethod(mainRef.getSelector());
				if (m != null) {
					System.out.println("Found main method in " + ((JavaClass) klass).getSourceURL());
					result.add(new DefaultEntrypoint(m, cha));
				}
			}

		}
		if (result.isEmpty()) {
			if (clr.getParent().equals(JavaSourceAnalysisScope.SOURCE))
				return makeMainEntrypointsFromIncSourceLoader(clr.getParent(), cha);
		}
		return result::iterator;
	}

}
