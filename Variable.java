/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package up.cmsc142.julia.TimeComplexityFinal;

/**
 *
 * @author juliam
 */
public class Variable {
    String variable;
    int exponent;
    
    public Variable() {
        
    }
    
    public Variable(String variable) {
        this.variable = variable;
    }
    
    public String getVariable() {
        return this.variable;
    }
    
    public int getExponent() {
        return this.exponent;
    }
    
    public void setExponent(int exponent) {
        this.exponent = exponent;
    }
}
