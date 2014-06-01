/**
 * <p>
 * This package contains all the interfaces related to describing 
 * an alignment between two ontologies.
 * </p>
 * 
 * <p>
 * Some terminology:
 * <ul>
 * <li>When two entities are matched, a {@link am.api.alignment.Correspondence correspondence} is created between them.  
 *     The matched entities can be two classes, two properties, or two instances.</li>
 * <li>A {@link am.api.alignment.Correspondence correspondence} describes how much a 
 *     {@link am.api.alignment.Correspondence#getRelation() relation} holds between the matched entities.  
 *     The strength of the relation is modeled with a 
 *     {@link am.api.alignment.Correspondence#getValue() value} from 0 to 1.</li>
 * <li>An {@link am.api.alignment.OntoAlignment alignment} is a collection of correspondences between concepts of two ontologies.</li>
 * </ul>
 * </p>
 */
package am.api.alignment;

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