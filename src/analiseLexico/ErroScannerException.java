package analiseLexico;

/**
 *
 * @author filipe
 */
public class ErroScannerException extends Exception {
    public ErroScannerException(String message, StringBuffer lexema, int line, int column) {
        super("ERRO na linha: " + line + ", coluna: " + column + ", token lido " + lexema + ": " + message);
    }
}
