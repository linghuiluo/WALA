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
package com.ibm.wala.util.intset;

import com.ibm.wala.util.DeterministicHashCode;
import com.ibm.wala.util.debug.Assertions;

/**
 * 
 * A subclass of BitVector that implements the MutableIntSet interface.
 * 
 * Note that this is NOT a value with regard to hashCode and equals.
 * 
 * @author sfink
 */
public final class BitVectorIntSet implements MutableIntSet {

  // population count of -1 means needs to be computed again.
  private int populationCount = 0;

  private static final int UNDEFINED = -1;

  private BitVector bitVector = new BitVector(0);

  private final int hash;


  public BitVectorIntSet() {
    this.hash = DeterministicHashCode.get();
  }
  
  public BitVectorIntSet(BitVector v) {
    this.hash = DeterministicHashCode.get();
    bitVector.or(v);
    populationCount = UNDEFINED;
  }

  public BitVectorIntSet(IntSet S) {
    if (Assertions.verifyAssertions) {
      Assertions._assert(S != null);
    }
    copySet(S);
    this.hash = DeterministicHashCode.get();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.MutableIntSet#copySet(com.ibm.wala.util.intset.IntSet)
   */
  public void copySet(IntSet set) {
    if (set instanceof BitVectorIntSet) {
      BitVectorIntSet S = (BitVectorIntSet) set;
      bitVector = new BitVector(S.bitVector);
      populationCount = S.populationCount;
    } else if (set instanceof MutableSharedBitVectorIntSet) {
      BitVectorIntSet S = ((MutableSharedBitVectorIntSet) set).makeDenseCopy();
      bitVector = new BitVector(S.bitVector);
      populationCount = S.populationCount;
    } else if (set instanceof SparseIntSet) {
      SparseIntSet s = (SparseIntSet) set;
      if (s.size == 0) {
        populationCount = 0;
        bitVector = new BitVector(0);
      } else {
        bitVector = new BitVector(s.max());
        populationCount = s.size;
        for (int i = 0; i < s.size; i++) {
          bitVector.set(s.elements[i]);
        }
      }
    } else if (set instanceof BimodalMutableIntSet) {
      IntSet backing = ((BimodalMutableIntSet)set).getBackingStore();
      copySet(backing);
    } else {
      bitVector.clearAll();
      populationCount = set.size();
      for (IntIterator it = set.intIterator(); it.hasNext();) {
        bitVector.set(it.next());
      }
    }

  }


  /* (non-Javadoc)
   */
  public boolean addAll(IntSet set) {
    if (set instanceof BitVectorIntSet) {
      BitVector B = ((BitVectorIntSet) set).bitVector;
      int delta = bitVector.orWithDelta(B);
      populationCount += delta;
      populationCount = (populationCount == (delta + UNDEFINED)) ? UNDEFINED : populationCount;
      return (delta != 0);
    } else {
      BitVectorIntSet other = new BitVectorIntSet(set);
      return addAll(other);
    }
  }
  
  /**
   * this version of add all will likely be faster
   * if the client doesn't care about the change or
   * the population count.
   * @param set
   */
  public void addAllOblivious(IntSet set) {
    if (set instanceof BitVectorIntSet) {
      BitVector B = ((BitVectorIntSet) set).bitVector;
      bitVector.or(B);
      populationCount = UNDEFINED;
    } else {
      BitVectorIntSet other = new BitVectorIntSet(set);
      addAllOblivious(other);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.MutableIntSet#add(int)
   */
  public boolean add(int i) {
    if (bitVector.get(i)) {
      return false;
    } else {
      bitVector.set(i);
      populationCount++;
      populationCount = (populationCount == (UNDEFINED + 1)) ? UNDEFINED : populationCount;
      return true;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.MutableIntSet#remove(int)
   */
  public boolean remove(int i) {
    if (contains(i)) {
      populationCount--;
      populationCount = (populationCount == UNDEFINED - 1) ? UNDEFINED : populationCount;
      bitVector.clear(i);
      return true;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.MutableIntSet#intersectWith(com.ibm.wala.util.intset.IntSet)
   */
  public void intersectWith(IntSet set) {
    if (!(set instanceof BitVectorIntSet)) {
      set = new BitVectorIntSet(set);
    }
    BitVector B = ((BitVectorIntSet) set).bitVector;
    bitVector.and(B);
    populationCount = UNDEFINED;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#intersection(com.ibm.wala.util.intset.IntSet)
   */
  public IntSet intersection(IntSet that) {
    BitVectorIntSet newbie = new BitVectorIntSet();
    newbie.copySet(this);
    newbie.intersectWith(that);
    return newbie;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#isEmpty()
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#size()
   */
  public int size() {
    populationCount = (populationCount == UNDEFINED) ? bitVector.populationCount() : populationCount;
    return populationCount;
  }

  /**
   * Use with extreme care; doesn't detect ConcurrentModificationExceptions
   */
  public IntIterator intIterator() {
    populationCount = (populationCount == UNDEFINED) ? bitVector.populationCount() : populationCount;
    return new IntIterator() {
      int count = 0;

      int last = 0;

      public boolean hasNext() {
        return count < populationCount;
      }

      public int next() {
        count++;
        last = nextSetBit(last) + 1;
        return last - 1;
      }
    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#foreach(com.ibm.wala.util.intset.IntSetAction)
   */
  public void foreach(IntSetAction action) {
    int nextBit = bitVector.nextSetBit(0);
    populationCount = (populationCount == UNDEFINED) ? bitVector.populationCount() : populationCount;
    for (int i = 0; i < populationCount; i++) {
      action.act(nextBit);
      nextBit = bitVector.nextSetBit(nextBit + 1);
    }
  }

  public SparseIntSet makeSparseCopy() {
    populationCount = (populationCount == UNDEFINED) ? bitVector.populationCount() : populationCount;
    int[] elements = new int[populationCount];
    int i = 0;
    int nextBit = -1;
    while (i < populationCount)
      elements[i++] = nextBit = bitVector.nextSetBit(nextBit + 1);

    return new SparseIntSet(elements);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#foreach(com.ibm.wala.util.intset.IntSetAction)
   */
  public void foreachExcluding(IntSet X, IntSetAction action) {
    if (X instanceof BitVectorIntSet) {
      fastForeachExcluding((BitVectorIntSet) X, action);
    } else {
      slowForeachExcluding(X, action);
    }
  }

  private void slowForeachExcluding(IntSet X, IntSetAction action) {
    populationCount = (populationCount == UNDEFINED) ? bitVector.populationCount() : populationCount;
    for (int i = 0, count = 0; count < populationCount; i++) {
      if (contains(i)) {
        if (!X.contains(i)) {
          action.act(i);
        }
        count++;
      }
    }
  }

  /**
   * internal optimized form
   * 
   * @param X
   * @param action
   */
  private void fastForeachExcluding(BitVectorIntSet X, IntSetAction action) {
    int[] bits = bitVector.bits;
    int[] xbits = X.bitVector.bits;

    int w = 0;
    while (w < xbits.length && w < bits.length) {
      int b = bits[w] & ~xbits[w];
      actOnWord(action, w << 5, b);
      w++;
    }
    while (w < bits.length) {
      actOnWord(action, w << 5, bits[w]);
      w++;
    }
  }

  private void actOnWord(IntSetAction action, int startingIndex, int word) {
    if (word != 0) {
      if ((word & 0x1) != 0) {
        action.act(startingIndex);
      }
      for (int i = 0; i < 31; i++) {
        startingIndex++;
        word = word >>> 1;
        if ((word & 0x1) != 0) {
          action.act(startingIndex);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    return this == obj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return hash;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#contains(int)
   */
  public boolean contains(int i) {
    if (Assertions.verifyAssertions) {
      Assertions._assert(i >= 0);
    }
    return bitVector.get(i);
  }

  public int max() {
    return bitVector.max();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return bitVector.toString();
  }

  /**
   * @param n
   * @return min j >= n s.t get(j)
   */
  public int nextSetBit(int n) {
    return bitVector.nextSetBit(n);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#sameValue(com.ibm.wala.util.intset.IntSet)
   */
  public boolean sameValue(IntSet that) {
    if (that instanceof BitVectorIntSet) {
      BitVectorIntSet b = (BitVectorIntSet) that;
      return bitVector.sameBits(b.bitVector);
    } else if (that instanceof BimodalMutableIntSet) {
      return that.sameValue(this);
    } else if (that instanceof SparseIntSet) {
      return sameValueInternal((SparseIntSet) that);
    } else if (that instanceof MutableSharedBitVectorIntSet) {
      return sameValue(((MutableSharedBitVectorIntSet) that).makeDenseCopy());
    } else {
      Assertions.UNREACHABLE("unexpected argument type " + that.getClass());
      return false;
    }
  }

  /**
   */
  private boolean sameValueInternal(SparseIntSet that) {
    populationCount = (populationCount == UNDEFINED) ? bitVector.populationCount() : populationCount;
    if (populationCount != that.size()) {
      return false;
    }
    for (int i = 0; i < that.size(); i++) {
      int val = that.elementAt(i);
      if (!bitVector.contains(val)) {
        return false;
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#isSubset(com.ibm.wala.util.intset.IntSet)
   */
  public boolean isSubset(IntSet that) {
    if (that instanceof BitVectorIntSet) {
      return bitVector.isSubset(((BitVectorIntSet) that).bitVector);
    } else if (that instanceof SparseIntSet) {
      return isSubsetInternal((SparseIntSet) that);
    } else {
      // really slow.  optimize as needed.
      for (IntIterator it = intIterator(); it.hasNext(); ) {
        int x = it.next();
        if (!that.contains(x)) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * @param set
   */
  private boolean isSubsetInternal(SparseIntSet set) {
    return toSparseIntSet().isSubset(set);
  }

  /**
   */
  public BitVector getBitVector() {
    return bitVector;
  }

  /**
   * TODO: optimize
   * 
   */
  public SparseIntSet toSparseIntSet() {
    MutableSparseIntSet result = new MutableSparseIntSet();
    for (IntIterator it = intIterator(); it.hasNext();) {
      result.add(it.next());
    }
    return result;
  }

  /**
   * @param set
   */
  public boolean removeAll(BitVectorIntSet set) {
    int oldSize = size();
    bitVector.andNot(set.bitVector);
    populationCount = UNDEFINED;
    return oldSize > size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.IntSet#containsAny(com.ibm.wala.util.intset.IntSet)
   */
  public boolean containsAny(IntSet set) {
    if (set instanceof BitVectorIntSet) {
      BitVectorIntSet b = (BitVectorIntSet) set;
      return !bitVector.intersectionEmpty(b.bitVector);
    } else {
      // TODO: optimize
      for (IntIterator it = set.intIterator(); it.hasNext();) {
        if (contains(it.next())) {
          return true;
        }
      }
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.util.intset.MutableIntSet#addAllInIntersection(com.ibm.wala.util.intset.IntSet,
   *      com.ibm.wala.util.intset.IntSet)
   */
  public boolean addAllInIntersection(IntSet other, IntSet filter) {
    BitVectorIntSet o = new BitVectorIntSet(other);
    o.intersectWith(filter);
    return addAll(o);
  }

  public boolean containsAll(BitVectorIntSet other) {
    return other.isSubset(this);
  }
}
