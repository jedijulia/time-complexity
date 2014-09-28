/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package up.cmsc142.julia.TimeComplexity2Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Polynomial {
    String equation;
    List<Object> contents = new ArrayList<Object>();
    
    public Polynomial(List<Object> contents) {
        this.contents = contents;
    }
    
    
    private boolean isOperator(String item) {
        if (item.equals("+") || item.equals("-") || item.equals("*") || item.equals("/")) {
            return true;
        } else {
            return false;
        }
    }
}
    
