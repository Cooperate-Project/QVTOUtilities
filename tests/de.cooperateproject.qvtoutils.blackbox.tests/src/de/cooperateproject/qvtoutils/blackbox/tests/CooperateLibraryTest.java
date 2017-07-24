package de.cooperateproject.qvtoutils.blackbox.tests;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.internal.qvt.oml.library.Context;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Iterators;

import de.cooperateproject.qvtoutils.blackbox.CooperateLibrary;

public class CooperateLibraryTest {
    protected ResourceSet resourceSet;
    protected Resource modelResource;
    
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
    }
    
    @Test
    public void testGetAllContents() {
        EObject e = modelResource.getContents().get(0);
        assertThat(CooperateLibrary.getAllContents(new Context(), e), contains(Iterators.toArray(e.eAllContents(), EObject.class)));
    }
    
    @Test
    public void testGetAllContentsFromRoot() {
        EPackage p = (EPackage) modelResource.getContents().get(0).eContents().stream().filter(EcorePackage.eINSTANCE.getEPackage()::isInstance).findFirst().get();
        assertThat(CooperateLibrary.getAllContents(new Context(), p), 
                contains(Iterators.toArray(modelResource.getContents().get(0).eAllContents(), EObject.class)));
    }
    
    @Test
    public void testTypedAllContentsFromRoot() {
        EPackage p = (EPackage) modelResource.getContents().get(0).eContents().stream().filter(EcorePackage.eINSTANCE.getEPackage()::isInstance).findFirst().get();
        assertThat(CooperateLibrary.getAllContentsOfType(new Context(), p, EcorePackage.eINSTANCE.getEEnum(), true), hasSize(1));
        assertThat(CooperateLibrary.getAllContentsOfType(new Context(), p, EcorePackage.eINSTANCE.getEClass(), true), hasSize(4));
        assertThat(CooperateLibrary.getAllContentsOfType(new Context(), p, EcorePackage.eINSTANCE.getEClassifier(), true), hasSize(5));
    }

}
