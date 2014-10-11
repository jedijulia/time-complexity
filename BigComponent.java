package up.cmsc142.julia.TimeComplexityFinal;

//component that holds all the contents of the file
public class BigComponent extends Component {
    
    public BigComponent(String contents) {
        this.contents = contents;
        this.parse(this.contents);
    }
    
    @Override
    public Polynomial getTOfN() {
        return this.children.get(0).getTOfN();
    }
    
}
