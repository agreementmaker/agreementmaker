package am.api.ontology;

import java.util.List;

/**
 * <p>
 * This interface represents an Ontology that is loaded in the AgreementMaker
 * system. It's meant to be a thin layer over an existing OWL library (Jena, OWL
 * API, etc.) and it encapsulates the ontology object from the specific library.
 * </p>
 * 
 * <p>
 * The methods defined in this interface are meant to be common across all the
 * different underlying implementations.
 * </p>
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 * 
 * @param <O>
 *            The enclosed ontology type.
 * @param <C>
 *            The type of enclosed ontology class objects.
 * @param <P>
 *            The type of enclosed ontology property objects.
 * @param <I>
 *            The type of enclosed ontology instance objects.
 */
public interface AMOntology<O, C extends OntoClass<?>, P extends OntoProperty<?>, I extends OntoInstance<?>> {

	/**
	 * @return The enclosed ontology object. This method is provided so that the
	 *         full capabilities of the underlying library can accessed.
	 */
	public O getInner();
	
	/**
	 * @return A list of ontology classes defined in this ontology.
	 */
	public List<C> getClasses();
	
	/**
	 * @return A list of the ontology properties defined in this ontology.
	 */
	public List<P> getProperties();
	
	/**
	 * @return A list of the ontology instances defined in this ontology.
	 */
	public List<I> getInstances();
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
