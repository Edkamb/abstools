/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

//TODO: better solution for ABS singleton
/**
 *   An inner type of a trait type: an absent/present method flattening type
 */
public class TraitInnerType {
    public final static TraitInnerType ABS = new TraitInnerType();
    public String toString(){ return "ABS"; }
}
