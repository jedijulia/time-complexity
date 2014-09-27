/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package up.cmsc142.julia.TimeComplexityFinal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juliam
 */
public class Term {
    String term;
    int coefficient = 1;
    List<Variable> variables = new ArrayList();
    
    public Term(String term) {
        this.term = term;
        this.constructTerm();
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
    }
    
    public Term add(Term anotherTerm) {
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
            return resultTerm;
        } else {
            return this;
        }
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
    
//    - multiply coefficients
//    - multiply variables
//            > add their powers! 
//    - if there are more than one variables: DO NOT MULTIPLY, just concatenate them together 
    
    public Term multiply(Term anotherTerm) {
        String result = "" + (this.getCoefficient() * anotherTerm.getCoefficient());
        // make list of all variable.variables in both terms
        // loop through list and add powers if in both
        // if not, just add
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
        Term resultTerm = new Term(result);
        return resultTerm;
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
    
    //TEMPORARY
    public boolean isValid(Term anotherTerm) {
        return true;
    }
}

