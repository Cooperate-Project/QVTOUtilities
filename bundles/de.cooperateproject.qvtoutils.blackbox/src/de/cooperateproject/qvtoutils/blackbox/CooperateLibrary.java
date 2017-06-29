package de.cooperateproject.qvtoutils.blackbox;

import java.util.LinkedHashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.eclipse.ocl.util.CollectionUtil;

public final class CooperateLibrary {

    public CooperateLibrary() {
        super();
    }
    
    @SuppressWarnings("squid:S1319")
    @Operation(contextual=true)
    public static LinkedHashSet<EObject> getAllContents(Object rootObject) {
        LinkedHashSet<EObject> result = CollectionUtil.createNewOrderedSet();
        if (rootObject instanceof EObject) {
            ((EObject) rootObject).eAllContents().forEachRemaining(result::add);
        }
        return result;
    }
    
}
