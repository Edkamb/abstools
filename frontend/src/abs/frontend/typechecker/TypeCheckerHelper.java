/**
 * Copyright (c) 2009-2011, The HATS Consortium. All rights reserved.
 * This file is licensed under the terms of the Modified BSD License.
 */
package abs.frontend.typechecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import abs.common.Constants;
import abs.frontend.analyser.ErrorMessage;
import abs.frontend.analyser.SemanticConditionList;
import abs.frontend.analyser.SemanticError;
import abs.frontend.analyser.SemanticWarning;
import abs.frontend.analyser.TypeError;
import abs.frontend.ast.ASTNode;
import abs.frontend.ast.AmbiguousDecl;
import abs.frontend.ast.AttrAssignment;
import abs.frontend.ast.Binary;
import abs.frontend.ast.Call;
import abs.frontend.ast.CompilationUnit;
import abs.frontend.ast.Const;
import abs.frontend.ast.ConstructorPattern;
import abs.frontend.ast.DataConstructor;
import abs.frontend.ast.DataConstructorExp;
import abs.frontend.ast.DataTypeDecl;
import abs.frontend.ast.DataTypeUse;
import abs.frontend.ast.Decl;
import abs.frontend.ast.DeltaClause;
import abs.frontend.ast.DeltaDecl;
import abs.frontend.ast.DeltaID;
import abs.frontend.ast.DeltaParamDecl;
import abs.frontend.ast.Deltaparam;
import abs.frontend.ast.Deltaspec;
import abs.frontend.ast.ExceptionDecl;
import abs.frontend.ast.Exp;
import abs.frontend.ast.Export;
import abs.frontend.ast.Feature;
import abs.frontend.ast.FieldUse;
import abs.frontend.ast.FnApp;
import abs.frontend.ast.FromExport;
import abs.frontend.ast.FromImport;
import abs.frontend.ast.GetExp;
import abs.frontend.ast.HasActualParams;
import abs.frontend.ast.HasParams;
import abs.frontend.ast.HasType;
import abs.frontend.ast.Import;
import abs.frontend.ast.List;
import abs.frontend.ast.MethodSig;
import abs.frontend.ast.Model;
import abs.frontend.ast.ModuleDecl;
import abs.frontend.ast.Name;
import abs.frontend.ast.NamedExport;
import abs.frontend.ast.NamedImport;
import abs.frontend.ast.ParamDecl;
import abs.frontend.ast.ParametricDataTypeDecl;
import abs.frontend.ast.ParametricDataTypeUse;
import abs.frontend.ast.ParametricFunctionDecl;
import abs.frontend.ast.Pattern;
import abs.frontend.ast.ProductDecl;
import abs.frontend.ast.PureExp;
import abs.frontend.ast.StarExport;
import abs.frontend.ast.StarImport;
import abs.frontend.ast.TypeSynDecl;
import abs.frontend.ast.TypeUse;
import abs.frontend.ast.UpdateDecl;
import abs.frontend.ast.Value;
import abs.frontend.ast.VarDeclStmt;
import abs.frontend.ast.VarOrFieldDecl;
import abs.frontend.ast.VarOrFieldUse;
import abs.frontend.delta.traittype.dependency.DependencyList;
import abs.frontend.delta.traittype.dependency.InnerSubTypeDep;
import abs.frontend.delta.traittype.dependency.SubTypeDep;
import abs.frontend.delta.traittype.dependency.TypeIdentity;
import abs.frontend.delta.traittype.dependency.TypeOfDependency;
import abs.frontend.delta.traittype.dependency.TypeOfLocation;
import abs.frontend.delta.traittype.dependency.TypeOfMethod;
import abs.frontend.parser.SourcePosition;

public class TypeCheckerHelper {

    // Warn about using constructors that should not have been exported from
    // their module (used in DataConstructorExp.typeCheck() defined in
    // TypeChecker.jadd)
    public static Set<String> deprecatedConstructors
        = ImmutableSet.of("ABS.StdLib.Set", "ABS.StdLib.EmptySet", "ABS.StdLib.Insert",
                          "ABS.StdLib.Map", "ABS.StdLib.EmptyMap", "ABS.StdLib.InsertAssoc");



