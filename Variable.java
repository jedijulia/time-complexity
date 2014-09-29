package up.cmsc142.julia.TimeComplexityFinal;


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
    
    public boolean isEmpty() {
        if (this.variable == null) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isEqual(Variable anotherVar) {
        System.out.println("THIS IS INSIDE ISEQUAL");
        System.out.println("var.variable: " + this.variable);
        System.out.println("var.exponent: " + this.exponent);
        System.out.println("anotherVar.variable: " + anotherVar.variable);
        System.out.println("anotherVar.exponent: " + anotherVar.exponent);
        if ((this.variable.equals(anotherVar.variable)) && (this.exponent == anotherVar.exponent)) {
            return true;
        } else {
            return false;
        }
    }
}
