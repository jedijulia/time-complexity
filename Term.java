package up.cmsc142.julia.TimeComplexityFinal;

import java.util.ArrayList;
import java.util.List;


public class Term {
    String term;
    int coefficient = 1;
    int denom = 1;
    List<Variable> variables = new ArrayList();
    
    public Term(String term) {
        this.term = term;
        this.constructTerm();
    }
    
    public Term() {
        
    }
    
    public void constructTerm() {
        String coefficientStr = "";
        boolean afterVar = false;
        Variable currVar = new Variable();
        String currVarPower = "";
        for (int i=0; i < this.term.length(); i++) {
            String currChar = "" + this.term.charAt(i);
            // number
            try {
                Integer.parseInt(currChar);
                if (!afterVar) {
                    coefficientStr += currChar;
                } else {
                    currVarPower += currChar;
                }
            // letter
            } catch(Exception e) {
                if (!afterVar) {
                    currVar = new Variable(currChar);
                    afterVar = true;
                } else {
                    if (currVarPower.equals("")) {
                        currVar.setExponent(1);
                    } else {
                        currVar.setExponent(Integer.parseInt(currVarPower));
                    }
                    currVarPower = "";
                    variables.add(currVar);
                    currVar = new Variable(currChar);
                }
            }
        }
        if (!coefficientStr.isEmpty()) {
            this.coefficient = Integer.parseInt(coefficientStr);
        }
        if (!currVarPower.equals("")) {
            currVar.setExponent(Integer.parseInt(currVarPower));
            variables.add(currVar);
        } else {
            if (currVar.variable != null) {
                currVar.setExponent(1);
                variables.add(currVar);
            }
        }
        
        Variable negative = this.getVariable(this, "-");
        if (!negative.isEmpty()) {
            String coefficientStrMod = "-" + negative.exponent;
            this.coefficient = Integer.parseInt(coefficientStrMod);
            this.variables.remove(negative);
        }
    }
    
    public Polynomial add(Term anotherTerm) {
        List<Object> polynomialContents = new ArrayList();
        if (this.isValid(anotherTerm)) {
            // no variables
            String result = "";
            int coefficientSum = this.getCoefficient() + anotherTerm.getCoefficient();
            if (this.getVariables().isEmpty() && anotherTerm.getVariables().isEmpty()) {
                result += coefficientSum;
            } else {
                for (Variable variable: this.getVariables()) {
                    result = result + variable.variable + variable.getExponent();            
                }
                result = coefficientSum + result;
            }
            Term resultTerm = new Term(result);
            polynomialContents.add(resultTerm);
        } 
        Polynomial polynomial = new Polynomial(polynomialContents);
        return polynomial;
    }
    
    public Term subtract(Term anotherTerm) {
        if (this.isValid(anotherTerm)) {
            // no variables
            String result = "";
            int coefficientDiff = this.getCoefficient() - anotherTerm.getCoefficient();
            if (this.getVariables().isEmpty() && anotherTerm.getVariables().isEmpty()) {
                result += coefficientDiff;
            } else {
                for (Variable variable: this.getVariables()) {
                    result = result + variable.variable + variable.getExponent();            
                }
                result = coefficientDiff + result;
            }
            Term resultTerm = new Term(result);
            return resultTerm;
        } else {
            return this;
        }
    }
    
    
    public Polynomial multiply(Term anotherTerm) {
        String result = "" + (this.getCoefficient() * anotherTerm.getCoefficient());
        // make list of all variable.variables in both terms
        // loop through list and add powers if in both
        // if not, just add
        Term resultTerm = new Term();
        if (this instanceof Logarithm || anotherTerm instanceof Logarithm) {
            if (this instanceof Logarithm) {
                resultTerm = this;
            } else {
                resultTerm = anotherTerm;
            }
            resultTerm.coefficient = Integer.parseInt(result);
            resultTerm.updateTermCoeff(term);
        } else {
            List<String> variableList = getVarList(anotherTerm);
            if (variableList != null) {
                for (int i=0; i < variableList.size(); i++) {
                    String currVar = variableList.get(i);
                    Variable termVar = getVariable(this, currVar);
                    Variable anotherTermVar = getVariable(anotherTerm, currVar);
                    if ((termVar.variable != null) && (anotherTermVar != null)) {
                        int exponentSum = termVar.getExponent() + anotherTermVar.getExponent();
                        result = result + currVar + exponentSum;
                    } else if (termVar.variable != null) {
                        result = result + currVar + termVar.getExponent();
                    } else {
                        result = result + currVar + anotherTermVar.getExponent();
                    }
                }
            }
            resultTerm = new Term(result);
        }
        resultTerm.denom = this.denom * anotherTerm.denom;
        List<Object> polynomialContents = new ArrayList();
        polynomialContents.add(resultTerm);
        Polynomial polynomial = new Polynomial(polynomialContents);
        return polynomial;
    }
    
