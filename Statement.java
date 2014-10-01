package up.cmsc142.julia.TimeComplexityFinal;


public class Statement extends Component {
    
    public Statement(String contents) {
        this.contents = contents;
        this.contents = this.contents.trim();
        if (this.contents.endsWith(";")) {
            this.contents = this.contents.substring(0, this.contents.length()-1);
        }
    }
    
    @Override
    public void print() {
        System.out.println(this.contents);
    }
   
    public int getCount() {
        String statement = this.contents;
        int count = 0; 
        if (statement.contains("return")) {
            statement = statement.split("return")[1];
            count++;
        }
        for (int i=0; i < statement.length(); i++) {
            char currChar = statement.charAt(i);
            char nextChar = ' ';
            if (i+1 < statement.length()) {
                nextChar = statement.charAt(i+1);
            }
            if (this.isOperator(currChar)) {
                if (this.isOperator(nextChar)) {
                    i++;
                }
                count++;
            }
        }
        return count;
    }   
    
 
    private boolean isOperator(char c) {
        if ((c=='+') || (c=='-') || (c=='/') || (c=='*') || (c=='=') || (c=='%') || (c=='>') || (c=='<')) {
            return true;
        } else {
            return false;
        }
    }
}
