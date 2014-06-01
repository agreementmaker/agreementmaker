package am.api.matching;


/**
 * <p>
 * An interface to store similarities computed by a
 * {@link am.api.matching.MatchingAlgorithm matching algorithm}. Entities in the
 * source ontology are along the rows, and entities in the target ontology are
 * along the columns.
 * </p>
 * 
 * <p>
 * For example, in the matrix below, the similarity between source concept
 * <code>sC</code> and target concept <code>tC</code> is <code>1.0</code>.
 * <code>sA<code> has index of 0, <code>sB</code> has index of 1, and so on.
 * Likewise, <code>tA</code> has index 0, <code>tB</code> has index 1, and so
 * on. To get the similarity between <code>sC</code> and <code>tC</code>
 * directly, you can call <code>getSimilarity(2,2)</code> or
 * <code>getSimilarity(sC.getIndex(), tC.getIndex())</code>.
 * </p>
 * 
 * <pre>
 *                  tA  tB  tC   ...
 *                  ___ ___ ___
 *              sA |0.1|0.2|0.0|    
 *              sB |0.0|0.0|0.0| ...
 *              sC |0.0|0.3|1.0|
 *               .      .
 *               .      .
 *               .      .
 * </pre>
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 * 
 */
public interface SimilarityMatrix {
	
	/**
	 * @return the number of rows in this matrix.
	 */
	public int getRowCount();
	
	/**
	 * @return the number of columns in this matrix.
	 */
	public int getColCount();
	
	/**
	 * This method provides direct access to the similarities stored in the
	 * similarity matrix.
	 * 
	 * @param sourceIndex ranges from 0 to {@link #getRowCount()} - 1
	 * @param targetIndex ranges from 0 to {@link #getColCount()} - 1
	 * @return A similarity value from 0 to 1.0
	 */
	public double getSimilarity(int sourceIndex, int targetIndex);
	
	/**
	 * Set the similarity values in the matrix directly. 
	 * @param value must range from 0 to 1.0
	 */
	public void setSimilarity(int sourceIndex, int targetIndex, double value);
}

/*
 * Copyright (C) Cosmin Stroe, University of Illinois at Chicago, 2013
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */