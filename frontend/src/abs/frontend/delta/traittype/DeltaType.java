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
import abs.frontend.ast.InterfaceTypeUse;
import abs.frontend.ast.List;
import abs.frontend.ast.MethodImpl;
import abs.frontend.ast.MethodModifier;
import abs.frontend.ast.MethodSig;
import abs.frontend.ast.Modifier;
import abs.frontend.ast.ModifyClassModifier;
import abs.frontend.ast.ModifyMethodModifier;
import abs.frontend.ast.ModuleModifier;
import abs.frontend.ast.ParamDecl;
import abs.frontend.ast.RemoveClassModifier;
import abs.frontend.ast.RemoveMethodModifier;
import abs.frontend.ast.TraitExpr;
import abs.frontend.ast.TraitNameExpr;
import abs.frontend.ast.TraitSetExpr;
import abs.frontend.delta.traittype.dependency.HasMethodDep;
import abs.frontend.delta.traittype.dependency.TypeDep;

/**
 *   The type of a delta.
 *   Its inner types describe modified classes.
 */

public class DeltaType {
    public DeltaType(DeltaDecl deltaDecl, HashMap<String, TraitType> tTypes) {
        //we have to do this anyway
        deltaDecl.collapseTraitModifiers();
        
        for (ModuleModifier modMod : deltaDecl.getModuleModifiers()) {
            if(modMod instanceof ModifyClassModifier){
                ModifyClassModifier classMod = (ModifyClassModifier)modMod;
                preType = new DeltaInnerPreType(classMod.targetClassName());
                postType = new DeltaInnerPreType(classMod.targetClassName(), (DeltaInnerPreType)preType);        //no class decl
                //postType = new DeltaInnerPreType(classMod.getClassDecl(), true);                
                handleAllModifiers(classMod.getModifiers());
            }
            if(modMod instanceof AddClassModifier){
                AddClassModifier classMod = (AddClassModifier)modMod;
                preType = new DeltaInnerAbsType(classMod.getClassDecl().getName());
                DeltaInnerPreType pp = new DeltaInnerPreType(classMod.getClassDecl());   
                for (InterfaceTypeUse impl : classMod.getClassDecl().getImplementedInterfaceUseList()) {
                    pp.addDep(new TypeDep(impl.getName()));
                }
                for (ParamDecl param : classMod.getClassDecl().getParams()) {
                    pp.addDep(new TypeDep(param.getChild(0).toString()));                    
                }
                postType = pp;
            }
            if(modMod instanceof RemoveClassModifier){
                RemoveClassModifier classMod = (RemoveClassModifier)modMod;
                preType = new DeltaInnerPreType(classMod.getName());
                postType = new DeltaInnerAbsType(classMod.getName());           
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
                TraitInnerPreType pre = new TraitInnerPreType(sig);
                preType.add(pre);
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
            if(impl != null){
                TraitInnerPreType pre = new TraitInnerPreType(impl, true);
                TraitInnerPreType post = new TraitInnerPreType(impl, false);
                preType.add(pre);
                postType.add(post);
            } 
        } 
        
    }
    private DeltaInnerType preType;  
    private DeltaInnerType postType; 
    
    @Override
    public String toString() {
        return preType + "\n\t-->\n  " + postType;
    }
}
