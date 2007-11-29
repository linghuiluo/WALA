/*******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.model.java.lang;

import com.ibm.wala.annotations.Internal;

/**
 * @author sfink
 */
@Internal
public class System {

  /**
   * A simple model of object-array copy
   */
  static void arraycopy(Object src, Object dest) {
    if (!src.getClass().isArray() || !dest.getClass().isArray()) {
      return;
    }
    
    try {
      Object[] A = (Object[]) src;
      Object[] B = (Object[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }

    try {
      int[] A = (int[]) src;
      int[] B = (int[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
    
    try {
      char[] A = (char[]) src;
      char[] B = (char[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
    
    try {
      short[] A = (short[]) src;
      short[] B = (short[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
    
    try {
      long[] A = (long[]) src;
      long[] B = (long[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
    
    try {
      byte[] A = (byte[]) src;
      byte[] B = (byte[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
    
    try {
      double[] A = (double[]) src;
      double[] B = (double[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
    
    try {
      boolean[] A = (boolean[]) src;
      boolean[] B = (boolean[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
    
    try {
      float[] A = (float[]) src;
      float[] B = (float[]) dest;
      for (int i = 0; i < A.length; i++)
        B[i] = A[i];
    } catch (ClassCastException cce) {
    }
  }

//  public static void main(String[] args) {
//    char[] src1 = new char[] { 'a' , 'b' };
//    char[] dest1 = new char[] { 'c', 'd' };
//    arraycopy(src1, dest1);
//  }
}