    public static void typeCheck(ConstructorPattern p, SemanticConditionList e, Type t) {
        DataConstructor c = p.getDataConstructor();
        if (c == null) {
            e.add(new SemanticError(p, ErrorMessage.CONSTRUCTOR_NOT_RESOLVABLE, p.getConstructor()));
            return;
        }
        String cname = c.qualifiedName();
        if (deprecatedConstructors.contains(cname)
            && !(cname.startsWith(p.getModuleDecl().getName()))) {
            e.add(new SemanticWarning(p, ErrorMessage.DEPRECATED_CONSTRUCTOR, c.qualifiedName()));
        }

        if (c.getNumConstructorArg() != p.getNumParam()) {
            e.add(new TypeError(p, ErrorMessage.WRONG_NUMBER_OF_ARGS, c.getNumConstructorArg(), p.getNumParam()));
            return;
        }

        assert t.isDataType() || t.isExceptionType() : t; // TODO: more instances of this? Maybe exception should imply datatype?

        if (!t.getDecl().equals(c.getDataTypeDecl())) {
            e.add(new TypeError(p, ErrorMessage.WRONG_CONSTRUCTOR, t.toString(), p.getConstructor()));
        }

        Type myType = p.getType();

        if (!(myType instanceof DataTypeType))
            return;

        if (!(t instanceof DataTypeType)) {
            e.add(new TypeError(p, ErrorMessage.TYPE_MISMATCH, myType, t));
            return;
        }

        DataTypeType myDType = (DataTypeType) myType;
        DataTypeType otherType = (DataTypeType) t;
        if (!myDType.getDecl().equals(otherType.getDecl())) {
            e.add(new TypeError(p, ErrorMessage.TYPE_MISMATCH, myDType, t));
            return;
        }

        typeCheckMatchingParamsPattern(e, p, c);
    }

    public static void checkAssignment(SemanticConditionList l, ASTNode<?> n, Type lht, Exp rhte ) {
        Type te = rhte.getType();
        if (!te.isAssignableTo(lht)) {
            l.add(new TypeError(n, ErrorMessage.CANNOT_ASSIGN, te, lht));
        }
    }

    //TODO: handle case where the future is not directly accessed
    public static void checkInitAssignment(SemanticConditionList l, ASTNode<?> n, Type lht, Exp rhte, DependencyList deps) {
        Type te = rhte.getType();
        if (!te.isAssignableTo(lht)) {
            if(rhte instanceof GetExp){
                GetExp inner = (GetExp) rhte;
                if(inner.getType().isUnknownType()){
                    //System.out.println("dep: "+inner.getPureExp()+" must be a future with inner type "+lht);
                    deps.add(new InnerSubTypeDep(new TypeOfLocation(inner.getPureExp().toString()), new TypeIdentity(lht.toString())));
                }
            } if(rhte instanceof Call){
                Call call = (Call)rhte;
                TypeOfDependency tt = (lht.isUnknownType()? TypeOfDependency.FUTURE : new TypeIdentity(lht));
                deps.add(new InnerSubTypeDep(new TypeOfMethod(call.getMethod()),tt));
            }
        }
    }

    public static void partiallyCheckAssignment(SemanticConditionList l, ASTNode<?> n, VarOrFieldUse lhs, Type lht, Exp rhs, Type rht, DependencyList deps) {
        
        if(rhs instanceof VarOrFieldUse && lht.isUnknownType()){
            deps.add(new SubTypeDep(new TypeOfLocation(lhs.toString()), new TypeOfLocation(rhs.toString())));
        } else if(rhs instanceof VarOrFieldUse && !lht.isUnknownType()){
            deps.add(new SubTypeDep(new TypeOfLocation(rhs.toString()), new TypeIdentity(lht)));
        } else if(rhs instanceof GetExp && lht.isUnknownType() && rht.isUnknownType()){
           // System.out.println("dep: "+((GetExp)rhs).getPureExp()+ " must have future type with the inner type of "+lhs);
            deps.add(new InnerSubTypeDep(new TypeIdentity(lhs.toString()), new TypeOfLocation(((GetExp)rhs).getPureExp().toString())  ));
        } else if(rhs instanceof GetExp && !lht.isUnknownType() && rht.isUnknownType()){
            deps.add(new InnerSubTypeDep(new TypeOfLocation(((GetExp)rhs).getPureExp().toString()), TypeOfDependency.futureFor(lht.toString())));
        } else if(rhs instanceof GetExp && lht.isUnknownType() && !rht.isUnknownType()){
            //System.out.println("dep: "+lhs+ " subtype of inner type of"+rht);
            deps.add(new InnerSubTypeDep(new TypeOfLocation(lhs.toString()), new TypeIdentity(rht.toString())));
        }  else if(lht != null && !lht.isUnknownType()){
            //System.out.println("dep: "+lhs+" subtype of "+rht);            
            deps.add(new SubTypeDep(new TypeOfLocation(lhs.toString()), new TypeIdentity(rht)));
        } else if(lhs != null && rhs instanceof Call){
            VarOrFieldUse use = (VarOrFieldUse)lhs;
            Call call = (Call)rhs;
            VarOrFieldDecl decl = use.getDecl();
            deps.add(new SubTypeDep(new TypeOfLocation(use.toString()), TypeOfDependency.FUTURE));
            if(decl == null) deps.add(new InnerSubTypeDep(new TypeOfMethod(call.getMethod()),new TypeOfLocation(use.toString())));
            else             deps.add(new SubTypeDep(new TypeOfMethod(call.getMethod()),new TypeIdentity(decl.getChild(0).toString())));
        } else if(rht != null && !rht.isAssignableTo(lht)){
            l.add(new TypeError(n, ErrorMessage.CANNOT_ASSIGN, rht, lht));            
        }
    }

