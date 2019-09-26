package com.ibm.wala.inc;

import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.SourceModuleTranslator;
import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceLoaderImpl;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.propagation.Change;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.strings.Atom;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IncClassLoader extends ECJSourceLoaderImpl{

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
