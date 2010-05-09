package com.becker.common.interpolation;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAllInterpolation  {

    private TestAllInterpolation() {}

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Interpolation Tests");

        suite.addTestSuite(LinearInterpolatorTest.class);
        suite.addTestSuite(CubicInterpolatorTest.class);
        suite.addTestSuite(CosineInterpolatorTest.class);
        suite.addTestSuite(HermiteInterpolatorTest.class);
        suite.addTestSuite(StepInterpolatorTest.class);

        return suite;
    }
}