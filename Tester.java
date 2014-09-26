package up.cmsc142.julia.TimeComplexityFinal;

import java.io.IOException;
import java.util.List;


public class Tester {
    
    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader();
        List<String> lines = fileReader.readlines("input8.in");
        String toParse = "";
        for (int i=0; i < lines.size(); i++) {
            toParse += lines.get(i);
        }
        
        BigComponent component = new BigComponent(toParse);
        component.print();
    }
}
