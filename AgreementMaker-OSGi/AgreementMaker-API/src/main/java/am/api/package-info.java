/**
 * <p>This bundle represents the AgreementMaker API.</p>  
 * 
 * <p>
 * This API takes some inspiration from the <a href="http://alignapi.gforge.inria.fr">Alignment API</a>,
 * and also the <a href="http://semanticmatching.org">S-Match system</a>.  Adapting one of these APIs 
 * was considered, however none of them seemed to fit the current vision of AgreementMaker.
 * </p>
 * 
 * <p>
 * Sub-package description:
 * <ul>
 * <li><u>ontology</u> - API interfaces related to representing ontologies.</li>
 * <li><u>alignment</u> - API interfaces related to representing an alignment.</li>
 * <li><u>matching</u> - API interfaces related to matching algorithms (algorithms which compute similarity values).</li>
 * <li><u>selection</u> - API interfaces related to selection algorithms (algorithms which produce an alignment from a similarity matrix).</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Due to the heterogeneity in terminology between the APIs, below will be presented a mapping 
 * between the terms of the different APIs, to the best of our ability.
 * </p> 
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 */
package am.api;

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