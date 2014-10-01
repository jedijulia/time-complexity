package up.cmsc142.julia.TimeComplexityFinal;


public class Logarithm extends Term {
    String base;
    String arg;
    
    public Logarithm(String base, String arg) {
        this.base = base;
        this.arg = arg;
        if (this.isComputable()) {
            this.coefficient = (int)this.compute();
            this.term = ""+this.coefficient;
        }
        else {
            this.term = "log base " + this.base + "(" + this.arg + ")";
        }
    }
    
    @Override
    public double compute() {
        return Math.log(Integer.parseInt(this.arg)) / Math.log(Integer.parseInt(this.base));
    }
    
    @Override
    public boolean isComputable() {
        if (this.isInt(this.base) && this.isInt(this.arg)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}
