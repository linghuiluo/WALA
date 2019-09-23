package com.ibm.wala.inc;

import java.util.Collection;
import java.util.Map;

import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.CancelCHAConstructionException;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.MonitorUtil.IProgressMonitor;
import com.ibm.wala.util.NullProgressMonitor;

public class VersionedClassHierarchy extends ClassHierarchy {

	public static int latestVersion = 0;

	public VersionedClassHierarchy(AnalysisScope scope, ClassLoaderFactory factory, Collection<Language> languages,
			IProgressMonitor progressMonitor, Map<TypeReference, Node> map,
			MissingSuperClassHandling superClassHandling) throws ClassHierarchyException, IllegalArgumentException {
		super(scope, factory, languages, progressMonitor, map, superClassHandling);

	}

	public void addLatestIncrement(IClassLoader cl) throws CancelCHAConstructionException {
		this.addAllClasses(cl, new NullProgressMonitor());
	}

}
