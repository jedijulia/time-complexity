package up.cmsc142.julia.TimeComplexityFinal;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForLoop extends Component {
    List<String> forParts = new ArrayList();
    Polynomial upperBound = new Polynomial("");
    Polynomial lowerBound = new Polynomial("");
    String var = "";
    
    public ForLoop(String contents) {
        this.contents = contents;
        this.parse(this.process());
    }
    
    /* separates parts inside '()' of for() and stores them inside forParts
     * retrieves items inside the for loop which may or may not be surrounded
     * by {} and returns them as a single string to parse
     */
    private String process() {
        String forPart = "";
        String toParse = "";
        Pattern pattern = Pattern.compile("\\s*for\\s*\\(([^\\)]*)\\)\\s*\\{?(.*)\\}?\\s*$");
        Matcher matcher = pattern.matcher(this.contents);
        while (matcher.find()) {
            forPart = matcher.group(1);
            toParse = matcher.group(2);
        }
        toParse = toParse.trim();
        int beginIndex = 0;
        for (int i=0; i < forPart.length(); i++) {
            char currChar = forPart.charAt(i);
            if (currChar == ';') {
                if (beginIndex == (i)) {
                    this.forParts.add("" + currChar);
                } else {
                    String portion = forPart.substring(beginIndex, i+1);
                    this.forParts.add(portion);
                }
                beginIndex = i+1;
            }
            if (i==(forPart.length()-1)) {
                String portion = forPart.substring(beginIndex, i+1);
                portion = portion.trim();
                if (portion.equals("")) {
                    portion += ";";
                }
                this.forParts.add(portion);
            }
        }
        return toParse;
    }
    
    /* sets upper bound and lower bound of the for loop
     * return list containing counts of the initialization, condition, iteration statements
     */
    public List<Integer> setBoundsGetCounts() {
        List<Integer> counts = new ArrayList();
        String initialization = this.forParts.get(0);
        String condition = this.forParts.get(1);
        String incdec = this.forParts.get(2);
        
        Statement initStatement = new Statement(initialization);
        Statement conditionStatement = new Statement(condition);
        Statement incdecStatement = new Statement(incdec);
        
        counts.add(initStatement.getCount());
        counts.add(conditionStatement.getCount());
        counts.add(incdecStatement.getCount());
        
        if (counts.get(2) != 0) {
            String comparator = this.getComparator(condition);
            String conditionSplit[] = condition.split(comparator);
            String conditionLeft = conditionSplit[0].trim();
            String v = this.findV(conditionLeft);
            this.var = v.trim();
            Polynomial upperPoly = new Polynomial("");
            Polynomial lowerPoly = new Polynomial("");
            if (comparator.contains("<")) {
                String upper = conditionSplit[1].replace(';', ' ').trim();
                upperPoly = new Polynomial(upper);
                upperPoly = this.getVExtra(conditionLeft, v, upperPoly);
                lowerPoly = new Polynomial(this.getInitBound(initialization, v));
                
                //Check for whether loop will be executed
                if (upperPoly.contents.size()==1 && lowerPoly.contents.size()==1) {
                  if (upperPoly.convertToTerm().isNumeric() && lowerPoly.convertToTerm().isNumeric()) {
                    if (!comparator.contains("=")) {
                        if (Integer.parseInt(upperPoly.convertToTerm().term) < Integer.parseInt(lowerPoly.convertToTerm().term)) {
                          //Loop cannot be executed. Only initialization and condition will be counted
                          counts.add(-2);
                          return counts;
                        }
                    } else {
                        if (Integer.parseInt(upperPoly.convertToTerm().term) <= Integer.parseInt(lowerPoly.convertToTerm().term)) {
                          //Loop cannot be executed. Only initialization and condition will be counted
                          counts.add(-2);
                          return counts;
                        }
                    }
                  }
                }
                
                if (incdec.contains("-") || incdec.contains("/")) {
                    //INFINITE LOOP
                    counts.add(-1);
                    return counts;
                } else if (incdec.contains("+")){
                    String incdecResult = this.findIncDec(incdec, "+");
                    if (!incdecResult.equals("+")) {
                        this.setDenoms(upperPoly, incdecResult);
                    }
                } else if (incdec.contains("*")) {
                    String incdecResult = this.findIncDec(incdec, "*");
                    String upperPolyStr = upperPoly.toString().replace(" ", "");
                    upper = "log" + incdecResult + " " + upperPolyStr;
                    String split[] = upper.split("log");
                    String base = split[1].split(" ")[0];
                    String arg = split[1].split(" ")[1];
                    Term log = new Logarithm(base, arg);
                    upperPoly = log.convertToPolynomial();                
                } 
                if (!comparator.contains("=")) {
                    Term negativeOne = new Term("-1");
                    upperPoly.add(negativeOne.convertToPolynomial());
                }
            } else { // comparator contains ">"
                lowerPoly = new Polynomial(conditionSplit[1].replace(';', ' ').trim());
                upperPoly = new Polynomial(this.getInitBound(initialization, v)); 
                
                //Check for whether loop will be executed
                if (upperPoly.contents.size()==1 && lowerPoly.contents.size()==1) {
                  if (upperPoly.convertToTerm().isNumeric() && lowerPoly.convertToTerm().isNumeric()) {
                    if (!comparator.contains("=")) {
                      if (Integer.parseInt(upperPoly.convertToTerm().term) < Integer.parseInt(lowerPoly.convertToTerm().term)) {
                        //Loop cannot be executed. Only initialization and condition will be counted
                        counts.add(-2);
                        return counts;
                      }
                    } else {
                      if (Integer.parseInt(upperPoly.convertToTerm().term) <= Integer.parseInt(lowerPoly.convertToTerm().term)) {
                        //Loop cannot be executed. Only initialization and condition will be counted
                        counts.add(-2);
                        return counts;
                      }
                    }
                  }
                }
                
                if (incdec.contains("+") || incdec.contains("*")) {
                    //INFINITE LOOP
                    counts.add(-1);
                    return counts;
                } else if (incdec.contains("-")){
                    String incdecResult = this.findIncDec(incdec, "-");
                    if (!incdecResult.equals("-")) {
                        this.setDenoms(upperPoly, incdecResult);
                    }
                } else if (incdec.contains("/")) {
                    String incdecResult = this.findIncDec(incdec, "/");
                    String upperPolyStr = upperPoly.toString().replace(" ", "");
                    String upper = "log" + incdecResult + " " + upperPolyStr;
                    String split[] = upper.split("log");
                    String base = split[1].split(" ")[0];
                    String arg = split[1].split(" ")[1];
                    Term log = new Logarithm(base, arg);
                    upperPoly = log.convertToPolynomial();  
                } 
                if (!comparator.contains("=")) {
                    Term negativeOne = new Term("-1");
                    upperPoly.add(negativeOne.convertToPolynomial());
                }
            }
            this.upperBound = upperPoly;
            this.lowerBound = lowerPoly.simplify();
        }
        return counts;
    }
    
    private String getComparator(String condition) {
        if (condition.contains(">=")) {
            return ">=";
        } else if (condition.contains("<=")) {
            return "<=";
        } else if (condition.contains(">")) {
            return ">";
        } else if (condition.contains("<")) {
            return "<";
        } else if (condition.contains("!=")) {
            return "!=";
        } else {
            return "";
        }
    }
    
    //retrieves bound from the initialization statement
    private String getInitBound(String initialization, String var) {
        boolean varFound = false;
        boolean afterEquals = false;
        initialization = initialization.trim();
        String initBound = "";
                
        char currChar = initialization.charAt(0);
        int i = 0;
        while ((initialization.charAt(i) != ',') && (initialization.charAt(i) != ';')) {
            currChar = initialization.charAt(i);
            if (afterEquals) {
                initBound += currChar;
            }
            if (varFound && currChar == '=') {
                afterEquals = true;
            }
            if (currChar == var.charAt(0)) {
                varFound = true;
            }
            i++;
        }
        return initBound.trim();
    }

    // returns the summation of constant terms
    public Polynomial getSummation(Polynomial constant) {
        // constant: constant * (upper bound - lower bound + 1)
        List<Object> toSum = new ArrayList<Object>();
        this.lowerBound = this.lowerBound.toNegative();
        Term oneTerm = new Term("1");
        //adds objects to toSum in postfix notation
        toSum.add(constant);
        toSum.add(this.upperBound);
        toSum.add(this.lowerBound);
        toSum.add("+");
        toSum.add(oneTerm);
        toSum.add("+");
        toSum.add("*");
        
        Polynomial summation = new Polynomial(toSum);
        summation = summation.compute();
        return summation;
    } 
    
    // returns the summation of terms containing non-constants to be summed
    public Polynomial getSummationSpec(Polynomial constant, Polynomial upperPoly, Polynomial lowerPoly) {
        Polynomial finalResult = new Polynomial("");
        for(Object object: constant.contents) {
            if (object instanceof Term) {
                Term term = (Term)object;
                String notVarStr = "" + term.coefficient;
                // retrieves the portion of the term that is not the non-constant variable 
                for (Variable variable: term.variables) {
                    if (!variable.variable.equals(this.var)) {
                        notVarStr += variable.variable;
                        if (variable.exponent != 0) {
                            notVarStr += variable.exponent;
                        }
                    }
                }
                Term notVarTerm = new Term(notVarStr);
                // multiplies the notVarTerm with the summation of the non-constant variable
                if (finalResult.contents.isEmpty()) {
                    finalResult = notVarTerm.convertToPolynomial().multiply(this.getSummationSpecial(upperPoly, lowerPoly));
                } else {
                    finalResult = finalResult.add(notVarTerm.convertToPolynomial().multiply(this.getSummationSpecial(upperPoly, lowerPoly)));
                }
            }
        }
        return finalResult;
    }
    
    //returns the summation of a non-constant
    public Polynomial getSummationSpecial(Polynomial upperPol, Polynomial lowerPol) {
        Polynomial result = new Polynomial("");
        
        if (lowerPol.convertToTerm().term.equals("0")) {
            upperPol = upperPol.add(new Polynomial("1"));
            result = upperPol.multiply(upperPol);
            result = result.add(upperPol);
            this.setDenoms(result, "2");
        } else {
            result = upperPol.multiply(upperPol);
            result = result.add(upperPol);
            Polynomial lowerSquared = lowerPol.multiply(lowerPol).toNegative();
            result = result.add(lowerSquared);
            result = result.add(lowerPol);
            this.setDenoms(result, "2");
        }
        return result;
    }
    
    
    @Override
    public Polynomial getTOfN() {
        // summation of getChildrenCount + count of condiition + count of incdec
        // t of n = summation + count of init + count of condition
        List<Integer> counts = this.setBoundsGetCounts();
        
        //infinite loop check
        if (counts.contains(-1)) {
          return new Polynomial("InfiniteLoop");
        }
        
        int initCount = counts.get(0);
        int conditionCount = counts.get(1);
        int incdecCount = counts.get(2);
        int childrenCount = this.getChildrenCount();
        
        //check if loop will be executed
        if (counts.contains(-2)) {
          return new Polynomial("" + (initCount + conditionCount));
        }
        Polynomial complexity = new Polynomial("");
        
        if (initCount == 0 || conditionCount == 0 || incdecCount == 0){
            return new Polynomial("InfiniteLoop");
        }
        
        //checks if loop has a 0 childrenCount (count of statements) and no inner for loops
        if (childrenCount == 0 && this.noForLoopChildren()) {
            //checks if loop runs but has no body
            if (!this.upperBound.contents.isEmpty() && !this.lowerBound.contents.isEmpty()) {
                String constant = "" + (this.getChildrenCount() + conditionCount + incdecCount);
                Polynomial constantPoly = new Polynomial(constant);
                Polynomial summation = this.getSummation(constantPoly);
                complexity = summation.add(new Term("" + (initCount + conditionCount)).convertToPolynomial());
            } else { //just add counts of initialization and condition statements
                complexity = new Polynomial("" + (initCount + conditionCount));
            }
        } else {
            String constant = "" + (this.getChildrenCount() + conditionCount + incdecCount);
            Polynomial constantPoly = new Polynomial(constant);
            if (!this.noForLoopChildren()) {
                for (Component child: this.children) {
                    Polynomial childTOfN = child.getTOfN();
                    //infinite loop check
                    if ((!childTOfN.contents.isEmpty()) && (childTOfN.convertToTerm().term.equals("InfiniteLoop"))) {
                      return new Polynomial("InfiniteLoop");
                    }
                    constantPoly = constantPoly.add(childTOfN);
                }
            }
            Polynomial summation = new Polynomial("");
            
            //checks if constantPoly (polynomial to be summed) has a non-constant
            //aka the variable concerned / the iterator 
            if (constantPoly.hasVar(var)) {
                List<Object> regSum = new ArrayList();
                List<Object> specSum = new ArrayList();
                //the terms containing the non-constant are added to specSum
                //those that do not are added to regSum
                for (int i=0; i < constantPoly.contents.size(); i++) {
                    Object object = constantPoly.contents.get(i);
                    if (object instanceof Term) {
                        Term term = (Term)object;
                        if (term.hasVar(var)) {
                            if (!specSum.isEmpty()) {
                                specSum.add("+");
                            }
                            specSum.add(object);
                        } else {
                            if (!regSum.isEmpty()) {
                                regSum.add("+");
                            }
                            regSum.add(object);
                        }
                    } 
               }
                //summations of regPoly and specPoly are added and stored as the summation
                Polynomial regPoly = new Polynomial(regSum);
                Polynomial specPoly = new Polynomial(specSum);
                Polynomial upperPoly = new Polynomial("");
                Polynomial lowerPoly = new Polynomial("");
                upperPoly = upperPoly.clone(this.upperBound);
                lowerPoly = lowerPoly.clone(this.lowerBound);
                Polynomial regPolySum = this.getSummation(regPoly);
                Polynomial specPolySum = this.getSummationSpec(specPoly, upperPoly, lowerPoly);
                summation = regPolySum.add(specPolySum);
            }
            else {
                summation = this.getSummation(constantPoly);
            }
            //complexity is computed
            complexity = summation.add(new Term("" + (initCount + conditionCount)).convertToPolynomial());
            complexity = complexity.clean(); //remove terms with 0 as coefficient
            complexity = complexity.updateCoefficients(); //update coefficients accdng to denominators 
            complexity = complexity.simplify(); 
            complexity = complexity.clean();
        }
        return complexity;
    }
    
    //returns the count of the loop's children that are statements
    public int getChildrenCount() {
        int count = 0;
        for (Component child: this.children) {
            if (child instanceof Statement) {
                Statement childStatement = (Statement)child;
                count += childStatement.getCount();
            }
        }
        return count;
    }
    
    //returns true if the loop has no children that are for loops
    public boolean noForLoopChildren() {
        for (Component child: this.children) {
            if (child instanceof ForLoop) {
                return false;
            }
        }
        return true;
    }
    
    //retrieves the statement surrounding the variable (iterator) involved
    public Polynomial getVExtra(String s, String v, Polynomial upperPoly) {
        Polynomial vExtra = new Polynomial("0");
        String[] split = s.split(v);
        if (split.length == 0) {
            return upperPoly;
        }
        String extra = "";
        for (int i=0; i < split.length; i++) {
            if (!split[i].contains(v)) {
                extra += split[i];
            }
        }
        String operator = "";
        String number = "";
        for (int j=0; j < extra.length(); j++) {
            char currChar = extra.charAt(j);
            if (currChar != ' ') {
                if (this.isOperator(currChar)) {
                    if (!number.equals("")) {
                        vExtra = new Polynomial(number);
                        upperPoly = this.performOperation(upperPoly, vExtra, this.reverseOperator(operator));
                        number = "";
                    }
                    operator = "" + currChar;
                } else {
                    number += currChar;
                }
            }
        }
        if (!number.equals("")) {
            vExtra = new Polynomial(number);
            upperPoly = this.performOperation(upperPoly, vExtra, this.reverseOperator(operator));
        }
        return upperPoly;
    }
    
    //returns the variable involved / variable in condition / variable that is the iterator
    public String findV(String s) {
        for (int i=0; i < s.length(); i++) {
            char currChar = s.charAt(i);
            if (Character.isLetter(currChar)) {
                return "" + currChar;
            }
        }
        return "";
    }
    
    public String findIncDec(String incdec, String op) {
        int index = incdec.indexOf(op);
        String sub = incdec.substring(index+1, incdec.length());
        if (sub.contains("=")) {
            sub = sub.replace("=", "");
            sub = sub.trim();
        }
        return sub;
    }
    
    
    //sets the denominator of each of the terms inside the Polynomial
    public void setDenoms(Polynomial poly, String denom) {
        for (Object object: poly.contents) {
            if (object instanceof Term) {
                Term currTerm = (Term)object;
                currTerm.setDenom(Integer.parseInt(denom));
                if (currTerm.coefficient != 0) {
                    currTerm.updateCoefficient();
                }
            }
        }
    }
    
    //performs a given operation on two polynomials and returns the result
    public Polynomial performOperation(Polynomial poly, Polynomial anotherPoly, String operator) {
        Polynomial result = new Polynomial("");
        Term polyTerm = anotherPoly.convertToTerm();
        String content = polyTerm.term;
        if (operator.equals("+")) {
            result = poly.add(anotherPoly);
        } else if (operator.equals("-")) {
            content = "-" + content;
            anotherPoly = new Polynomial(content);
            result = poly.add(anotherPoly);
        } else if (operator.equals("*")) {
            poly.multiply(anotherPoly);
        } else { //division
            this.setDenoms(poly, content);
            return poly;
        }
        return result;
    }
    
    private boolean isOperator(char c) {
        if (c == '+' || c == '-' || c == '*' || c == '/') {
            return true;
        } else {
            return false;
        }
    }
    
    private String reverseOperator(String operator) {
        if (operator.equals("+")) {
            return "-";
        } else if (operator.equals("-")) {
            return "+";
        } else if (operator.equals("*")) {
            return "/";
        } else { // '/'
            return "*";
        }
    }
    
    @Override
    public void print() {
        System.out.println("for loop:");
        for (String forPart: forParts) {
            System.out.println(forPart);
        }
        System.out.println("inside for loop:");
        for (Component child: this.children) {
            child.print();
        }
        System.out.println("end of for loop");
    }
}
