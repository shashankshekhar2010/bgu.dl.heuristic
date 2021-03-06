/*
 * Copyright Dept. of Mathematics & Computer Science Univ. Paris-Descartes
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package pddl4j.exp;

import pddl4j.exp.term.Substitution;
import pddl4j.exp.term.Term;

/**
 * This interface defines a literal in the PDDL language.
 * 
 * @author Damien Pellier
 * @version 1.0 
 */
public interface Literal extends InitEl, Iterable<Term> {
    
    /**
     * Returns the predicate of this literal.
     * 
     * @return the predicate of this literal. 
     */
    String getPredicate();

    /**
     * Adds a new term to this literal.
     * 
     * @param term the term to add.
     * @return <code>true</tt> if the term was added; <code>false</code> otherwise.
     * @throws NullPointerException if <code>term == null</code>.
     */
    boolean add(Term term);

    /**
     * Returns the arity of this literal.
     * 
     * @return the arity of this literal.
     */
    int getArity();

    /**
     * Substitutes all occurrences of the variables that occur in this
     * expression and that are mapped in the substitution by its binding
     * expression.
     * 
     * @param sigma the substitution.
     * @return a substituted copy of this expression.
     * @throws NullPointerException if <code>sigma == null</code>.
     */
    Literal apply(Substitution sigma);
}
