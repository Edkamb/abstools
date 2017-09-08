/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype;

import java.util.HashMap;

import abs.frontend.ast.AddClassModifier;
import abs.frontend.ast.AddMethodModifier;
import abs.frontend.ast.DeltaDecl;
import abs.frontend.ast.DeltaTraitModifier;
import abs.frontend.ast.List;
import abs.frontend.ast.MethodImpl;
import abs.frontend.ast.MethodModifier;
import abs.frontend.ast.MethodSig;
import abs.frontend.ast.Modifier;
import abs.frontend.ast.ModifyClassModifier;
import abs.frontend.ast.ModifyMethodModifier;
import abs.frontend.ast.ModuleModifier;
import abs.frontend.ast.RemoveClassModifier;
import abs.frontend.ast.RemoveMethodModifier;

public class DeltaType {
    public DeltaType(DeltaDecl deltaDecl, HashMap<String, TraitType> tTypes) {
        deltaDecl.collapseTraitModifiers();
        
        for (ModuleModifier modMod : deltaDecl.getModuleModifiers()) {
            if(modMod instanceof ModifyClassModifier){
                ModifyClassModifier classMod = (ModifyClassModifier)modMod;
                preType = new DeltaInnerPreType(classMod.getClassDecl(), true);
                postType = new DeltaInnerPreType(classMod.getClassDecl(), true);                
                handleAllModifiers(classMod.getModifiers());
            }
            if(modMod instanceof AddClassModifier){
                AddClassModifier classMod = (AddClassModifier)modMod;
                preType = new DeltaInnerAbsType(classMod.getClassDecl().getName());
                postType = new DeltaInnerPreType(classMod.getClassDecl(), false);                
               // handleAllModifiers(classMod.get);
            }
            if(modMod instanceof RemoveClassModifier){
                RemoveClassModifier classMod = (RemoveClassModifier)modMod;
                preType = new DeltaInnerPreType(classMod.getName());
                postType = new DeltaInnerAbsType(classMod.getName());                
               // handleAllModifiers(classMod.get);
            }
        }
    }
    
    private void handleAllModifiers(List<Modifier> modifiers) {                
        for (Modifier classSubMod : modifiers) {
            if(classSubMod instanceof DeltaTraitModifier){
                DeltaTraitModifier traitMod = (DeltaTraitModifier)classSubMod;
                handleMethodModifier(traitMod.getMethodModifier());
           } else if (classSubMod instanceof MethodModifier){
               handleMethodModifier((MethodModifier) classSubMod);
           }
        }        
    }

    public void handleMethodModifier(MethodModifier classSubMod){ 
        if(classSubMod instanceof RemoveMethodModifier){
            for (MethodSig sig : ((RemoveMethodModifier)classSubMod).getMethodSigs()) {
                TraitInnerAbsType abs = new TraitInnerAbsType(sig);
                preType.add(abs);
                postType.add(abs);
            }
        } else if(classSubMod instanceof AddMethodModifier){
            MethodImpl impl = ((AddMethodModifier)classSubMod).getMethodImpl();
            TraitInnerAbsType abs = new TraitInnerAbsType(impl.getMethodSig());
            TraitInnerPreType pre = new TraitInnerPreType(impl, true);
            preType.add(abs);
            postType.add(pre);
            postType.add(abs);
        } else if(classSubMod instanceof ModifyMethodModifier){
            MethodImpl impl = ((ModifyMethodModifier)classSubMod).getMethodImpl();
            TraitInnerPreType pre = new TraitInnerPreType(impl, true);
            TraitInnerPreType post = new TraitInnerPreType(impl, false);
            preType.add(pre);
            postType.add(post);
        } 
        
    }
    private DeltaInnerType preType;  
    private DeltaInnerType postType; 
    
    @Override
    public String toString() {
        return preType + "\n\t-->\n  " + postType;
    }
}