    public static void typeCheckParamList(SemanticConditionList l, HasParams params) {
        Set<String> names = new HashSet<String>();
        for (ParamDecl d : params.getParams()) {
            if (!names.add(d.getName())) {
                l.add(new TypeError(d, ErrorMessage.DUPLICATE_PARAM_NAME, d.getName()));
            }
            d.typeCheck(l);
        }
    }

    public static void typeCheckRunMethodSig(SemanticConditionList l, MethodSig m) {
        if (m.getNumParam() > 0) {
            l.add(new TypeError(m, ErrorMessage.RUN_METHOD_WRONG_NUM_PARAMS, m.getNumParam()));
        }
        if (!m.getReturnType().getType().isUnitType()) {
            l.add(new TypeError(m, ErrorMessage.RUN_METHOD_WRONG_RETURN_TYPE,
                                m.getReturnType().getType().toString()));
        }
    }

    public static void typeCheckMatchingParams(SemanticConditionList l, DataConstructorExp n, DataConstructor c) {
        assert n.getDecl() == c;
        final Map<TypeParameter, Type> binding = n.getTypeParamBinding(n, c);
        typeCheckEqual(l, n, c.applyBindings(binding));
    }

    private static void typeCheckMatchingParamsPattern(SemanticConditionList l, ConstructorPattern n, DataConstructor decl) {
        Map<TypeParameter, Type> binding = decl.getTypeParamBinding(n, n.getTypes());
        java.util.List<Type> types = decl.applyBindings(binding);
        typeCheckEqualPattern(l, n, types);
    }

    public static java.util.List<Type> applyBindings(Map<TypeParameter, Type> binding, java.util.List<Type> types) {
        ArrayList<Type> res = new ArrayList<Type>(types.size());
        for (Type t : types) {
            res.add(t.applyBinding(binding));
        }
        return res;
    }

    public static void typeCheckMatchingParams(SemanticConditionList l, FnApp n, ParametricFunctionDecl decl) {
        Map<TypeParameter, Type> binding = n.getTypeParamBindingFromParamDecl(decl);
        java.util.List<Type> types = decl.applyBindings(binding);
        typeCheckEqual(l, n, types);
    }

    public static void typeCheckEqual(SemanticConditionList l, ASTNode<?> n, java.util.List<Type> params) {
        List<PureExp> args = ((HasActualParams)n).getParams();
        if (params.size() != args.getNumChild()) {
            l.add(new TypeError(n, ErrorMessage.WRONG_NUMBER_OF_ARGS, params.size(), args.getNumChild()));
        } else {
            for (int i = 0; i < params.size(); i++) {
                Type argType = params.get(i);
                PureExp exp = args.getChild(i);
                exp.typeCheck(l);
                Type expType = exp.getType();
                if (!expType.isAssignableTo(argType)) {
                    l.add(new TypeError(n, ErrorMessage.TYPE_MISMATCH, exp.getType(), argType));
                }
            }
        }
    }

    private static void typeCheckEqualPattern(SemanticConditionList l, ConstructorPattern n, java.util.List<Type> params) {
        List<Pattern> args = n.getParams();
        if (params.size() != args .getNumChild()) {
            l.add(new TypeError(n, ErrorMessage.WRONG_NUMBER_OF_ARGS, params.size(), args.getNumChild()));
        } else {
            for (int i = 0; i < params.size(); i++) {
                Type argType = params.get(i);
                Pattern exp = args.getChild(i);
                exp.typeCheck(l, argType);
            }
        }
    }

