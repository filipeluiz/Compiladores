/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analiseSintaticoSemantico;

/**
 *
 * @author filipe
 */
public class ErroSemanticException extends Exception {
     public ErroSemanticException(String message, StringBuffer lexema, int line, int column) {
        super("ERRO na linha: " + line + ", coluna: " + column + ", ultimo token lido '" + lexema + "' : " + message);
    }    
}