    public Variable getVariable(Term term, String var) {
        List<Variable> variables = term.getVariables();
        if (variables != null) {
            for (Variable variable: variables) {
                if (var.equals(variable.variable)) {
                    return variable;
                }
            }
        }
        Variable empty = new Variable();
        return empty;
    }
    
    public List<String> getVarList(Term anotherTerm) {
        List<String> variableList = new ArrayList();
        
        List<Variable> variables = this.getVariables();
        if (variables != null) {
            for (Variable variable: this.getVariables()) {
                String variableStr = variable.variable;
                if (!variableList.contains(variableStr)) {
                    variableList.add(variableStr);
                }
            }
        }
        
        variables = anotherTerm.getVariables();
        if (variables != null) {
            for (Variable variable: anotherTerm.getVariables()) {
                String variableStr = variable.variable;
                if (!variableList.contains(variableStr)) {
                    variableList.add(variableStr);
                }
            }
        }
        return variableList;
    }
    
    public int getCoefficient() {
        return this.coefficient;
    }
    
    public List<Variable> getVariables() {
        return this.variables;
    }
    
    
    public boolean isValid(Term anotherTerm) {
        if (this instanceof Logarithm || anotherTerm instanceof Logarithm) {
            return false;
        }
        
        if (this.variables.size() != anotherTerm.variables.size()) {
            return false;
        }
        for (Variable variable: this.variables) {
            Variable otherVariable = this.getVariable(anotherTerm, variable.variable);
            if (otherVariable.variable == null) {
                return false;
            }
            if (variable.getExponent() != otherVariable.getExponent()) {
                return false;
            }
        }
        if (this.denom != anotherTerm.denom) {
          return false;
        }
        return true;
    }
    
    public Polynomial convertToPolynomial() {
        List<Object> polyContents = new ArrayList();
        polyContents.add(this);
        Polynomial poly = new Polynomial(polyContents);
        return poly;
    }
    
    public void setDenom(int denom) {
        this.denom = denom;
    }
    
    //update coefficient given a denominator
    public void updateCoefficient() {
        if (this.denom != 1) {
            if ((this.coefficient % this.denom) == 0) {
                this.coefficient /= this.denom;
                this.updateTermCoeff("" + this.coefficient);
                this.denom = 1;
            }
        }
    }
    
    //update coefficient portion of Term's term
    public void updateTermCoeff(String coeff) {
        boolean found = false;
        int i=0;
        int index=0;
        while (!found) {
            char currChar = this.term.charAt(i);
            if(!Character.isDigit(currChar)) {
                found = true;
                index = i;
            }
            i++;
        }
        String updatedCoeff = coeff + this.term.substring(index, this.term.length());
        this.term = updatedCoeff;
    }
    
    public boolean isNumeric() {
      return (this.variables.isEmpty());
    }
    
    @Override
    public String toString() {
        String toReturn = "";
        //toReturn = this.term;
        if (this instanceof Logarithm) {
            toReturn += this.term;
        } else {
            if (!(this.coefficient == 1 && !this.variables.isEmpty())) {
                toReturn += this.coefficient;
            }
            for (Variable var : this.variables) {
                toReturn += var.variable;
                if (var.exponent != 1) {
                    toReturn += var.exponent;
                }
            }
        }
        if (this.denom != 1) {
            toReturn = toReturn + "/" + denom;
        }
        return toReturn;
    }
    
    //for Logarithm
    public double compute() {
        return 0;
    }
    
    public boolean isComputable() {
        return true;
    }
    
    public boolean isInt(String s) {
        return true;
    }
}


