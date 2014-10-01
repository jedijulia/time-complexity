package up.cmsc142.julia.TimeComplexityFinal;


public class Variable {
    String variable;
    int exponent;
    int denom;
    
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
    
    
    public boolean isEmpty() {
        if (this.variable == null) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isEqual(Variable anotherVar) {
        if ((this.variable.equals(anotherVar.variable)) && (this.exponent == anotherVar.exponent)) {
            return true;
        } else {
            return false;
        }
    }
}
