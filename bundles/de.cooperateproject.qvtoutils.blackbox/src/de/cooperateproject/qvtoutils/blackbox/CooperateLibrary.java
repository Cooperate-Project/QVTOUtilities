package de.cooperateproject.qvtoutils.blackbox;

import java.util.LinkedHashSet;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation.Kind;
import org.eclipse.m2m.qvt.oml.util.IContext;
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
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.QUERY,
        description = "Determines all contained elements of the given object.")
    public static LinkedHashSet<EObject> getAllContents(IContext executionContext, Object rootObject) {
        return getAllContents(executionContext, rootObject, true);
    }

    @SuppressWarnings("squid:S1319")
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.QUERY,
        description = "Determines all elements of the containment tree, to which the given object belongs to.")
    public static LinkedHashSet<EObject> getAllContents(IContext executionContext, Object rootObject, boolean forRoot) {
        LinkedHashSet<EObject> result = CollectionUtil.createNewOrderedSet();
        if (rootObject instanceof EObject) {
            EObject typedRootObject = (EObject) rootObject;
            if (forRoot) {
                typedRootObject = EcoreUtil.getRootContainer(typedRootObject);
            }
            typedRootObject.eAllContents().forEachRemaining(result::add);
        } else {
            executionContext.getLog().log("Unsupported type for call to getAllContents()",
                    Optional.ofNullable(rootObject).map(Object::getClass).orElseGet(null));
        }
        return result;
    }

}
