package up.cmsc142.julia.TimeComplexityFinal;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForLoop extends Component {
    List<String> forParts = new ArrayList();
    String upperBound = "";
    String lowerBound = "";
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
        if (toParse.endsWith("}")) {
            toParse = toParse.substring(0, toParse.length()-1);
        }
        
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
        
        // sets bounds only if incdec statement exists
        if (counts.get(2) != 0) {
            // check condition if less than or greater than 
            // if less than: initialization is lower, condition is upper
            // if not: condition is lower, initialization is upper
            String lower;
            String upper;
            String comparator = this.getComparator(condition);

            String conditionSplit[] = condition.split(comparator);
            String conditionLeft = conditionSplit[0].trim();
            String v = this.findV(conditionLeft);
            
            if (comparator.contains("<")) {
                upper = conditionSplit[1].replace(';', ' ').trim() + getVExtra(conditionLeft, v);
                lower = this.getInitBound(initialization, v);
                if (!comparator.contains("=")) {
                    upper += "-1";
                }
                if (incdec.contains("-")) {
                    System.out.println("THIS IS AN INFINITE LOOP!");
                } else if (incdec.contains("+")){
                    String incdecResult = this.findIncDec(incdec, "+");
                    if (!incdecResult.equals("+")) {
                        upper = upper + "/" + incdecResult;
                    }
                } else if (incdec.contains("*")) {
                    String incdecResult = this.findIncDec(incdec, "*");
                    upper = "log" + incdecResult + " " + upper;
                } 
            } else {
                lower = conditionSplit[1].replace(';', ' ').trim();
                upper = this.getInitBound(initialization, v); 
                if (!comparator.contains("=")) {
                    upper += "-1";
                }
                if (incdec.contains("+")) {
                    System.out.println("THIS IS AN INFINITE LOOP!");
                } else if (incdec.contains("-")){
                    String incdecResult = this.findIncDec(incdec, "-");
                    if (!incdecResult.equals("-")) {
                        upper = upper + "/" + incdecResult;
                    }
                } else if (incdec.contains("/")) {
                    String incdecResult = this.findIncDec(incdec, "/");
                    upper = "log" + incdecResult + " " + upper;
                } 
            }
            this.var = v;
            this.lowerBound = lower;
            this.upperBound = upper;
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

    
    public Polynomial getSummation(Term constant) {
        // constant: constant * (upper bound - lower bound + 1)
        List<Object> toSum = new ArrayList<Object>();
        Polynomial upperBoundPoly;
        
        //upper = "log" + incdecResult + " " + upper;
        if (this.upperBound.contains("log")) {
            String split[] = this.upperBound.split("log");
            String base = split[1].split(" ")[0];
            String arg = split[1].split(" ")[1];
            Term log = new Logarithm(base, arg);
            upperBoundPoly = log.convertToPolynomial();
        }
        
        else if (this.upperBound.contains("/")) {
            String denom = getDenomSetUpper();
            upperBoundPoly = new Polynomial(this.upperBound);
            this.setDenoms(upperBoundPoly, denom);
        }
        else {
            upperBoundPoly = new Polynomial(this.upperBound);
        }
        
        Polynomial lowerBoundPoly = new Polynomial("-" + this.lowerBound);
        Term oneTerm = new Term("1");
        
        //adds objects to toSum in postfix notation
        toSum.add(constant);
        toSum.add(upperBoundPoly);
        toSum.add(lowerBoundPoly);
        toSum.add("+");
        toSum.add(oneTerm);
        toSum.add("+");
        toSum.add("*");
        
        Polynomial summation = new Polynomial(toSum);
        
        summation = summation.compute();
        return summation;
    } 
    
    
    @Override
    public String getTOfN() {
        // summation of getChildrenCount + count of condiition + count of incdec
        // t of n = summation + count of init + count of condition
        List<Integer> counts = this.setBoundsGetCounts();
        int initCount = counts.get(0);
        int conditionCount = counts.get(1);
        int incdecCount = counts.get(2);
        int childrenCount = this.getChildrenCount();
        
        if (childrenCount == 0) {
            return "" + (initCount + conditionCount);
        } else {
            String constant = "" + (this.getChildrenCount() + conditionCount + incdecCount);
            Term constantTerm = new Term(constant);
            Polynomial summation = this.getSummation(constantTerm);
            Polynomial complexity = summation.add(new Term("" + (initCount + conditionCount)).convertToPolynomial());
            return complexity.toString();
        }
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
    
    public String getVExtra(String s, String v) {
        String[] split = s.split(v);
        if (split.length == 0) {
            return "";
        }
        String extra = "";
        for (int i=0; i < split.length; i++) {
            if (!split[i].contains(v)) {
                extra += split[i];
            }
        }
        
        String extraRev = "";
        for (int j=0; j < extra.length(); j++) {
            char currChar = extra.charAt(j);
            if (currChar == '+') {
                extraRev += '-';
            } else if (currChar == '-') {
                extraRev += '+';
            } else {
                extraRev += currChar;
            }
        }
        return extraRev;
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
    
    // returns the denominator, sets the upperbound 
    public String getDenomSetUpper() {
        String[] split = this.upperBound.split("/");
        String denom = split[1];
        denom = denom.trim();
        this.upperBound = split[0].trim();
        return denom;
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
