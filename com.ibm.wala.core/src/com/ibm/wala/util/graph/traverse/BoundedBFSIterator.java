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
package com.ibm.wala.util.graph.traverse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.NonNullSingletonIterator;
import com.ibm.wala.util.graph.Graph;

/**
 * This class implements breadth-first search over a Graph, returning an
 * Iterator of the nodes of the graph in order of discovery. This class follows
 * the outNodes of the graph nodes to define the graph, but this behavior can be
 * changed by overriding the getConnected method.
 * 
 * This traversal only visits nodes within k hops of a root.
 * 
 * @author Stephen Fink
 */
public class BoundedBFSIterator<T> implements Iterator<T> {

  /**
   * List of nodes as discovered
   */
  ArrayList<T> Q = new ArrayList<T>();

  /**
   * Set of nodes that have been visited
   */
  HashSet<T> visited = HashSetFactory.make();

  /**
   * index of the node currently being searched
   */
  private int index = 0;

  /**
   * Governing Graph
   */
  protected Graph<T> G;

  /**
   * limit on number of hops
   */
  private final int k;

  /**
   * boundary[i] is the first index which represents a child that is > i hops
   * away.
   */
  private final int[] boundary;

  /**
   * how many hops away is the next element.
   */
  private int currentHops = 0;

  /**
   * Construct a breadth-first iterator starting with a particular node in a
   * directed graph.
   * 
   * @param G
   *          the graph whose nodes to enumerate
   */
  public BoundedBFSIterator(Graph<T> G, T N, int k) {
    this.k = k;
    boundary = new int[k];
    init(G, new NonNullSingletonIterator<T>(N));
  }

  /**
   * Construct a breadth-first enumerator across the (possibly improper) subset
   * of nodes reachable from the nodes in the given enumeration.
   * 
   * @param G
   *          the graph whose nodes to enumerate
   * @param nodes
   *          the set of nodes from which to start searching
   */
  public BoundedBFSIterator(Graph<T> G, Iterator <? extends T> nodes, int k) {
    this.k = k;
    boundary = new int[k];
    init(G, nodes);
  }


  private void init(Graph<T> G, Iterator<? extends T> nodes) {
    this.G = G;
    if (G.getNumberOfNodes() == 0) {
      return;
    }
    while (nodes.hasNext()) {
      T o = nodes.next();
      if (!visited.contains(o)) {
        Q.add(o);
        visited.add(o);
      }
    }
    index = 0;
    T current = Q.get(0);
    visitChildren(current);
  }

  private void visitChildren(T N) {
    if (currentHops == k) {
      return;
    }
    if (boundary[currentHops] == 0) {
      boundary[currentHops] = Q.size();
    }
    for (Iterator<? extends T> children = getConnected(N); children.hasNext();) {
      T child = children.next();
      if (!visited.contains(child)) {
        Q.add(child);
        visited.add(child);
      }
    }
  }

  /**
   * Return whether there are any more nodes left to enumerate.
   * 
   * @return true if there nodes left to enumerate.
   */
  public boolean hasNext() {
    return (Q.size() > index);
  }

  /**
   * Find the next graph node in discover time order.
   * 
   * @return the next graph node in discover time order.
   */
  public T next() throws NoSuchElementException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    T result = Q.get(index);
    index++;
    if (currentHops < k && index == boundary[currentHops]) {
      currentHops++;
    }
    if (hasNext()) {
      T N = Q.get(index);
      visitChildren(N);
    }
    return result;
  }

  /**
   * get the out edges of a given node
   * 
   * @param n
   *          the node of which to get the out edges
   * @return the out edges
   * 
   */
  protected Iterator<? extends T> getConnected(T n) {
    return G.getSuccNodes(n);
  }

  /**
   * @see java.util.Iterator#remove()
   */
  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}
