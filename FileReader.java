package up.cmsc142.julia.TimeComplexityFinal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class FileReader {
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