    public static void typeCheckDeltaClause(DeltaClause clause, Map<String,DeltaDecl> deltaNames, Set<String> definedFeatures, SemanticConditionList e) {

        /* Does the delta exist? */
        final Deltaspec spec = clause.getDeltaspec();
        if (! deltaNames.containsKey(spec.getDeltaID()))
            e.add(new TypeError(spec, ErrorMessage.NAME_NOT_RESOLVABLE, spec.getDeltaID()));
        else {
            DeltaDecl dd = deltaNames.get(spec.getDeltaID());
            if (dd.getNumParam() != spec.getNumDeltaparam()) {
                e.add(new TypeError(spec, ErrorMessage.WRONG_NUMBER_OF_ARGS,dd.getNumParam(),spec.getNumDeltaparam()));
            } else {
                for (int i=0; i<dd.getNumParam(); i++) {
                    DeltaParamDecl formal = dd.getParam(i);
                    Deltaparam actual = spec.getDeltaparam(i);
                    // TODO: W00t?!
                    if (actual instanceof Const) {
                        Value a = ((Const) actual).getValue();
                        if (! formal.accepts(a)) {
                            e.add(new TypeError(a, ErrorMessage.CANNOT_ASSIGN, a.getName(), formal.getType().getSimpleName()));
                        }
                    }
                }
            }
        }

        /* Do the referenced features exist? */
        if (clause.hasAppCond()) {
            clause.getAppCond().typeCheck(definedFeatures, e);
        }
        if (clause.hasFromAppCond()) {
            clause.getFromAppCond().typeCheck(definedFeatures, e);
        }

        /* What about deltas mentioned in the 'after' clause? */
        for (DeltaID did : clause.getAfterDeltaIDs()) {
            if (! deltaNames.containsKey(did.getName())) {
                e.add(new TypeError(did, ErrorMessage.NAME_NOT_RESOLVABLE, did.getName()));
            }
        }
    }

    public static void typeCheckProductDecl(ProductDecl prod,
            Map<String,Feature> featureNames,
            Set<String> prodNames,
            Map<String,DeltaDecl> deltaNames,
            Set<String> updateNames,
            SemanticConditionList e) {
        if (featureNames != null) {
            // Do the features exist in the PL declaration (and also check feature attributes)?
            Model m = prod.getModel();
            for (Feature f : prod.getProduct().getFeatures()) {
                if (!featureNames.containsKey(f.getName()))
                    e.add(new TypeError(prod, ErrorMessage.NAME_NOT_RESOLVABLE, f.getName()));
                else {
                    Collection<DeltaClause> dcs = findDeltasForFeature(m,f);
                    for (int i = 0; i<f.getNumAttrAssignment(); i++) {
                        AttrAssignment aa = f.getAttrAssignment(i);
                        for (DeltaClause dc : dcs) {
                            DeltaDecl dd = m.findDelta(dc.getDeltaspec().getDeltaID());
                            DeltaParamDecl dp = dd.getParam(i);
                            // FIXME: we assumed here that delta
                            // parameters and feature parameters have
                            // same order, arity.  This is clearly
                            // wrong, and parameters for the delta are
                            // named with the feature.  we should find a
                            // dp with the same name as aa, and ignore
                            // any superfluous aa (the value is simply
                            // not used by this delta).
                            if (dp != null && !dp.accepts(aa.getValue())) {
                                e.add(new TypeError(aa, ErrorMessage.CANNOT_ASSIGN, aa.getValue().getName(), dp.getType().getSimpleName()));
                            }
                        }
                    }
                }
            }
        }

        // Check the right side of product expression that contains in prodNames
        Set<String> productNames = new HashSet<String>();
        prod.getProductExpr().setRightSideProductNames(productNames);
        for (String productName : productNames) {
            if (!prodNames.contains(productName)) {
                e.add(new TypeError(prod, ErrorMessage.UNDECLARED_PRODUCT, productName));
            }
        }

        // Check solution from getProduct()
        if (prod.getProduct() != null) {
            java.util.List<String> errors = prod.getModel().instantiateCSModel().checkSolutionWithErrors(
                    prod.getProduct().getSolution(),
                    prod.getModel());

            if (!errors.isEmpty()) {
                String failedConstraints = "";
                for (String s: errors)
                    failedConstraints += "\n- " + s;

                e.add(new TypeError(prod, ErrorMessage.INVALID_PRODUCT, prod.getName(), failedConstraints));
            }
        }

        Set<String> seen = new HashSet<String>();
        // FIXME: deal with reconfigurations
//        for (Reconfiguration recf : prod.getReconfigurations()) {
//            if (!seen.add(recf.getTargetProductID()))
//                e.add(new TypeError(recf, ErrorMessage.DUPLICATE_RECONFIGURATION, recf.getTargetProductID()));
//
//            // Does the reconfiguration target product exist?
//            if (! prodNames.contains(recf.getTargetProductID()))
//                e.add(new TypeError(recf, ErrorMessage.NAME_NOT_RESOLVABLE, recf.getTargetProductID()));
//            // Do the deltas used for reconfiguration exist?
//            for (DeltaID d : recf.getDeltaIDs()) {
//                if (! deltaNames.containsKey(d.getName()))
//                    e.add(new TypeError(recf, ErrorMessage.NAME_NOT_RESOLVABLE, d.getName()));
//            }
//            // Does the update used for reconfiguration exist?
//            if (! updateNames.contains(recf.getUpdateID()))
//                e.add(new TypeError(recf, ErrorMessage.NAME_NOT_RESOLVABLE, recf.getUpdateID()));
//        }
    }

