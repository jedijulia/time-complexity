package up.cmsc142.julia.TimeComplexityFinal;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tester {
    
    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader();
        List<String> lines = fileReader.readlines("input10.in");
        String toParse = "";
        for (int i=0; i < lines.size(); i++) {
            toParse += lines.get(i);
        }
        
        BigComponent component = new BigComponent(toParse);
        //component.print();
        System.out.println(component.getTOfN());    
       
        
//        List<Object> polyContents = new ArrayList();
////        Term one = new Term("3n2");
////        Term two = new Term("4");
////        Term three = new Term("x");
////        polyContents.add(one);
////        polyContents.add("+");
////        polyContents.add(two);
////        polyContents.add("+");
////        polyContents.add(three);
//        Term one = new Term("6");
//        polyContents.add(one);
//        Polynomial first = new Polynomial(polyContents);
//        
//        System.out.println("BEFORE\n");
//        
//        System.out.println("*First*");
//        for (int i=0; i < first.contents.size(); i++) {
//            System.out.println("curr i: " + i);
//            Object currItem = first.contents.get(i);
//            if (currItem instanceof String) {
//                System.out.println(currItem);
//            } else {
//                Term currTerm = (Term)currItem;
//                System.out.println(currTerm.term);
//            }
//        }
//        
//        List<Object> polyContentsTwo = new ArrayList();
//        Term four = new Term("n2");
//        Term five = new Term("3n");
//        Term six = new Term("5x");
//        Term seven = new Term("1");
//        polyContentsTwo.add(four);
//        polyContentsTwo.add("+");
//        polyContentsTwo.add(five);
//        polyContentsTwo.add("+");
//        polyContentsTwo.add(six);
//        polyContentsTwo.add("+");
//        polyContentsTwo.add(seven);
//        Polynomial second = new Polynomial(polyContentsTwo);
//        
//        System.out.println("*Second*");
//        for (int i=0; i < second.contents.size(); i++) {
//            System.out.println("curr i: " + i);
//            Object currItem = second.contents.get(i);
//            if (currItem instanceof String) {
//                System.out.println(currItem);
//            } else {
//                Term currTerm = (Term)currItem;
//                System.out.println(currTerm.term);
//            }
//        }
//        
//        System.out.println("-------------------------\n");
//        
//        Polynomial result = first.multiply(second);
//        
//        System.out.println("\n------------------------\nAFTER\n");
//        
//        for (int i=0; i < result.contents.size(); i++) {
//            System.out.println("curr i: " + i);
//            Object currItem = result.contents.get(i);
//            if (currItem instanceof String) {
//                System.out.println(currItem);
//            } else {
//                Term currTerm = (Term)currItem;
//                System.out.println(currTerm.term);
//            }
//        }
    }
}
class FileReader {
    public List<String> readlines(String filename) throws IOException{
        Scanner reader = new Scanner(new File(filename));
        List<String> lines = new ArrayList<String>();
        while (reader.hasNextLine()) {
            lines.add(reader.nextLine());
        }
        reader.close();
        return lines;
    }
}