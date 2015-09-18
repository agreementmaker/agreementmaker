package am.api.ontology;

/**
 * <p>
 * This interface represents an ontology Class. It's meant to be a thin layer
 * over an underlying library (Jena, OWL API, etc.).
 * </p>
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 * 
 * @param <C> The underlying object that represents an ontology class.
 */
public interface OntoClass<C> extends OntoEntity {

	/**
	 * @return The enclosed ontology class object. This method is provided so
	 *         that the full capabilities of the underlying library can
	 *         accessed.
	 */
	public C getInner();
	
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
