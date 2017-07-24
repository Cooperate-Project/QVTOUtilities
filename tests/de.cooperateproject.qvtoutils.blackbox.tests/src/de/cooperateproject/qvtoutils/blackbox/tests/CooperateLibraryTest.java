package de.cooperateproject.qvtoutils.blackbox.tests;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Iterators;

import de.cooperateproject.qvtoutils.blackbox.CooperateLibrary;

public class CooperateLibraryTest {
	
    protected ResourceSet resourceSet;
    protected Resource modelResource;
	protected IContext context;
    
    @BeforeClass
    public static void init() throws Exception {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%m%n")));
        
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
        EcoreFactory.eINSTANCE.eClass();
    }
    
	@Before
    public void setup() {
        resourceSet = new ResourceSetImpl();
        modelResource = resourceSet.getResource(URI.createURI("model/test.ecore"), true);
        context = TestUtils.createLoggingContext();
    }
    
    @Test
    public void testGetAllContents() {
        EObject e = modelResource.getContents().get(0);
        assertThat(CooperateLibrary.getAllContents(context, e), contains(Iterators.toArray(e.eAllContents(), EObject.class)));
    }
    
    @Test
    public void testGetAllContentsFromRoot() {
        EPackage p = (EPackage) modelResource.getContents().get(0).eContents().stream().filter(EcorePackage.eINSTANCE.getEPackage()::isInstance).findFirst().get();
        assertThat(CooperateLibrary.getAllContents(context, p), 
                contains(Iterators.toArray(modelResource.getContents().get(0).eAllContents(), EObject.class)));
    }
    
    @Test
    public void testTypedAllContentsFromRoot() {
        EPackage p = (EPackage) modelResource.getContents().get(0).eContents().stream().filter(EcorePackage.eINSTANCE.getEPackage()::isInstance).findFirst().get();
        assertThat(CooperateLibrary.getAllContentsOfType(context, p, EcorePackage.eINSTANCE.getEEnum(), true), hasSize(1));
        assertThat(CooperateLibrary.getAllContentsOfType(context, p, EcorePackage.eINSTANCE.getEClass(), true), hasSize(4));
        assertThat(CooperateLibrary.getAllContentsOfType(context, p, EcorePackage.eINSTANCE.getEClassifier(), true), hasSize(5));
    }

    @Test
    public void testAddToFeatureSingleElement() {
    	EPackage rootPackage = (EPackage) modelResource.getContents().get(0);
    	
    	ArrayList<EClassifier> expectedClassifiers = new ArrayList<>(rootPackage.getEClassifiers());
    	EClass newClassifier = EcoreFactory.eINSTANCE.createEClass();
    	expectedClassifiers.add(newClassifier);

    	CooperateLibrary.addToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), newClassifier);
    	
    	assertEquals(expectedClassifiers, rootPackage.getEClassifiers());
    }
    
    @Test
    public void testAddToFeatureDuplicates() {
    	EPackage rootPackage = (EPackage) modelResource.getContents().get(0);
    	
    	ArrayList<EClassifier> expectedClassifiers = new ArrayList<>(rootPackage.getEClassifiers());
    	EClassifier existingClassifier = rootPackage.getEClassifiers().get(0);

    	CooperateLibrary.addToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), existingClassifier);
    	
    	assertEquals(expectedClassifiers, rootPackage.getEClassifiers());
    }
    
    @Test
    public void testAddToFeatureCollectionOfElements() {
    	EPackage rootPackage = (EPackage) modelResource.getContents().get(0);
    	
    	EClass newClassifier = EcoreFactory.eINSTANCE.createEClass();
    	EClass newClassifier2 = EcoreFactory.eINSTANCE.createEClass();
    	Collection<EClass> classifiersToAdd = Arrays.asList(newClassifier, newClassifier2);
    	
    	Collection<EClassifier> expectedClassifiers = new ArrayList<>(rootPackage.getEClassifiers());
    	expectedClassifiers.addAll(classifiersToAdd);

    	CooperateLibrary.addToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), expectedClassifiers);
    	
    	assertEquals(expectedClassifiers, rootPackage.getEClassifiers());
    }
    
    @Test
    public void testAddToFeatureCollectionOfElementsContainingDuplicates() {
    	EPackage rootPackage = (EPackage) modelResource.getContents().get(0);
    	
    	EClassifier oldClassifier = rootPackage.getEClassifiers().get(0);
    	EClass newClassifier2 = EcoreFactory.eINSTANCE.createEClass();
    	Collection<EClassifier> classifiersToAdd = Arrays.asList(oldClassifier, newClassifier2);
    	
    	Collection<EClassifier> expectedClassifiers = new ArrayList<>(rootPackage.getEClassifiers());
    	expectedClassifiers.add(newClassifier2);

    	CooperateLibrary.addToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), classifiersToAdd);
    	
    	assertEquals(expectedClassifiers, rootPackage.getEClassifiers());
    }
    
    @Test
    public void testSetToFeatureSingleElement() {
    	EPackage rootPackage = (EPackage) modelResource.getContents().get(0);
    	
    	EClass newClassifier = EcoreFactory.eINSTANCE.createEClass();
    	List<EClass> expectedClassifiers = Arrays.asList(newClassifier);

    	CooperateLibrary.setToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), newClassifier);
    	
    	assertEquals(expectedClassifiers, rootPackage.getEClassifiers());
    }
    
    @Test
    public void testSetToFeatureSingleElementMultipleTimes() {
    	EPackage rootPackage = (EPackage) modelResource.getContents().get(0);
    	
    	EClass newClassifier = EcoreFactory.eINSTANCE.createEClass();
    	EClass newClassifier2 = EcoreFactory.eINSTANCE.createEClass();
    	List<EClass> expectedClassifiers = Arrays.asList(newClassifier2);

    	CooperateLibrary.setToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), newClassifier);
    	CooperateLibrary.setToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), newClassifier2);
    	
    	assertEquals(expectedClassifiers, rootPackage.getEClassifiers());
    }
    
    @Test
    public void testSetToFeatureCollectionOfElements() {
    	EPackage rootPackage = (EPackage) modelResource.getContents().get(0);
    	
    	EClass newClassifier = EcoreFactory.eINSTANCE.createEClass();
    	EClass newClassifier2 = EcoreFactory.eINSTANCE.createEClass();
    	List<EClass> expectedClassifiers = Arrays.asList(newClassifier, newClassifier2);

    	CooperateLibrary.setToFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ECLASSIFIERS.getName(), expectedClassifiers);
    	
    	assertEquals(expectedClassifiers, rootPackage.getEClassifiers());
    }
}
