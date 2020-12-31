package analiseSintaticoSemantico;

/**
 *
 * @author filipe
 */
public class ErroParserException extends Exception {
    public ErroParserException(String msg, int line, int column) {
        super("ERRO na linha " + line + ", coluna " + column + ": " + msg);
    }

    ErroParserException(String saddsa) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
