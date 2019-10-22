package com.ibm.wala.inc;

import com.ibm.wala.cast.java.translator.jdt.ecj.ECJSourceModuleTranslator;
import com.ibm.wala.classLoader.ModuleEntry;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import java.util.Set;

public class IncSourceModuleTranslator extends ECJSourceModuleTranslator {

  public IncSourceModuleTranslator(AnalysisScope scope, IncSourceLoader sourceLoader) {
    super(scope, sourceLoader);
  }

  @Override
  public void loadAllSources(Set<ModuleEntry> modules) {
    // make sure this.sources contains all source directories.
    //		Set<String> sources = new HashSet<>();
    //		for (ModuleEntry m : modules) {
    //			if (m.isSourceFile()) {
    //				SourceFileModule s = (SourceFileModule) m;
    //				sources.add(s.getFile().getParent());
    //			}
    //		}
    //		for (String s : this.sources) {
    //			sources.add(s);
    //		}
    //		this.sources = sources.toArray(new String[sources.size()]);

    super.loadAllSources(modules);
  }
}
