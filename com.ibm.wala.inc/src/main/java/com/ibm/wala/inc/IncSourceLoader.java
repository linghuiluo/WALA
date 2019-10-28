package com.ibm.wala.inc;

import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl.JavaClass;
import com.ibm.wala.cast.java.translator.SourceModuleTranslator;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceLoaderImpl;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.ModuleEntry;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.ipa.callgraph.propagation.Change;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** @author Linghui Luo */
public class IncSourceLoader extends ECJSourceLoaderImpl {

	public IncSourceLoader(ClassLoaderReference loaderRef, IClassLoader parent, IClassHierarchy cha) {
		super(loaderRef, parent, cha);
	}

	public List<Change> getChanges(List<Position> ranges) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SourceModuleTranslator getTranslator() {
		return new IncSourceModuleTranslator(cha.getScope(), this);
	}

	@Override
	protected boolean shouldIgnore(JavaClass javaClass) {
		return false;
	}

	@Override
	public void init(List<Module> modules) throws IOException {
		if (modules.isEmpty())
			super.init(modules);
		else {
			// load all source files at once.
			Set<ModuleEntry> sourceFiles = new HashSet<>();
			for (Module m : modules) {
				if (m instanceof SourceFileModule) {
					sourceFiles.addAll(super.getSourceFiles(m));
				}
			}
			loadAllSources(sourceFiles);
		}
	}

	@Override
	public String toString() {
		return "Incremental Java Source Loader (classes " + loadedClasses.values() + ')';
	}

	@Override
	public IClass lookupClass(TypeName className) {
		if (className == null) {
			throw new IllegalArgumentException("className is null");
		}

		// treat arrays specially:
		if (className.isArrayType()) {
			return arrayClassLoader.lookupClass(className, this, cha);
		}

		// try current inc loader first
		IClass result = loadedClasses.get(className);
		// try delegating first.
		if (result == null) {
			IClassLoader parent = getParent();
			if (parent != null) {
				result = parent.lookupClass(className);
			}
			if (result != null) {
				return result;
			}
		}
		return result;
	}
	
}
