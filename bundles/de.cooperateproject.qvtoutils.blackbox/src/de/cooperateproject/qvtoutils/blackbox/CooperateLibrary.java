package de.cooperateproject.qvtoutils.blackbox;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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
    
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.HELPER,
            description = "Adds the given element to multi-valued feature of the given context element.")
    public static void addToFeature(IContext executionContext, Object context, String featureName, Object element) {
        addToFeature(executionContext, context, featureName, Arrays.asList(element));
    }
    
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.HELPER,
            description = "Adds the given elements to multi-valued feature of the given context element.")
    public static void addToFeature(IContext executionContext, Object context, String featureName, Collection<Object> elements) {
        getFeatureCollection(context, featureName).addAll(elements);
    }
    
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.HELPER,
            description = "Replaces existing elements with the given element in a multi-valued feature of the given context element.")
    public static void setToFeature(IContext executionContext, Object context, String featureName, Object element) {
        setToFeature(executionContext, context, featureName, Arrays.asList(element));
    }
    
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.HELPER,
            description = "Replaces existing elements with the given elements in a multi-valued feature of the given context element.")
    public static void setToFeature(IContext executionContext, Object context, String featureName, Collection<Object> elements) {
        Collection<Object> values = getFeatureCollection(context, featureName);
        values.clear();
        values.addAll(elements);
    }
    
    private static Collection<Object> getFeatureCollection(Object context, String featureName) {
        if (!(context instanceof EObject)) {
            throw new IllegalArgumentException(String.format("The given element %s is not an EObject.", context));
        }
        
        EObject typedContext = (EObject)context;
        Optional<EReference> possibleFeature = typedContext.eClass().getEAllReferences().stream().filter(f -> f.getName().equals(featureName)).findFirst();
        if (!possibleFeature.map(EReference::isMany).orElse(false)) {
            throw new IllegalArgumentException(String.format("The given feature %s is not a multi-valued reference.", featureName));
        }
        
        Optional<Collection<Object>> values = possibleFeature.map(typedContext::eGet).filter(Collection.class::isInstance).map(Collection.class::cast);
        if (!values.isPresent()) {
            throw new IllegalStateException("The feature does not contain a collection.");
        }
        return values.get();
    }

}
