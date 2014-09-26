package up.cmsc142.julia.TimeComplexityFinal;


public class Statement extends Component {
    
    public Statement(String contents) {
        this.contents = contents;
        this.contents = this.contents.trim();
    }
    
    @Override
    public void print() {
        System.out.println(this.contents);
    }
    
    
    private boolean isOperator(char c) {
        if ((c=='+') || (c=='-') || (c=='/') || (c=='*') || (c=='=')) {
            return true;
        } else {
            return false;
        }
    }
}
