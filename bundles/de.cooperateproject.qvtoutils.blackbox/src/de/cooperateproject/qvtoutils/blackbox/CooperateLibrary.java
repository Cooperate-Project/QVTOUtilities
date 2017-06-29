package de.cooperateproject.qvtoutils.blackbox;

import java.util.LinkedHashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.eclipse.ocl.util.CollectionUtil;

/**
 * Library for several utility operations related to the Cooperate project.
 */
public class CooperateLibrary {

    /**
     * Instantiates the library.
     */
    @SuppressWarnings("squid:S1118")
    public CooperateLibrary() {
        super();
    }
    
    @SuppressWarnings("squid:S1319")
    @Operation(contextual=true)
    public static LinkedHashSet<EObject> getAllContents(Object rootObject) {
        return getAllContents(rootObject, true);
    }
    
    @SuppressWarnings("squid:S1319")
    @Operation(contextual=true)
    public static LinkedHashSet<EObject> getAllContents(Object rootObject, boolean forRoot) {
        LinkedHashSet<EObject> result = CollectionUtil.createNewOrderedSet();
        if (rootObject instanceof EObject) {
            EObject typedRootObject = (EObject)rootObject;
            if (forRoot) {
                typedRootObject = EcoreUtil.getRootContainer(typedRootObject);
            }
            typedRootObject.eAllContents().forEachRemaining(result::add);
        }
        return result;
    }
    
}
