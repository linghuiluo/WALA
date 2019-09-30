/**
 * ***************************************************************************** Copyright (c) 2007
 * IBM Corporation. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * <p>Contributors: IBM Corporation - initial API and implementation
 * *****************************************************************************
 */
package com.ibm.wala.ipa.callgraph.propagation;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.IMethod.SourcePosition;
import java.util.List;

public class Change {

  private IClass cl;

  private List<IMethod> methods;

  private SourcePosition pos;

  public Change(IClass cl, SourcePosition pos) {
    this.cl = cl;
    this.pos = pos;
  }

  public List<IMethod> getMethod() {
    return this.methods;
  }
}
