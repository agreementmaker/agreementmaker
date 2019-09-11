package am.api.matching;

/**
 * <p>
 * A matching result contains three similarity matrices, one for each kind of
 * entity in the ontology.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> Not all matching algorithms can produce every kind of similarity
 * matrix. Algorithms that cannot compute similarities for a specific entity
 * type should return a null matrix for that type.
 * </p>
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 * 
 */
public interface MatchingResult {

	/**
	 * @return the computed classes similarity matrix, or null if the matching
	 *         algorithm cannot compute class similarities.
	 */
	public SimilarityMatrix getClasses();

	/**
	 * @return the computed properties similarity matrix, or null if the
	 *         matching algorithm cannot compute property similarities.
	 */
	public SimilarityMatrix getProperties();

	/**
	 * @return the computed instances similarity matrix, or null if the matching
	 *         algorithm cannot compute instance similarities.
	 */
	public SimilarityMatrix getInstances();
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
