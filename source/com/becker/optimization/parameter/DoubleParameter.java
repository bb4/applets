package com.becker.optimization.parameter;

import com.becker.optimization.parameter.redistribution.GaussianRedistribution;
import com.becker.optimization.parameter.redistribution.UniformRedistribution;
import com.becker.optimization.parameter.ui.DoubleParameterWidget;
import com.becker.optimization.parameter.ui.ParameterWidget;

/**
 *  represents a double (i.e. floating point) parameter to an algorithm
 *
 *  @author Barry Becker
 */
public class DoubleParameter extends AbstractParameter {

    /**
     * approximate number of steps to take when marching across one of the parameter dimensions.
     * used to calculate the stepsize in a dimension direction.
     */
    private static final int NUM_STEPS = 30;

    /**
     *  Constructor
     * @param val the initial or assign parameter value
     * @param minVal the minimum value that this parameter is allowed to take on
     * @param maxVal the maximum value that this parameter is allowed to take on
     * @param paramName of the parameter
     */
    public DoubleParameter( double val, double minVal, double maxVal, String paramName ) {
        super(val, minVal, maxVal, paramName, false);    
    }
    
    public static DoubleParameter createGaussianParameter(double val, double minVal, double maxVal,
                                                         String paramName, double normalizedMean, double stdDeviation) {
        DoubleParameter param = new DoubleParameter(val, minVal, maxVal, paramName);
        param.setRedistributionFunction(new GaussianRedistribution(normalizedMean, stdDeviation));    
        return param;
    }
    
    public static DoubleParameter createUniformParameter(double val, double minVal, double maxVal,
                                                         String paramName, double[] specialValues,
                                                         double[] specialValueProbabilities) {
        DoubleParameter param = new DoubleParameter(val, minVal, maxVal, paramName);
        param.setRedistributionFunction(
                new UniformRedistribution(specialValues, specialValueProbabilities));   
        return param;
    }

    public Parameter copy() {
        DoubleParameter p =  new DoubleParameter( getValue(), getMinValue(), getMaxValue(), getName() );
        p.setRedistributionFunction(redistributionFunction_);
        return p;
    }

    /**
     * increments the parameter based on the number of steps to get from one end of the range to the other.
     * If we are already at the max end of the range, then we can only move in the other direction if at all.
     * @param direction 1 for forward, -1 for backward.
     * @return the size of the increment taken
     */
    public double incrementByEps(int direction ) {

        double increment = direction * (getMaxValue() - getMinValue()) / NUM_STEPS;

        double v = getValue();
        if ( (v+increment > getMaxValue())) {
            value_ = getMaxValue();
            return 0;
        }
        else if (v+increment < getMinValue())  {
            value_ = getMinValue();
            return 0;
        }
        else {
            value_ = (v + increment);
            return increment;
        }
    }

    public Object getNaturalValue() {
        return this.getValue();
    }
    
    @Override
    public boolean isIntegerOnly() {
        return false;
    }
    
    @Override
    public Class getType() {
        return float.class; 
    }
    
    public ParameterWidget createWidget(ParameterChangeListener listener) {
        return new DoubleParameterWidget(this, listener);
    }
}
