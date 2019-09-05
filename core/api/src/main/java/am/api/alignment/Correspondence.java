package am.api.alignment;

import am.api.ontology.OntoEntity;

/**
 * <p>
 * This interface represents a single correspondence between two entities
 * defined in an ontology, the first which we name the <i>source</i> entity and
 * the second we name the <i>target</i> entity. The entities can be classes,
 * properties, or instances.
 * </p>
 * 
 * <p>
 * The correspondence describes the strength of a
 * {@link am.api.alignment.SemanticRelation relation} that holds between the two
 * entities. The strength of the relation is modeled as a similarity value, a
 * number from 0 to 1, retrieved by {@link #getValue()}.
 * </p>
 * 
 * <p>
 * For relations that have a direction (such as {@link am.api.alignment.SemanticRelation#SUBCLASSOF subClassOf}) 
 * the direction is defined to be from the source entity to the target entity.
 * </p>
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 */
public interface Correspondence {
	
	/**
	 * @return The kinds of concepts we are relating (classes, properties, instances).
	 */
	public EntityType getType();
	
	/**
	 * @return The source entity of this correspondence.
	 */
	public OntoEntity getSource();
	
	/**
	 * @return The target entity of this correspondence.
	 */
	public OntoEntity getTarget();
	
	/**
	 * @return The relation we are describing with this correspondence.
	 */
	public SemanticRelation getRelation();
	
	/**
	 * @return A number from 0 to 1 which conveys how strong the relationship is.
	 */
	public double getValue();
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
