package com.ibm.wala.inc;

import java.io.IOException;

import com.ibm.wala.cast.java.translator.jdt.ecj.ECJClassLoaderFactory;
import com.ibm.wala.classLoader.ClassLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.SetOfClasses;

/**
 * 
 * @author Linghui Luo
 *
 */
public class IncSourceLoaderFactory extends ECJClassLoaderFactory {

	public IncSourceLoaderFactory(SetOfClasses exclusions) {
		super(exclusions);
	}

	@Override
	protected IClassLoader makeNewClassLoader(ClassLoaderReference classLoaderReference, IClassHierarchy cha,
			IClassLoader parent, AnalysisScope scope) throws IOException {
		if (classLoaderReference.equals(IncJavaSourceAnalysisScope.INCREMENTAL)) {
			ClassLoaderImpl cl = makeIncClassLoader(classLoaderReference, cha, parent);
			cl.init(scope.getModules(classLoaderReference));
			return cl;
		} else
			return super.makeNewClassLoader(classLoaderReference, cha, parent, scope);

	}

	protected ClassLoaderImpl makeIncClassLoader(ClassLoaderReference classLoaderReference, IClassHierarchy cha,
			IClassLoader parent) {
		return new IncSourceLoaderImpl(classLoaderReference, parent, cha);
	}

}
