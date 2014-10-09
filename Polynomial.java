package up.cmsc142.julia.TimeComplexityFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Polynomial {
    String equation;
    List<Object> contents = new ArrayList<Object>();
    
    public Polynomial(List<Object> contents) {
        this.contents = contents;
    }
    
    public Polynomial(String string) {
        String termStr = "";
        boolean afterNegative = false;
        for (int i=0; i < string.length(); i++) {
            char currChar = string.charAt(i); 
            if (currChar == '+') {
                Term term = new Term(termStr);
                this.contents.add(term);
                this.contents.add("+");
                termStr = "";
            } else if (currChar == '-') {
                if (i!=0) {
                    Term term = new Term(termStr);
                    this.contents.add(term);
                    this.contents.add("+");
                    termStr = "";
                }
                afterNegative = true;
            } else { 
                if (afterNegative) {
                    termStr = termStr + "-" + currChar;
                    afterNegative = false;
                } else {
                    termStr += currChar;
                }
            }
        }
        if (!termStr.equals("")) {
            Term term = new Term(termStr);
            this.contents.add(term);
        }
    }
        
    public Polynomial compute() { //parses polynomial in postfix
        Stack stack = new Stack();
        for (int i=0; i < this.contents.size(); i++) {
            Object currItem = this.contents.get(i);
            if (currItem instanceof Term) { 
                stack.push(currItem);
            } else if (currItem instanceof Polynomial) {
                stack.push(currItem);
            } else {
                String operation = (String)currItem;
                Object second = stack.pop();
                Object first = stack.pop();
                Polynomial firstPol;
                Polynomial secondPol;
                Polynomial result;
                
                if ((first instanceof Term) && (second instanceof Term)){
                    first = ((Term)first).convertToPolynomial();
                    second = ((Term)second).convertToPolynomial();
                } else if ((first instanceof Term)) {
                    first = ((Term)first).convertToPolynomial();
                } else if ((second instanceof Term)) {
                    second = ((Term)second).convertToPolynomial();
                }
                firstPol = (Polynomial)first;
                secondPol = (Polynomial)second;
                
                if (operation.equals("+")) {   
                    result = firstPol.add(secondPol);
                    result = result.updateCoefficients();
                    stack.push(result);      
                } else if (operation.equals("*")) {
                    result = firstPol.multiply(secondPol);
                    result = result.updateCoefficients();
                    stack.push(result);
                }
            }
        }
        return (Polynomial)stack.pop();
    }
     
    public Polynomial add(Polynomial anotherPoly) {
        List<Object> anotherPolyContents = anotherPoly.contents;
        for (Object object: anotherPolyContents) {
            this.contents.add("+");
            this.contents.add(object);
        }
        Polynomial result = this.simplify();
        return result;
    }
    
    public Polynomial multiply(Polynomial anotherPoly) {
        List<Object> polyContents = this.contents;
        List<Object> anotherPolyContents = anotherPoly.contents;
        List<Object> productFinal = new ArrayList();
        
        for (Object content: polyContents) {
            if (content instanceof Term) {
                Term toMultiply = (Term)content;
                List<Object> product = new ArrayList();
                for (int i=0; i < anotherPolyContents.size(); i++) {
                    Object object = anotherPolyContents.get(i);
                    if (object instanceof Term) {
                        Term objectTerm = (Term)object;
                        if (i!=0) {
                            product.add("+");
                        }
                        product.add(toMultiply.multiply(objectTerm).convertToTerm());
                    }
                }
                for (Object contentProd: product) {
                    productFinal.add(contentProd);
                }
            } else {
                productFinal.add(content);
            }
        }
        Polynomial result = new Polynomial(productFinal);
        result = result.simplify();
        return result;
    }
    
    public Polynomial simplify() {
        List<Object> arranged = new ArrayList();
        boolean found;
        for (Object object: this.contents) {
            found = false;
            if (object instanceof Term) {
                Term currTerm = (Term)object;
                for (int i=0; i < arranged.size(); i++) {
                    Object objectArr = arranged.get(i);
                    if (objectArr instanceof Term) {
                        Term currTermArr = (Term)objectArr;
                        if (currTerm.isValid(currTermArr)) {
                            currTermArr = currTerm.add(currTermArr).convertToTerm();
                            arranged.set(i, currTermArr);
                            found = true;
                        }
                    }     
                }          
                if (!found) {
                    arranged.add(object);
                }
            } else { //operator
               arranged.add(object);
            }
        }

        // remove extra operators at the end
        boolean done = false;
        int j = arranged.size()-1;
        while (!done) {
            Object currItem = arranged.get(j);
            if (currItem instanceof String) {
                arranged.remove(j);
            } else {
                done = true;
            }
            j--;
        }
        
        Polynomial arrangedPoly = new Polynomial(arranged);
        return arrangedPoly;
    }
    
    public boolean isTerm() {
        if (this.contents.size() == 1) {
            return true;
        } else {
            return false;
        }
    }
    
    public Term convertToTerm() {
        Term term = (Term)this.contents.get(0);
        return term;
    }
    
    public boolean isOperator(String item) {
        if (item.equals("+") || item.equals("-") || item.equals("*") || item.equals("/")) {
            return true;
        } else {
            return false;
        }
    }
    
    //updates coefficient for each term
    public Polynomial updateCoefficients() {
        List<Object> updated = new ArrayList();
        for (Object object: this.contents) {
            if (object instanceof Term) {
                Term currTerm = (Term)object;
                currTerm.updateCoefficient();
                updated.add(currTerm);
            } else {
                updated.add(object);
            }
        }
        Polynomial updatedPoly = new Polynomial(updated);
        return updatedPoly;
    }
    
    @Override
    public String toString() {
        String toReturn = "";
        for (int i=0; i < this.contents.size(); i++) {
            Object currItem = this.contents.get(i);
            if (currItem instanceof String) {
                toReturn = toReturn + currItem + " ";
            } else {
                Term currTerm = (Term)currItem;
                toReturn = toReturn + currTerm + " ";
            }
        }
        return toReturn;
    }
}
    

