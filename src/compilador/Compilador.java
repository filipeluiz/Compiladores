 package compilador;

import analiseLexico.ErroScannerException;
import java.io.FileInputStream;
import java.io.IOException;
import analiseSintaticoSemantico.ErroParserException;
import analiseSintaticoSemantico.ErroSemanticException;
import analiseSintaticoSemantico.Parser;

/**
 *
 * @author filipe
 */
public class Compilador {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws analiseLexico.ErroScannerException
     * @throws analiseSintaticoSemantico.ErroParserException
     * @throws analiseSintaticoSemantico.ErroSemanticException
     */
    public static void main(String[] args) throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {
        FileInputStream file = new FileInputStream(args[0]);

        Parser parser = new Parser(file);
        parser.programa();
    }
}
