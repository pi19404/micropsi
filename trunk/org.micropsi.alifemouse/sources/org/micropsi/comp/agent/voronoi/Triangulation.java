/*
 * Copyright (c) 2005 by L. Paul Chew.
 * 
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following 
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package org.micropsi.comp.agent.voronoi;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Triangulation on vertices (generic type V).
 * A Triangulation is a set of Simplices (see Simplex below).
 * For efficiency, we keep track of the neighbors of each Simplex.
 * Two Simplices are neighbors of they share a facet.
 * 
 * @author Paul Chew
 * 
 * Created July 2005.  Derived from an earlier, messier version.
 */
public class Triangulation<V> implements Iterable<Simplex<V>> {
    
    private HashMap<Simplex<V>,HashSet<Simplex<V>>> neighbors;  // Maps Simplex to neighbors
    
    /**
     * Constructor.
     * @param simplex the initial Simplex.
     */
    public Triangulation (Simplex<V> simplex) {
        neighbors = new HashMap<Simplex<V>,HashSet<Simplex<V>>>();
        neighbors.put(simplex, new HashSet<Simplex<V>>());
    }
    
    /**
     * String representation.
     * Shows number of simplices currently in the Triangulation.
     * @return a String representing the Triangulation
     */
    public String toString () {
        return "Triangulation (with " + neighbors.size() + " elements)";
    }
    
    /**
     * Size (# of Simplices) in Triangulation.
     * @return the number of Simplices in this Triangulation
     */
    public int size () {
        return neighbors.size();
    }
    
    /**
     * True iff the simplex is in this Triangulation.
     * @param simplex the simplex to check
     * @return true iff the simplex is in this Triangulation
     */
    public boolean contains (Simplex<V> simplex) {
        return this.neighbors.containsKey(simplex);
    }
    
    /**
     * Iterator.
     * @return an iterator for every Simplex in the Triangulation
     */
    public Iterator<Simplex<V>> iterator () {
        return Collections.unmodifiableSet(this.neighbors.keySet()).iterator();
    }
    
    /**
     * Print stuff about a Triangulation.
     * Used for debugging.
     */
    public void printStuff () {
        boolean remember = Simplex.isMoreInfo();
        System.out.println("Neighbor data for " + this);
        for (Simplex<V> simplex: neighbors.keySet()) {
            Simplex.setMoreInfo(true);
            System.out.print("    " + simplex + ":");
            Simplex.setMoreInfo(false);
            for (Simplex neighbor: neighbors.get(simplex))
                System.out.print(" " + neighbor);
            System.out.println();
        }
        Simplex.setMoreInfo(remember);
    }
    
    /* Navigation */
    
    /**
     * Report neighbor opposite the given vertex of simplex.
     * @param vertex a vertex of simplex
     * @param simplex we want the neighbor of this Simplex
     * @return the neighbor opposite vertex of simplex; null if none
     * @throws IllegalArgumentException if vertex is not in this Simplex
     */
    public Simplex<V> neighborOpposite (Object vertex, Simplex<V> simplex) {
        if (!simplex.contains(vertex))
            throw new IllegalArgumentException("Bad vertex; not in simplex");
        SimplexLoop: for (Simplex<V> s: neighbors.get(simplex)) {
            for (V v: simplex) {
                if (v.equals(vertex)) continue;
                if (!s.contains(v)) continue SimplexLoop;
            }
            return s;
        }
        return null;
    }
    
    /**
     * Report neighbors of the given simplex.
     * @param simplex a Simplex
     * @return the Set of neighbors of simplex
     */
    public Set<Simplex<V>> neighbors (Simplex<V> simplex) {
        return new HashSet<Simplex<V>>(this.neighbors.get(simplex));
    }
    
    /* Modification */
    
    /**
     * Update by replacing one set of Simplices with another.
     * Both sets of simplices must fill the same "hole" in the
     * Triangulation.
     * @param oldSet set of Simplices to be replaced
     * @param newSet set of replacement Simplices
     */
    public void update (Set<? extends Simplex<V>> oldSet, 
                        Set<? extends Simplex<V>> newSet) {
        // Collect all simplices neighboring the oldSet
        Set<Simplex<V>> allNeighbors = new HashSet<Simplex<V>>();
        for (Simplex<V> simplex: oldSet)
            allNeighbors.addAll(neighbors.get(simplex));
        // Delete the oldSet
        for (Simplex<V> simplex: oldSet) {
            for (Simplex<V> n: neighbors.get(simplex))
                neighbors.get(n).remove(simplex);
            neighbors.remove(simplex);
            allNeighbors.remove(simplex);
        }
        // Include the newSet simplices as possible neighbors
        allNeighbors.addAll(newSet);
        // Create entries for the simplices in the newSet
        for (Simplex<V> s: newSet)
            neighbors.put(s, new HashSet<Simplex<V>>());
        // Update all the neighbors info
        for (Simplex<V> s1: newSet)
        for (Simplex<V> s2: allNeighbors) {
            if (!s1.isNeighbor(s2)) continue;
            neighbors.get(s1).add(s2);
            neighbors.get(s2).add(s1);
        }
    }
}

