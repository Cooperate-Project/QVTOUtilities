package de.cooperateproject.qvtoutils.blackbox;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collector;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation;
import org.eclipse.m2m.qvt.oml.blackbox.java.Operation.Kind;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.eclipse.ocl.util.CollectionUtil;

import com.google.common.collect.Streams;

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
        return getAllContentsOfType(executionContext, rootObject, EcorePackage.eINSTANCE.getEObject(), forRoot);
    }
    
    @SuppressWarnings("squid:S1319")
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.QUERY,
        description = "Determines all elements of the containment tree, to which the given object belongs to.")
    public static LinkedHashSet<EObject> getAllContentsOfType(IContext executionContext, Object rootObject, EClass type, boolean forRoot) {
        LinkedHashSet<EObject> result = null;
        if (rootObject instanceof EObject) {
            EObject typedRootObject = (EObject) rootObject;
            if (forRoot) {
                typedRootObject = EcoreUtil.getRootContainer(typedRootObject);
            }
            result = Streams.stream(typedRootObject.eAllContents()).filter(type::isInstance).collect(
                    Collector.of(CollectionUtil::<EObject>createNewOrderedSet, Collection::add, 
                            (c1, c2) -> {c1.addAll(c2); return c1;}));
        } else {
            executionContext.getLog().log("Unsupported type for call to getAllContents()",
                    Optional.ofNullable(rootObject).map(Object::getClass).orElseGet(null));
        }
        return result != null ? result : CollectionUtil.createNewOrderedSet();
    }
    
    @SuppressWarnings("unchecked")
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.HELPER,
            description = "Adds the given element to multi-valued feature of the given context element.")
    public static void addToFeature(IContext executionContext, Object context, String featureName, Object element) {
        Collection<Object> values = getFeatureCollection(context, featureName);
        if (element instanceof Collection) {
            values.addAll((Collection<Object>)element);
        } else {
            values.add(element);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Operation(contextual = true, withExecutionContext = true, kind = Kind.HELPER,
            description = "Replaces existing elements with the given element in a multi-valued feature of the given context element.")
    public static void setToFeature(IContext executionContext, Object context, String featureName, Object element) {
        Collection<Object> values = getFeatureCollection(context, featureName);
        values.clear();
        if (element instanceof Collection) {
            values.addAll((Collection<Object>)element);
        } else {
            values.add(element);
        }
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