    /**
     * Look for all deltas that have a particular feature in the application condition --
     * up to the boolean madness that lies within AppConds (delta D(F.x) when ~F will
     * be checked when actually trying to flatten the product, I hope.
     */
    private static Collection<DeltaClause> findDeltasForFeature(Model m, Feature f) {
        Collection<DeltaClause> dcs = new ArrayList<DeltaClause>();
        for (int i = 0; i < m.getProductLine().getNumDeltaClause(); i++) {
            DeltaClause dc = m.getProductLine().getDeltaClause(i);
            if (dc.refersTo(f)) {
                dcs.add(dc);
            }
        }
        return dcs;
    }

    public static <T extends ASTNode<?>> java.util.List<Type> getTypes(List<T> params) {
        ArrayList<Type> res = new ArrayList<Type>();
        for (ASTNode<?> u : params) {
            res.add(((HasType)u).getType());
        }
        return res;
    }

    public static void addTypeParamBinding(ASTNode<?> node, Map<TypeParameter, Type> binding, java.util.List<Type> params,
            java.util.List<Type> args) {
        if (params.size() != args.size())
            throw new TypeCheckerException(new TypeError(node, ErrorMessage.WRONG_NUMBER_OF_ARGS, params.size(),args.size()));
        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i);
            Type argType = args.get(i);
            if (argType == null)
                return;
            if (argType.isBoundedType()) {
                BoundedType bt = (BoundedType) argType;
                if (bt.hasBoundType())
                    argType = bt.getBoundType();
            }

