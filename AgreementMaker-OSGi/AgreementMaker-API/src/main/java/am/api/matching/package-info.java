/**
 * <p>
 * This package contains all interfaces related to matching algorithms.
 * </p>
 * 
 * <p>
 * A {@link am.api.matching.MatchingAlgorithm matching algorithm} takes as input two ontologies and returns a 
 * {@link am.api.matching.MatchingResult matching result}, which contains a
 * {@link am.api.matching.SimilarityMatrix similarity matrix} for each type of entity (class, property, and instance).
 * </p>
 */
package am.api.matching;

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