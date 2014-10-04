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
        System.out.println(component.getTOfN());  
        
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