            if (paramType.isTypeParameter()) {
                if (binding.containsKey(paramType)) {
                    Type prevArgType = binding.get(paramType);
                    if (prevArgType.isAssignableTo(argType)
                        && !argType.isAssignableTo(prevArgType))
                    {
                        // Replace, e.g., "Int" with "Rat".  If the two types
                        // do not match at all, we'll raise a type error
                        // later.
                        binding.put((TypeParameter)paramType, argType);
                    }
                } else {
                    binding.put((TypeParameter) paramType, argType);
                }
            } else if (paramType.isDataType() && argType.isDataType()) {
                DataTypeType paramdt = (DataTypeType) paramType;
                DataTypeType argdt = (DataTypeType) argType;
                if (paramdt.numTypeArgs() == argdt.numTypeArgs()) {
                    addTypeParamBinding(node, binding, paramdt.getTypeArgs(), argdt.getTypeArgs());
                }
            }
        }
    }

    static final StarImport STDLIB_IMPORT = new StarImport(Constants.STDLIB_NAME);

    public static void checkForDuplicateDecls(ModuleDecl mod, SemanticConditionList errors) {
        Map<KindedName, ResolvedName> duplicateNames = new HashMap<KindedName, ResolvedName>();
        Map<KindedName, ResolvedName> names = getVisibleNames(mod, duplicateNames);
        for (KindedName n : duplicateNames.keySet()) {
            ResolvedName rn = names.get(n);
            ResolvedName origrn = duplicateNames.get(n);
            ErrorMessage msg = null;
            String location = "";
            Decl decl = null;
            if (origrn instanceof ResolvedDeclName) {
                decl = ((ResolvedDeclName)origrn).getDecl();
            } else if (origrn instanceof ResolvedAmbigiousName) {
                decl = ((AmbiguousDecl)((ResolvedAmbigiousName)origrn).getDecl()).getAlternative().get(0);
            }
            if (decl != null && !decl.getFileName().equals(abs.frontend.parser.Main.UNKNOWN_FILENAME)) {
                location = " at " + decl.getFileName() + ":" + decl.getStartLine() + ":" + decl.getStartColumn();
            }
            switch (n.getKind()) {
            case CLASS:
                msg = ErrorMessage.DUPLICATE_CLASS_NAME;
                break;
            case FUN:
                msg = ErrorMessage.DUPLICATE_FUN_NAME;
                break;
            case DATA_CONSTRUCTOR:
                msg = ErrorMessage.DUPLICATE_CONSTRUCTOR;
                break;
            case TYPE_DECL:
                msg = ErrorMessage.DUPLICATE_TYPE_DECL;
                break;
            case EXCEPTION:
                msg = ErrorMessage.DUPLICATE_EXCEPTION_DECL;
                break;
            case MODULE:
                assert false; // doesn't happen, no modules within modules
                break;
            default:
                assert false;   // detect if we added a new KindedName.Kind
                break;
            }
            errors.add(new TypeError(rn.getDecl(), msg, n.getName(), location));
        }
    }

    public static ResolvedMap getDefinedNames(ModuleDecl mod,
            Map<KindedName, ResolvedName> foundDuplicates) {
        ResolvedMap res = new ResolvedMap();
        ResolvedModuleName moduleName = new ResolvedModuleName(mod);

        for (Decl d : mod.getDeclList()) {
            ResolvedDeclName rn = new ResolvedDeclName(moduleName, d);
            if (res.containsKey(rn.getSimpleName()))
                foundDuplicates.put(rn.getSimpleName(), res.get(rn.getSimpleName()));
            res.put(rn.getSimpleName(), rn);
            res.put(rn.getQualifiedName(), rn);

            if (d instanceof DataTypeDecl) {
                DataTypeDecl dataDecl = (DataTypeDecl) d;
                for (DataConstructor c : dataDecl.getDataConstructors()) {
                    rn = new ResolvedDeclName(moduleName, c);
                    if (res.containsKey(rn.getSimpleName()))
                        foundDuplicates.put(rn.getSimpleName(), res.get(rn.getSimpleName()));
                    res.put(rn.getSimpleName(), rn);
                    res.put(rn.getQualifiedName(), rn);
                }
            } else if (d.isException()) {
                ExceptionDecl ed = (ExceptionDecl) d;
                DataConstructor ec = ed.dataConstructor;
                assert ec != null : ed.getName();
                if (ec.getName().equals(d.getName())) {
                    // should always be true, see Main.java where the data
                    // constructor gets constructed
                    rn = new ResolvedDeclName(moduleName, ec);
                    // If it's already in there, is it from the same location -- from stdlib?
                    ResolvedName tryIt = res.get(rn);
                    if (tryIt != null && tryIt.getDecl() != ed)
                        foundDuplicates.put(rn.getSimpleName(), tryIt);
                    else {
                        res.put(rn.getQualifiedName(), rn);
                    }
                }
            }
        }
        return res;
    }

    public static ResolvedMap getImportedNames(ModuleDecl mod) {
        ResolvedMap res = new ResolvedMap();

        for (Import i : mod.getImports()) {
            if (i instanceof StarImport) {
                StarImport si = (StarImport) i;
                ModuleDecl md = mod.lookupModule(si.getModuleName());
                if (md != null) {
                    res.addAllNamesNoHiding(md.getExportedNames());
                }
            } else if (i instanceof NamedImport) {
                NamedImport ni = (NamedImport) i;
                for (Name n : ni.getNames()) {
                    // statements like "import X;" lead to earlier type error;
                    // avoid calling lookupModule(null) here
                    if (!n.isSimple()) {
                        ModuleDecl md = mod.lookupModule(n.getModuleName());
                        if (md != null)
                            try {
                                res.addAllNames(md.getExportedNames(), n);
                            } catch (TypeCheckerException e) {} // NADA
                    }
                }
            } else if (i instanceof FromImport) {
                FromImport fi = (FromImport) i;
                ModuleDecl md = mod.lookupModule(fi.getModuleName());
                if (md != null) {
                    ResolvedMap en = md.getExportedNames();
                    for (Name n : fi.getNames()) {
                        res.putKindedNamesNoHiding(n.getString(), en);
                        res.putKindedNamesNoHiding(fi.getModuleName() + "." + n.getString(), en);
                    }
                }
            }
        }
        return res;
    }

    public static ResolvedMap getVisibleNames(ModuleDecl mod, Map<KindedName, ResolvedName> foundDuplicates) {
        ResolvedMap res = new ResolvedMap();
        ResolvedMap ownNames = getDefinedNames(mod, foundDuplicates);

        // add imported names:
        res.putAll(mod.getImportedNames());

        // Find shadowing entries
        for (KindedName entry : ownNames.keySet()) {
            if (res.containsKey(entry) && !ownNames.get(entry).equals(res.get(entry))) {
                foundDuplicates.put(entry, res.get(entry));
            }
        }

        // defined names hide imported names for the purpose of this method
        res.putAll(ownNames);
        return res;
    }

    public static ResolvedMap getExportedNames(ModuleDecl mod) {
        ResolvedMap res = new ResolvedMap();
        for (Export e : mod.getExports()) {
            if (e instanceof StarExport) {
                StarExport se = (StarExport) e;
                if (!se.hasModuleName()) {
                    res.putAll(mod.getDefinedNames());
                } else {
                    String moduleName = se.getModuleName().getName();
                    res.putNamesOfModule(mod, mod.getVisibleNames(), moduleName, null);
                }
            } else if (e instanceof FromExport) {
                FromExport fe = (FromExport) e;
                String moduleName = fe.getModuleName();
                for (Name n : fe.getNames()) {
                    String simpleName = n.getSimpleName();
                    res.putNamesOfModule(mod, mod.getVisibleNames(), moduleName, simpleName);
                }
            } else if (e instanceof NamedExport) {
                NamedExport ne = (NamedExport) e;
                for (Name n : ne.getNames()) {
                    String simpleName = n.getSimpleName();
                    res.putKindedNames(simpleName, mod.getVisibleNames());
                    res.putKindedNames(mod.getName() + "." + simpleName, mod.getVisibleNames());
                }
            }

        }
        return res;
    }

    public static void typeCheckBinary(SemanticConditionList e, Binary b, Type t) {
        b.getLeft().assertHasType(e, t);
        b.getRight().assertHasType(e, t);
        b.getLeft().typeCheck(e);
        b.getRight().typeCheck(e);
    }

    public static void partialTypeCheckBinary(SemanticConditionList e, Binary b, Type t, DependencyList deps) {
        // Special case: can also "add" strings.
        Type rt = null;
        try {
            rt = b.getRight().getType();
        } catch (Exception e2) {
            if(b.getRight() instanceof VarOrFieldUse)
            deps.add(new SubTypeDep(new TypeOfLocation(b.getRight().toString()), new TypeIdentity(t)));
        }
        Type lt = null;
        try {
            lt = b.getLeft().getType();
        } catch (Exception e2) {        
            if(b.getLeft() instanceof VarOrFieldUse)
            deps.add(new SubTypeDep(new TypeOfLocation(b.getLeft().toString()), new TypeIdentity(t)));
        }
        if(rt.isUnknownType()&& b.getRight() instanceof VarOrFieldUse){
            deps.add(new SubTypeDep(new TypeOfLocation(b.getRight().toString()), new TypeIdentity(t)));
        }

        if(lt.isUnknownType() && b.getLeft() instanceof VarOrFieldUse){
            deps.add(new SubTypeDep(new TypeOfLocation(b.getLeft().toString()), new TypeIdentity(t)));
        }

        b.getLeft().partialTypeCheck(e, deps);
        b.getRight().partialTypeCheck(e, deps);
    }

    /**
     * checks whether the local variable v was already defined in the current function
     */
    public static void checkForDuplicatesOfVarDecl(SemanticConditionList e, VarDeclStmt v) {
        String varName = v.getVarDecl().getName();
        VarOrFieldDecl otherVar = v.lookupVarOrFieldName(varName , false);
        if (otherVar != null && v.inSameMethodOrBlock(otherVar)) {
            e.add(new TypeError(v,ErrorMessage.VARIABLE_ALREADY_DECLARED, varName));
        }
    }

    /**
     * check a list of compilation units for duplicate module names, product names, delta names
     */
    public static void checkForDuplicateModules(SemanticConditionList errors, Iterable<CompilationUnit> compilationUnits) {
        Set<String> seenModules = new HashSet<String>();
        for (CompilationUnit u : compilationUnits) {
            for (ModuleDecl module : u.getModuleDecls()) {
                if (!seenModules.add(module.getName())) {
                    errors.add(new TypeError(module, ErrorMessage.DUPLICATE_MODULE_NAME,module.getName()));
                }
            }
        }
    }
    public static void checkForDuplicateProducts(SemanticConditionList errors, Iterable<CompilationUnit> compilationUnits) {
        Set<String> seen = new HashSet<String>();
        for (CompilationUnit u : compilationUnits) {
            for (ProductDecl p : u.getProductDecls()) {
                if (!seen.add(p.getName()))
                    errors.add(new TypeError(p, ErrorMessage.DUPLICATE_PRODUCT, p.getName()));
            }
        }
    }
    public static void checkForDuplicateDeltas(SemanticConditionList errors, Iterable<CompilationUnit> compilationUnits) {
        Set<String> seen = new HashSet<String>();
        for (CompilationUnit u : compilationUnits) {
            for (DeltaDecl d : u.getDeltaDecls()) {
                if (!seen.add(d.getName()))
                    errors.add(new TypeError(d, ErrorMessage.DUPLICATE_DELTA, d.getName()));
            }
        }
    }
    public static void checkForDuplicateUpdates(SemanticConditionList errors, Iterable<CompilationUnit> compilationUnits) {
        Set<String> seen = new HashSet<String>();
        for (CompilationUnit u : compilationUnits) {
            for (UpdateDecl d : u.getUpdateDecls()) {
                if (!seen.add(d.getName()))
                    errors.add(new TypeError(d, ErrorMessage.DUPLICATE_UPDATE, d.getName()));
            }
        }
    }

    /**
     * get all the alternative declarations of an ambiguous declaration formated as a list
     * which can be used in error messages
     * @param a
     * @return
     */
    public static String getAlternativesAsString(AmbiguousDecl a) {
        String result = "";
        for (Decl alternative : a.getAlternative()) {
            result += "\n * " + alternative.qualifiedName() +  " (defined in " +
                    alternative.getFileName() + ", line " + alternative.getStartLine() + ")";
        }
        return result;
    }

    public static void checkDataTypeUse(SemanticConditionList e, DataTypeUse use) {
        Type type = use.getType();
        if (type.getDecl() instanceof ParametricDataTypeDecl) {
            DataTypeType t = (DataTypeType) type;
            int expected = ((ParametricDataTypeDecl)type.getDecl()).getNumTypeParameter();
            if (expected != t.numTypeArgs()) {
                e.add(new TypeError(use, ErrorMessage.WRONG_NUMBER_OF_TYPE_ARGS,type.toString(),""+expected,""+t.numTypeArgs()));
            } else if (expected > 0) {
                if (use instanceof ParametricDataTypeUse) {
                        for (TypeUse du : ((ParametricDataTypeUse)use).getParams()) {
                                du.typeCheck(e);
                        }
                } else if (use.getDecl() instanceof TypeSynDecl) {
                    // nothing to check as this is already checked at the TypeSynDecl
                } else {
                    e.add(new TypeError(use, ErrorMessage.WRONG_NUMBER_OF_TYPE_ARGS,type.toString(),""+expected,"0"));
                }
            }
        }
    }

    public static void checkDefBeforeUse(SemanticConditionList e, VarOrFieldUse use) {
        if (use.getType().isUnknownType()) {
            e.add(new TypeError(use,ErrorMessage.NAME_NOT_RESOLVABLE, use.getName()));
        } else {
            /* Check that fields are not used before they are defined,
             * when we are NOT inside a method, e.g. when initialising a field upon declaration.
             */
            // FIXME: this could break down wrt deltas
            boolean isUsedInFieldDecl = use instanceof FieldUse;
            if (isUsedInFieldDecl && use.getContextMethod() == null
                && SourcePosition.larger(use.getDecl().getEndLine(), use.getDecl().getEndColumn(), use.getStartLine(), use.getStartColumn())) {
                e.add(new TypeError(use,
                                    isUsedInFieldDecl ? ErrorMessage.FIELD_USE_BEFORE_DEFINITION : ErrorMessage.VAR_USE_BEFORE_DEFINITION,
                                    use.getName()));
            }
        }
    }
}
