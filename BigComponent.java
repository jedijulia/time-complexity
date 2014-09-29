package up.cmsc142.julia.TimeComplexityFinal;

//component that holds all the contents of the file

import up.cmsc142.julia.TimeComplexity2Mod3.*;

public class BigComponent extends Component {
    
    public BigComponent(String contents) {
        this.contents = contents;
        this.parse(this.contents);
    }
    
    public String getTOfN() {
        return this.children.get(0).getTOfN();
    }
}
