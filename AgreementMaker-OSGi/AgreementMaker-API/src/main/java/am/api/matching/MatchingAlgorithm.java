package am.api.matching;

/**
 * <p>
 * A matching algorithm takes as input two {@link am.api.ontology.AMOntology ontologies} and 
 * produces a {@link am.api.matching.MatchingResult matching result}.
 * </p>
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public interface MatchingAlgorithm {

	/**
	 * The main work of the matching algorithm is expected to be done here. It
	 * is a distinct call because it is expected to take a considerable amount
	 * of time, therefore should be run in a separate thread, and monitored.
	 */
	public void match();
	
	/**
	 * @return The result of the matching algorithm, after the {@link #match()}
	 *         method has run.
	 */
	public MatchingResult getResult();
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
