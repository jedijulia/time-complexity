package up.cmsc142.julia.TimeComplexity2Mod;

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
