package com.ibm.wala.inc;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.propagation.Change;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.strings.Atom;

public class IncClassLoader implements IClassLoader{
	
	private int version;
	
	public IncClassLoader()
	{
		this.version = VersionedClassHierarchy.latestVersion +1;
	}
	
	
	
	public int getVersion()
	{
		return this.version;
	}
	@Override
	public IClass lookupClass(TypeName className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoaderReference getReference() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<IClass> iterateAllClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfClasses() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Atom getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Language getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SSAInstructionFactory getInstructionFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfMethods() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSourceFileName(IMethod method, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getSource(IMethod method, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSourceFileName(IClass klass) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reader getSource(IClass klass) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassLoader getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(List<Module> modules) throws IOException {
		// TODO load classes from modules‚
		
	}

	@Override
	public void removeAll(Collection<IClass> toRemove) {
		// TODO Auto-generated method stub
		
	}
	
	public List<Change> getChanges(List<Position> ranges)
	{
		// TODO return a list of changes based on the ranges
		return null;
	}

}
