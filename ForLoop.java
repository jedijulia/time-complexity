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
            Polynomial upperPoly = new Polynomial("");
            Polynomial lowerPoly = new Polynomial("");
            if (comparator.contains("<")) {
                String upper = conditionSplit[1].replace(';', ' ').trim();
                upperPoly = new Polynomial(upper);
                upperPoly = this.getVExtra(conditionLeft, v, upperPoly);
                lowerPoly = new Polynomial(this.getInitBound(initialization, v));
                if (!comparator.contains("=")) {
                    Term negativeOne = new Term("-1");
                    upperPoly.add(negativeOne.convertToPolynomial());
                }
                if (incdec.contains("-") || incdec.contains("/")) {
                    System.out.println("THIS IS AN INFINITE LOOP!");
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
            } else {
                lowerPoly = new Polynomial(conditionSplit[1].replace(';', ' ').trim());
                upperPoly = new Polynomial(this.getInitBound(initialization, v)); 
                if (!comparator.contains("=")) {
                    System.out.println("this happened!!!!!");
                    Term negativeOne = new Term("-1");
                    upperPoly.add(negativeOne.convertToPolynomial());
                    System.out.println("upperPoly: " + upperPoly);
                }
                if (incdec.contains("+") || incdec.contains("*")) {
                    System.out.println("THIS IS AN INFINITE LOOP!");
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

    
    public Polynomial getSummation(Polynomial constant) {
        // constant: constant * (upper bound - lower bound + 1)
        List<Object> toSum = new ArrayList<Object>();
        
        //CHECK THIS
        Term lowerBoundTerm = this.lowerBound.convertToTerm();
        String content = lowerBoundTerm.term;
        content = "-" + content;
        this.lowerBound = new Polynomial(content);
        Term oneTerm = new Term("1");
        
        //adds objects to toSum in postfix notation
        System.out.println("this was your constant: " + constant);
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
    
    
    @Override
    public Polynomial getTOfN() {
        // summation of getChildrenCount + count of condiition + count of incdec
        // t of n = summation + count of init + count of condition
        List<Integer> counts = this.setBoundsGetCounts();
        int initCount = counts.get(0);
        int conditionCount = counts.get(1);
        int incdecCount = counts.get(2);
        int childrenCount = this.getChildrenCount();
        Polynomial complexity = new Polynomial("");
        System.out.println("initCount: " + initCount);
        System.out.println("conditionCount: " + conditionCount);
        System.out.println("incdecCount: " + incdecCount);
        System.out.println("childrenCount: " + childrenCount);
        System.out.println("UPPER BOUND: " + this.upperBound);
        System.out.println("LOWER BOUND: " + this.lowerBound);
        
        if (childrenCount == 0 && this.noForLoopChildren()) {
            complexity = new Polynomial("" + (initCount + conditionCount));
        } else {
            String constant = "" + (this.getChildrenCount() + conditionCount + incdecCount);
            //Term constantTerm = new Term(constant);
            Polynomial constantPoly = new Polynomial(constant);
            if (!this.noForLoopChildren()) {
                for (Component child: this.children) {
                    constantPoly = constantPoly.add(child.getTOfN());
                }
            }
            System.out.println("constantPoly: " + constantPoly);
            Polynomial summation = this.getSummation(constantPoly);
            System.out.println("SUMMATION: " + summation);
            complexity = summation.add(new Term("" + (initCount + conditionCount)).convertToPolynomial());
        }
        return complexity;
    }

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
    
    public boolean noForLoopChildren() {
        for (Component child: this.children) {
            if (child instanceof ForLoop) {
                return false;
            }
        }
        return true;
    }
    
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
        System.out.println("extra: " + extra);
        
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
        System.out.println("upperPoly over here: " + upperPoly);
        return upperPoly;
    }
    
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
                currTerm.updateCoefficient();
            }
        }
    }
    
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
