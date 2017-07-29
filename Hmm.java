
package Ihmm;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.*;


public class Hmm<O extends Observation> 
implements Serializable, Cloneable
{		
	private double pi[];
	private double a[][];
	private ArrayList<Opdf<O>> opdfs;
	
	
	/**
	 * Creates a new HMM.  Each state has the same <i>pi</i> value and
	 * the transition probabilities are all equal.
	 *
	 * @param nbStates The (strictly positive) number of states of the HMM.
	 * @param opdfFactory A pdf generator that is used to build the
	 *        pdfs associated to each state.
	 */
	public Hmm(int nbStates, OpdfFactory<? extends Opdf<O>> opdfFactory)
	{
		if (nbStates <= 0)
			throw new IllegalArgumentException("Number of states must be " +
			"strictly positive");
		
		pi = new double[nbStates];
		a = new double[nbStates][nbStates];
		opdfs = new ArrayList<Opdf<O>>(nbStates);
		
		for (int i = 0; i < nbStates; i++) {
			pi[i] = 1. / ((double) nbStates);
			opdfs.add(opdfFactory.factor());
			
			for (int j = 0; j < nbStates; j++)
				a[i][j] = 1. / ((double) nbStates);
		}
	

	
	/**
	 * Creates a new HMM.  All the HMM parameters are given as arguments.
	 *
	 * @param pi The initial probability values.  <code>pi[i]</code> is the
	 *        initial probability of state <code>i</code>. This array is
	 *        copied. 
	 * @param a The state transition probability array. <code>a[i][j]</code>
	 *        is the probability of going from state <code>i</code> to state
	 *        <code>j</code>.  This array is copied.
	 * @param opdfs The observation distributions.  <code>opdfs.get(i)</code>
	 *        is the observation distribution associated with state
	 *        <code>i</code>.  The distributions are not copied.
	 */
	
	
	
	
		if (nbStates <= 0)
			throw new IllegalArgumentException("Number of states must be " +
			"positive");
		
		pi = new double[nbStates];
		a = new double[nbStates][nbStates];
		opdfs = new ArrayList<Opdf<O>>(nbStates);
		
		for (int i = 0; i < nbStates; i++)
			opdfs.add(null);
	}
	
	
	/**
	 * Returns the number of states of this HMM.
	 *
	 * @return The number of states of this HMM.
	 */
	public int nbStates()
	{
		return pi.length;
	}
	
	
	/**
	 * Returns the <i>pi</i> value associated with a given state.
	 *
	 * @param stateNb A state number such that
	 *                <code>0 &le; stateNb &lt; nbStates()</code>
	 * @return The <i>pi</i> value associated to <code>stateNb</code>.
	 */
	public double getPi(int stateNb)
	{
		return pi[stateNb];
	}
	
	
	/**
	 * Sets the <i>pi</i> value associated with a given state.
	 *
	 * @param stateNb A state number such that
	 *                <code>0 &le; stateNb &lt; nbStates()</code>.
	 * @param value The <i>pi</i> value to associate to state number
	 *              <code>stateNb</code>
	 */
	public void setPi(int stateNb, double value)
	{
		pi[stateNb] = value;
	}
	
	
	/**
	 * Returns the opdf associated with a given state.
	 *
	 * @param stateNb A state number such that
	 *                <code>0 &le; stateNb &lt; nbStates()</code>.
	 * @return The opdf associated to state <code>stateNb</code>.
	 */
	public Opdf<O> getOpdf(int stateNb)
	{
		return opdfs.get(stateNb);
	}
	
	
	/**
	 * Sets the opdf associated with a given state.
	 *
	 * @param stateNb A state number such that
	 *                <code>0 &le; stateNb &lt; nbStates()</code>.
	 * @param opdf An observation probability function.
	 */
	public void setOpdf(int stateNb, Opdf<O> opdf)
	{
		opdfs.set(stateNb, opdf);
	}
	
	
	
	/**
	 * Sets the probability associated to the transition going from
	 * state <i>i</i> to state <i>j</i> (<i>A<sub>i,j</sub></i>).
	 *
	 * @param i The first state number such that
	 *        <code>0 &le; i &lt; nbStates()</code>.
	 * @param j The second state number such that
	 *        <code>0 &le; j &lt; nbStates()</code>.
	 * @param value The value of <i>A<sub>i,j</sub></i>.
	 */
	public void setAij(int i, int j, double value)
	{
		a[i][j] = value;
	}
	
	
	/**
	 * Returns an array containing the most likely state sequence matching an
	 * observation sequence given this HMM.  This sequence <code>I</code>
	 * maximizes the probability of <code>P[I|O,Model]</code> where
	 * <code>O</code> is the observation sequence and <code>Model</code> this
	 * HMM model.
	 *
	 * @param oseq A non-empty observation sequence.
	 * @return An array containing the most likely sequence of state numbers.
	 *         This array can be modified.
	 */
	
	
}
