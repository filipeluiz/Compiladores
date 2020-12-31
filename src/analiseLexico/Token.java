package analiseLexico;

/**
 *
 * @author filipe
 */
public class Token {
    private int typeToken;
    private StringBuffer lexema;
    
    public Token() {}
    
    public Token(int type, StringBuffer lexema) {
        this.typeToken = type;
        this.lexema = lexema;
    }

    public int getTypeToken() {
        return typeToken;
    }

    public void setTypeToken(int typeToken) {
        this.typeToken = typeToken;
    }

    public StringBuffer getLexema() {
        return lexema;
    }

    public void setLexema(StringBuffer lexema) {
        this.lexema = lexema;
    }
    
    @Override
    public String toString() {
        return "< " + this.typeToken + " , " + this.lexema + " >";
    } 
}
