package up.cmsc142.julia.TimeComplexityFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public abstract class Component {
    String contents;
    List<Component> children = new ArrayList();
    
    public void parse(String toParse) {
        Stack stack = new Stack(); //for obtaining contents enclosed in {} if ever
        boolean forPart = false; //true if currently inside the '()' of for()
        int beginIndex = 0;
        for (int i = 0; i < toParse.length(); i++) { 
            char currChar = toParse.charAt(i);
            if (currChar == '(') {
                forPart = true;
            } else if (currChar == ')') {
                forPart = false;
            } else if (currChar == '{') {
                stack.push(currChar);
            } else if (currChar == '}') {
                stack.pop();
                //System.out.println("stack here: " + stack);
                if (stack.isEmpty()) {
                    //get contents inside for { } and create ForLoop component
                    //System.out.println("this happened");
                    String childContents = toParse.substring(beginIndex, i);
                    //System.out.println("child contents:" + childContents);
                    beginIndex = i+1;
                    Component child = new ForLoop(childContents);
                    this.children.add(child);
                }
            } else if (currChar == ';' && !forPart && stack.isEmpty()) {
                //get contents of the component
                String childContents = toParse.substring(beginIndex, i+1);
                beginIndex = i+1;
                //if contents contain 'for', create ForLoop Component
                if (childContents.contains("for")) {
                    Component child = new ForLoop(childContents);
                    this.children.add(child);
                //create Statement component
                } else {
                    Component child = new Statement(childContents);
                    this.children.add(child);
                }
            } 
        }
    }
    
    public Polynomial getTOfN() {
        return new Polynomial("");
    }
    
    public void print() {
        for (Component child: this.children) {
            child.print();
        }
    }
}

