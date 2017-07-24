package de.cooperateproject.qvtoutils.blackbox.tests;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.m2m.qvt.oml.util.IContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.cooperateproject.qvtoutils.blackbox.CooperateLibrary;

@RunWith(Parameterized.class)
public class CooperateLibraryFeatureSetterTests {

	@FunctionalInterface
	private static interface SetterFunction {
		void setFeature(IContext executionContext, Object context, String featureName, Object element);
	}
	
	@Parameters(name="{0}")
	public static Collection<Object[]> data() {
		SetterFunction s1 = CooperateLibrary::setToFeature;
		SetterFunction s2 = CooperateLibrary::addToFeature;

		return Arrays.asList(new Object[][] { { "setToFeature", s1 }, { "addToFeature", s2 } });
	}
	
	private final SetterFunction setterFunction;
	private IContext context;
	private EPackage rootPackage;
	
	public CooperateLibraryFeatureSetterTests(String methodName, SetterFunction setterFunction) {
		this.setterFunction = setterFunction;
	}
	
	@Before
	public void setup() {
		context = TestUtils.createLoggingContext();
		rootPackage = EcoreFactory.eINSTANCE.createEPackage();
	}
	
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidContext() {
    	setterFunction.setFeature(context, new Object(), "someNonExistingName", new Object());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testNonExistingFeatureName() {
    	setterFunction.setFeature(context, rootPackage, "someNonExistingName", new Object());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testNoEReference() {
    	setterFunction.setFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__NS_URI.getName(), new Object());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testNoMultiValuedEReference() {
    	setterFunction.setFeature(context, rootPackage, EcorePackage.Literals.EPACKAGE__ESUPER_PACKAGE.getName(), new Object());
    }
}
