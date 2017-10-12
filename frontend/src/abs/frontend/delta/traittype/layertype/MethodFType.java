/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.layertype;

import abs.frontend.analyser.SemanticConditionList;
import abs.frontend.ast.ASTNode;
import abs.frontend.ast.MethodImpl;
import abs.frontend.ast.MethodSig;
import abs.frontend.ast.OriginalCall;
import abs.frontend.delta.traittype.dependency.DependencyList;

public class MethodFType extends LayerTwoType {
    private MethodSig sig;
    private boolean usesOrig = false;
    @Override
    public String toString() {
        return "sig=" + sig + ", usesOrig=" + usesOrig + ", deps=" + deps;
    }
    public MethodFType(MethodImpl met, boolean empty) {
        if(!empty)traverse(met, deps);
        this.sig = met.getMethodSig();
        SemanticConditionList s = new SemanticConditionList();
        try {
            met.getBlock().partialTypeCheck(s,deps);
            usesOrig = hasOriginal(met);
            if(usesOrig) System.out.println("WARNING: Original call detected");
        } catch (Exception e){
      //      System.out.println(e);
        } finally {
      //      System.out.println("semantic: ");
      //      System.out.println(s);
      //      System.out.println(" ############## generated: "+ deps);
        }
    }

    private static boolean hasOriginal(ASTNode<?> node){
        if(node instanceof OriginalCall) return true;
        for(int i = 0; i < node.getNumChild(); i++){
            if(hasOriginal(node.getChild(i))) return true;
        }
        return false;
    }
    
}


