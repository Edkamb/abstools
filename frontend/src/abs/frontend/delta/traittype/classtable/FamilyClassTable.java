/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved. 
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.delta.traittype.classtable;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import abs.frontend.ast.AddClassModifier;
import abs.frontend.ast.AddFieldModifier;
import abs.frontend.ast.AddMethodModifier;
import abs.frontend.ast.ClassDecl;
import abs.frontend.ast.Decl;
import abs.frontend.ast.DeltaDecl;
import abs.frontend.ast.DeltaTraitModifier;
import abs.frontend.ast.MethodSig;
import abs.frontend.ast.Model;
import abs.frontend.ast.Modifier;
import abs.frontend.ast.ModifyClassModifier;
import abs.frontend.ast.ModifyMethodModifier;
import abs.frontend.ast.ModuleModifier;
import abs.frontend.ast.RemoveFieldModifier;
import abs.frontend.ast.RemoveMethodModifier;

public class FamilyClassTable {
    private final LinkedHashMap<String, ClassEntry> entries = new LinkedHashMap<>();

    public FamilyClassTable(Model m) {
        //collect all class decl
        for (Decl decl : m.getDecls()) {
            if(decl instanceof ClassDecl){
                ClassDecl cDecl = (ClassDecl)decl;
                if(cDecl.getModuleDecl().getName().startsWith("ABS")) continue;
                if(entries.containsKey(cDecl.getName())){
                    System.err.println("Cannot build family table: "+cDecl.getName() +" defined twice in core code");
                } else {
                    entries.put(cDecl.getModuleDecl().getName()+"."+cDecl.getName(), 
                                new ClassEntry(cDecl));
                }
            }
        }
        
        //collect all class modifications in delta
        for (DeltaDecl decl : m.getDeltaDecls()) {
           // String nameOfMod = decl.getDeltaAccesss().toString();
            for (ModuleModifier mMod : decl.getModuleModifiers()) {
                String nameOfMod = mMod.getModuleName();
                if(mMod instanceof AddClassModifier){
                    AddClassModifier cMod = (AddClassModifier)mMod;
                    String name = cMod.getName();
                    name = (name.contains(".")? "" : nameOfMod+".")+name;
                    if(entries.containsKey(name)){
                        entries.get(name).enhance(cMod.getClassDecl());
                    }else{
                        entries.put(name, new ClassEntry(cMod.getClassDecl()));
                    }
                } else if(mMod instanceof ModifyClassModifier){
                    ModifyClassModifier cMod = (ModifyClassModifier)mMod;
                    String name = cMod.getName();
                    name = (name.contains(".")? "" : nameOfMod+".")+name;
                    if(!entries.containsKey(name)){
                        entries.put(name, new ClassEntry(name));
                    }
                    ClassEntry ce = entries.get(name);
                    for (Modifier mod : cMod.getModifierList()) handleModifier(mod, ce);
                    
                }
            }
        }
        System.out.println(this);
    }
    
    private void handleModifier(Modifier mod, ClassEntry ce){
        if(mod instanceof AddMethodModifier){
            AddMethodModifier aMod = (AddMethodModifier)mod;
            ce.enhance(aMod.getMethodImpl());
        } else if (mod instanceof ModifyMethodModifier){
            ModifyMethodModifier aMod = (ModifyMethodModifier)mod;
            ce.enhance(aMod.getMethodImpl());
        } else if (mod instanceof RemoveMethodModifier){
            RemoveMethodModifier aMod = (RemoveMethodModifier)mod;
            for (MethodSig sig : aMod.getMethodSigList()) {
                ce.enhance(sig);
            }
        } else if (mod instanceof AddFieldModifier){
            AddFieldModifier aMod = (AddFieldModifier)mod;
            ce.enhance(aMod.getFieldDecl());
        } else if (mod instanceof RemoveFieldModifier){
            RemoveFieldModifier aMod = (RemoveFieldModifier)mod;
            ce.enhance(aMod.getFieldDecl());
        } else if(mod instanceof DeltaTraitModifier){
            DeltaTraitModifier aMod = (DeltaTraitModifier)mod;
            handleModifier(aMod.getMethodModifier(),ce);
        }
    }
    

    @Override
    public String toString() {
        StringBuilder bb = new StringBuilder("FamilyClassTable [\n");
        for (Entry<String, ClassEntry> entry : entries.entrySet()) {
            bb.append("\t"+entry.getKey()+" : "+entry.getValue().toString()+"\n");
        }
        bb.append("]");
        return bb.toString();
    }

}
