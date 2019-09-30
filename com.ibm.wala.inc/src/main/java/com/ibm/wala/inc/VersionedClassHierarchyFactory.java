package com.ibm.wala.inc;

import java.util.concurrent.ConcurrentHashMap;

import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;

/**
 * 
 * @author Linghui Luo
 *
 */
public class VersionedClassHierarchyFactory extends ClassHierarchyFactory {

	public static VersionedClassHierarchy make(VersionedClassHierarchy previous, AnalysisScope scope,
			ClassLoaderFactory factory) {
		if (scope == null) {
			throw new IllegalArgumentException("null scope");
		}
		if (factory == null) {
			throw new IllegalArgumentException("null factory");
		}
		try {
			return new VersionedClassHierarchy(previous, scope, factory, scope.getLanguages(), null,
					new ConcurrentHashMap<>(), ClassHierarchy.MissingSuperClassHandling.NONE);
		} catch (ClassHierarchyException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
}
