package up.cmsc142.julia.TimeComplexityFinal;

import up.cmsc142.julia.TimeComplexity2Mod3.*;
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
            String v = conditionSplit[0].trim();

            if (comparator.contains("<")) {
                upper = conditionSplit[1].replace(';', ' ').trim();
                lower = this.getInitBound(initialization, v);
                if (!comparator.contains("=")) {
                    lower = "" + (Integer.parseInt(lower) + 1);
                }
            } else {
                lower = conditionSplit[1].replace(';', ' ').trim();
                upper = this.getInitBound(initialization, v);
                if (!comparator.contains("=")) {
                    lower = "" + (Integer.parseInt(lower) + 1);
                }
            }
            this.var = v;
            this.lowerBound = lower;
            this.upperBound = upper;
            System.out.println("upper: " + this.upperBound);
            System.out.println("lower: " + this.lowerBound);
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
        } else {
            return "";
        }
    }
    
    private String getInitBound(String initialization, String var) {
        boolean varFound = false;
        boolean afterEquals = false;
        initialization = initialization.trim();
        if (!initialization.equals(";")) {
            for (int i=0; i < initialization.length(); i++) {
                char currChar = initialization.charAt(i);
                if (afterEquals && currChar != ' ') {
                    return "" + currChar;
                }
                if (varFound && currChar == '=') {
                    afterEquals = true;
                }
                if (currChar == var.charAt(0)) {
                    varFound = true;
                }
            }
        }
        return "";
    }
    
    public Polynomial getSummation(Term constant) {
        // constant: constant * (upper bound - lower bound + 1)
        List<Object> toSum = new ArrayList<Object>();
        Term upperBoundTerm = new Term(this.upperBound);
        Term lowerBoundTerm = new Term("-" + this.lowerBound);
        Term oneTerm = new Term("1");
        
        System.out.println("THIS IS CONSTANT: " + constant.term);
        toSum.add(constant);
        toSum.add(upperBoundTerm);
        toSum.add(lowerBoundTerm);
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
        
        System.out.println("init: " + initCount);
        System.out.println("condition: " + conditionCount);
        System.out.println("incdec: " + incdecCount);
        System.out.println("children: " + childrenCount);
        System.out.println("----------\n");
        
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
