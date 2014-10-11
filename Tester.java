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
        List<String> toParse = new ArrayList();
        String currParse = "";
        for (int i=0; i < lines.size(); i++) {
            if (lines.get(i).trim().equals("")) {
                toParse.add(currParse);
                currParse = "";
            } else {
                currParse += lines.get(i);
            }
        }
        if (!currParse.equals("")) {
            toParse.add(currParse);
        }
        for (String item: toParse) {
            BigComponent component = new BigComponent(item);
            Polynomial TOfN = component.getTOfN();
            if (TOfN.convertToTerm().term.equals("InfiniteLoop")) {
              System.out.println("-------------------------------------------- T(n): INFINITE LOOP" );
            } else {
              System.out.println("-------------------------------------------- T(n): " + TOfN);
            }
        }
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
