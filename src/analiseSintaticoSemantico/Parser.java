/*

<decl_var> 	  ->   <tipo> <id> {,<id>}* ";"
<tipo> 		  ->   int | float | char
------------------------------------------------------------------------------------
<programa>        ->   int main"("")" <bloco>
<bloco>           ->   “{“ {<decl_var>}* {<comando>}* “}”
<comando>         ->   <comando_básico> | <iteração> | if "("<expr_relacional>")" <comando> {else <comando>}?
<comando_básico>  ->   <atribuição> | <bloco>
<iteração>        ->   while "("<expr_relacional>")" <comando> | do <comando> while "("<expr_relacional>")"";"
<atribuição>      ->   <id> "=" <expr_arit> ";"
<expr_relacional> ->   <expr_arit> <op_relacional> <expr_arit>
-------------------------------------------------------------------------------------
Eliminar a recursão à esquerda abaixo:
<expr_arit>       ->   <expr_arit> "+" <termo>   | <expr_arit> "-" <termo> | <termo>
<termo>           ->   <termo> "*" <fator> | <termo> “/” <fator> | <fator>

Já eliminado a recursão à esquerda:
<expr_arit>       ->   <termo> <expr_arit2> 
<expr_arit2>      ->  "+" <termo> <expr_arit2> | "-" <termo> <expr_arit2> | ε
<termo>           ->  <fator> <termo2> 
<termo2>          -> "*" <fator> <termo2> | “/” <fator> <termo2> | ε
------------------------------------------------------------------------------------
<fator>           ->   “(“ <expr_arit> “)” | <id> | <float> | <inteiro> | <char>

*/

package analiseSintaticoSemantico;

import geradorCodigoIntermediario.GCI;
import analiseLexico.Classification;
import analiseLexico.ErroScannerException;
import analiseLexico.Scanner;
import analiseLexico.Token;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author filipe
 */
public class Parser {
    private Token lk;
    private Scanner scanner;
    private GCI gci;
    private SymbolTable table;
    private Symbol symbols;
    private int scope, tp, ep;
    
    public Parser(FileInputStream file) throws IOException {
        this.scanner = new Scanner(file);
        this.gci = new GCI();
        this.table = new SymbolTable();
    }
    
    public void nextToken() throws IOException, ErroScannerException {
        this.lk = this.scanner.scan();
    }
    
    private int tipo() throws ErroParserException, IOException, ErroScannerException {
        int type;
        if((this.lk.getTypeToken() == Classification.INT.ordinal()) || (this.lk.getTypeToken() == Classification.FLOAT.ordinal()) || (this.lk.getTypeToken() == Classification.CHAR.ordinal())){
            // sucesso
            type = this.lk.getTypeToken();
            this.nextToken();  
        }
        else {
            //erro
            throw new ErroParserException("Uso de identificador não declarado" + " " + this.lk, this.scanner.getLine(), this.scanner.getColumn());
        }
        return type;
    }
    
    private void id() throws IOException, ErroScannerException, ErroParserException {
        if(this.lk.getTypeToken() != Classification.ID.ordinal()){
            throw new ErroParserException("Não encontrado identificador", this.scanner.getLine(), this.scanner.getColumn());            
        }      
        this.nextToken();
    }

    private void declVar() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException { // Tudo ok
        //<decl_var> -> <tipo> <id> {,<id>}* ";"
        // exemplo int x; ou int x,y,z;
        this.symbols = new Symbol(); 
        StringBuffer erro;
        
        int type = this.tipo(); 
        this.symbols.setType(type);
        this.symbols.setScope(this.scope);
        this.symbols.setLexema(this.lk.getLexema());
        
        erro = this.lk.getLexema();

        this.id();

        if(this.table.isAlreadyDeclaredScope(this.symbols, this.scope)) {
            throw new ErroSemanticException("Variável já declarada", erro, this.scanner.getLine(), this.scanner.getColumn());
        }
        else {
            this.table.push(this.symbols);
        }

        while(this.lk.getTypeToken() == Classification.VIRGULA.ordinal()){
            this.symbols = new Symbol();             
            this.nextToken();
            this.symbols.setType(type);
            this.symbols.setLexema(this.lk.getLexema());
            this.symbols.setScope(this.scope); 
            erro = this.lk.getLexema();
            this.id();
            
            if(this.table.isAlreadyDeclaredScope(this.symbols, this.scope)) {
                throw new ErroSemanticException("Variável já declarada", erro, this.scanner.getLine(),this.scanner.getColumn());
            }
            else {
                this.table.push(this.symbols);
            }            
        } 
        
//        System.out.println(this.table.getTable());

        if(this.lk.getTypeToken() == Classification.PONTOVIRGULA.ordinal()) {
            this.nextToken();
        }
        else {
            throw new ErroParserException("Não encontrado ';'", this.scanner.getLine(), this.scanner.getColumn());            
        }   
    }

    public void programa() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {
        // <programa> -> int main"("")" <bloco>
        
        this.nextToken();
        if(this.lk.getTypeToken() != Classification.INT.ordinal()){
            throw new ErroParserException("Não encontrada palavra reserva int", this.scanner.getLine(), this.scanner.getColumn());
        }

        this.nextToken();
        if(this.lk.getTypeToken() != Classification.MAIN.ordinal()){
            throw new ErroParserException("Não encontrada palavra reserva main", this.scanner.getLine(), this.scanner.getColumn());
        }        
        
        this.nextToken();
        if(this.lk.getTypeToken() != Classification.ABERTAPARENTESES.ordinal()){
            throw new ErroParserException("Não encontrado '('", this.scanner.getLine(), this.scanner.getColumn());
        }
        
        this.nextToken();
        if(this.lk.getTypeToken() != Classification.FECHAPARENTESES.ordinal()){
            throw new ErroParserException("Não encontrado ')'", this.scanner.getLine(), this.scanner.getColumn());
        }  
        
        this.nextToken();   
        this.bloco(); 
        if(this.lk.getTypeToken() != Classification.$.ordinal()){
            throw new ErroParserException("Erro fora de bloco", this.scanner.getLine(), this.scanner.getColumn());            
        }
    }  
    
    private void bloco() throws ErroParserException, IOException, ErroScannerException, ErroSemanticException { // Tudo ok
        //<bloco> -> “{“ {<decl_var>}* {<comando>}* “}”

        if(this.lk.getTypeToken() != Classification.ABERTACHAVES.ordinal()) {
            throw new ErroParserException("Não encontrado '{'", this.scanner.getLine(), this.scanner.getColumn());            
        }
        
        this.scope++;
        this.nextToken(); 
 
        while((this.lk.getTypeToken() == Classification.INT.ordinal()) || 
              (this.lk.getTypeToken() == Classification.FLOAT.ordinal()) || 
              (this.lk.getTypeToken() == Classification.CHAR.ordinal())) {
            this.declVar();
        }       

        while(this.lk.getTypeToken() == Classification.ABERTACHAVES.ordinal() ||
              this.lk.getTypeToken() == Classification.ID.ordinal() || 
              this.lk.getTypeToken() == Classification.WHILE.ordinal() || 
              this.lk.getTypeToken() == Classification.DO.ordinal() ||
              this.lk.getTypeToken() == Classification.IF.ordinal()) {
            this.comando(); 
        }  
        
        if(this.lk.getTypeToken() != Classification.FECHACHAVES.ordinal()) {
            throw new ErroParserException("Não encontrado '}'", this.scanner.getLine(), this.scanner.getColumn());                     
        }

        if(this.scope > 1){ 
            while(this.table.getTable().lastElement().getScope() == this.scope) {
                if(this.table.getTable().size() > 1) {
                    this.table.pop();
                }                  
                else {
                    break;
                }
            }
        }

        this.scope--;
        this.nextToken();
    }
    
    private void comando() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {
        // <comando> -> <comando_básico> | <iteração> | if "("<expr_relacional>")" <comando> {else <comando>}?
        if(this.lk.getTypeToken() == Classification.ID.ordinal() || this.lk.getTypeToken() == Classification.ABERTACHAVES.ordinal()) {
            this.comandoBasico();  
        } 
        else if(this.lk.getTypeToken() == Classification.WHILE.ordinal() || this.lk.getTypeToken() == Classification.DO.ordinal()) {
            this.iteracao(); 
        }
        else if(this.lk.getTypeToken() == Classification.IF.ordinal()) {
            String label1, label2, label3;
            this.nextToken();
            if(this.lk.getTypeToken() != Classification.ABERTAPARENTESES.ordinal()){
                throw new ErroParserException("Não encontrado '('", this.scanner.getLine(), this.scanner.getColumn());            
            }     
            this.nextToken();
            this.exprRelacional(); 
            this.gci.incrementLabel();
            label1 = this.gci.getLabel();
            System.out.println("if " + gci.getTemporary() + " != 0" + " goto " + label1 + ";");  
            this.gci.incrementLabel();
            label2 = this.gci.getLabel(); 
            System.out.println("goto " + label2 + ";");            
            if(this.lk.getTypeToken() != Classification.FECHAPARENTESES.ordinal()){
                throw new ErroParserException("Não encontrado ')'", this.scanner.getLine(), this.scanner.getColumn());            
            }  
            System.out.println(label1 + ":");
            this.nextToken();          
            this.gci.setScope(1);
            this.comando(); 
            this.gci.incrementLabel();
            label3 = this.gci.getLabel();
            if(this.lk.getTypeToken() == Classification.ELSE.ordinal()){
                System.out.println("\tgoto " + label3 + ";"); 
                System.out.println(label2 + ":");                
                this.nextToken();
                this.gci.setScope(1);
                this.comando(); 
                System.out.println(label3 + ":");
                this.gci.setScope(0);
            }  
            else {
                System.out.println(label2 + ":");
                this.gci.setScope(0);
            }
        } else {
            throw new ErroParserException("A declaração tem corpo vazio.", this.scanner.getLine(), this.scanner.getColumn());            
        }
    }
    
    private void comandoBasico() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {
        // <comando_básico> -> <atribuição> | <bloco>
        if(this.lk.getTypeToken() == Classification.ID.ordinal()) {
            this.atribuicao();
        }
        else if(this.lk.getTypeToken() == Classification.ABERTACHAVES.ordinal()) {
            this.bloco(); 
        }    
        else {
            throw new ErroParserException("Não encontrada a declaração", this.scanner.getLine(), this.scanner.getColumn());                        
        }
    }
    
    private void iteracao() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {
        // <iteração> -> while "("<expr_relacional>")" <comando> | do <comando> while "("<expr_relacional>")"";"
        if(this.lk.getTypeToken() == Classification.WHILE.ordinal()){
            String label1, label2;
            this.nextToken();
            if(this.lk.getTypeToken() != Classification.ABERTAPARENTESES.ordinal()){
                throw new ErroParserException("Não encontrado '('", this.scanner.getLine(), this.scanner.getColumn());            
            }    
            this.nextToken();
            this.gci.incrementLabel();
            label1 = this.gci.getLabel();
            System.out.println(label1 + ":");
            this.gci.incrementLabel();
            this.gci.setScope(1);
            label2 = this.gci.getLabel();
            this.exprRelacional(); 
            System.out.println("\t" + "if " + this.gci.getTemporary() + " == 0" + " goto " + label2 + ";");
            if(this.lk.getTypeToken() != Classification.FECHAPARENTESES.ordinal()){
                throw new ErroParserException("Não encontrado ')'", this.scanner.getLine(), this.scanner.getColumn());            
            }  
            this.nextToken();
            this.comando(); 
            System.out.println("\tgoto " + label1 + ";");
            System.out.println(label2 + ":");
            this.gci.setScope(0);
        }  
        else if(this.lk.getTypeToken() == Classification.DO.ordinal()) {
            String label1, label2;
            this.gci.incrementLabel();
            label1 = this.gci.getLabel();   
            System.out.println(label1 + ":");
            this.nextToken();
            this.gci.setScope(1);
            this.comando(); 
            if(this.lk.getTypeToken() != Classification.WHILE.ordinal()){ 
                throw new ErroParserException("Não encontrado 'while'", this.scanner.getLine(), this.scanner.getColumn());            
            }     
            this.gci.setScope(1);
            this.nextToken();
            if(this.lk.getTypeToken() != Classification.ABERTAPARENTESES.ordinal()){
                throw new ErroParserException("Não encontrado '('", this.scanner.getLine(), this.scanner.getColumn());            
            }    
            this.nextToken();
            this.exprRelacional();
            this.gci.incrementLabel();
            label2 = this.gci.getLabel();   
            System.out.println("\tif " + this.gci.getTemporary() + " == 0" + " goto " + label2 + ";");
            System.out.println("\tgoto " + label1 + ";");
            if(this.lk.getTypeToken() != Classification.FECHAPARENTESES.ordinal()){
                throw new ErroParserException("Não encontrado ')'", this.scanner.getLine(), this.scanner.getColumn());            
            }   
            this.nextToken();
            if(this.lk.getTypeToken() != Classification.PONTOVIRGULA.ordinal()){
                throw new ErroParserException("Não encontrado ';'", this.scanner.getLine(), this.scanner.getColumn());            
            }
            System.out.println(label2 + ":");
            this.gci.setScope(0);
            this.nextToken();
        }
    }
    
    private void exprRelacional() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {
        // <expr_relacional> -> <expr_arit> <op_relacional> <expr_arit>
        Symbol a1, a2;
        Token op; 

        a1 = this.exprArit(); 
        op = this.opRelacional();  
        a2 = this.exprArit();  

        this.gci.translationTeste(a1, op.getLexema().toString(), a2);
    }
    
    private Token opRelacional() throws IOException, ErroScannerException, ErroParserException {
        //<expr_relacional> -> <expr_arit> <op_relacional> <expr_arit>
        Token op;
        
        if(this.lk.getTypeToken() == Classification.IGUAL.ordinal()) {
            op = this.lk;
            this.nextToken();          
        }   
        else if(this.lk.getTypeToken() == Classification.DIFERENTE.ordinal()) {
            op = this.lk;
            this.nextToken();          
        }  
        else if(this.lk.getTypeToken() == Classification.MENOR.ordinal()) {
            op = this.lk;
            this.nextToken();          
        }  
        else if(this.lk.getTypeToken() == Classification.MAIOR.ordinal()) {
            op = this.lk;
            this.nextToken();          
        }   
        else if(this.lk.getTypeToken() == Classification.MENORIGUAL.ordinal()) {
            op = this.lk;
            this.nextToken();          
        } 
        else if(this.lk.getTypeToken() == Classification.MAIORIGUAL.ordinal()) {
            op = this.lk;
            this.nextToken();          
        }
        else if(this.lk.getTypeToken() == Classification.FECHAPARENTESES.ordinal()) {
            throw new ErroParserException("início ilegal de expressão.", this.scanner.getLine(), this.scanner.getColumn());
        }
        else {
            throw new ErroParserException("Não pode ser convertido para booleano", this.scanner.getLine(), this.scanner.getColumn());
        }
        return op;
    }    

    private void atribuicao() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException{ 
        // <atribuição> -> <id> "=" <expr_arit> ";"
        Symbol a1, a2;
        StringBuffer erro = this.lk.getLexema();
        
        a1 = new Symbol();
        a1.setLexema(this.lk.getLexema());
        a1.setScope(this.scope);
        
        a1 = this.table.search(a1);        

        this.id();

        if(this.lk.getTypeToken() != Classification.ATRIBUICAO.ordinal()){
            throw new ErroParserException("Uso de identificador não declarado", this.scanner.getLine(), this.scanner.getColumn());
        }  

        this.nextToken();
        
        a2 = this.exprArit();  
        
        if(a1 == null || a2 == null) {
            throw new ErroSemanticException("Variável não foi declarada", erro, this.scanner.getLine(), this.scanner.getColumn());
        }

        if(!this.table.compatible(a1, a2)) {
            throw new ErroSemanticException("Tipo incompativel", erro, this.scanner.getLine(), this.scanner.getColumn());            
        }        
        
        if(this.lk.getTypeToken() != Classification.PONTOVIRGULA.ordinal()) {
            throw new ErroParserException("Não encontrado ';'", this.scanner.getLine(), this.scanner.getColumn());            
        }
        this.nextToken();
        this.gci.translationTeste(a1, "=", a2);
    }

    private Symbol exprArit() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {
        Symbol expr1, expr2;
        StringBuffer temporary, erro;
        Token op;
        
        expr1 = this.termo();    

        while(this.lk.getTypeToken() == Classification.ADICAO.ordinal() || this.lk.getTypeToken() == Classification.SUBTRACAO.ordinal()) {        
            op = this.lk;
            this.nextToken();
            erro = this.lk.getLexema();
            expr2 = this.termo(); 
            this.gci.translationTeste(expr1, op.getLexema().toString(), expr2);
            
            expr1.setType(this.table.checkType(expr1, op.getTypeToken() ,expr2));
            
            if(expr1.getType() == -1) {
                throw new ErroSemanticException("Variável não é compativel", erro, this.scanner.getLine(), this.scanner.getColumn());            
            }   

            temporary = new StringBuffer(this.gci.getTemporary());
            expr1.setLexema(temporary);
        }
        return expr1;  
    }
    
    private Symbol termo() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException {         
        Symbol aux1, aux2;
        StringBuffer temporary, erro;
        Token op;
        int type;
        
        aux1 = this.fator();  
               
        while(this.lk.getTypeToken() == Classification.MULTIPLICACAO.ordinal() || this.lk.getTypeToken() == Classification.DIVISAO.ordinal()) { 
            op = this.lk;  
            this.nextToken();
            erro = this.lk.getLexema();
            aux2 = this.fator();   
            
            this.gci.translationTeste(aux1, op.getLexema().toString(), aux2);    
            
            type = this.table.checkType(aux1,op.getTypeToken(),aux2);  
            aux1.setType(type); 
                    
            if(aux1.getType() == -1) {
                throw new ErroSemanticException("Variável não é compativel", erro, this.scanner.getLine(), this.scanner.getColumn());            
            } 

            temporary = new StringBuffer(this.gci.getTemporary());
            aux1.setLexema(temporary); 
        }     
        return aux1; 
    }

    private Symbol fator() throws IOException, ErroScannerException, ErroParserException, ErroSemanticException { 
    // <fator> -> “(“ <expr_arit> “)” | <id> | <float> | <inteiro> | <char>  
        Symbol symbol = new Symbol(); 
    
        // “(“ <expr_arit> “)”
        if(this.lk.getTypeToken() == Classification.ABERTAPARENTESES.ordinal()) {
            this.nextToken();
            symbol = this.exprArit();
            if(this.lk.getTypeToken() != Classification.FECHAPARENTESES.ordinal()) { 
                throw new ErroParserException("Não encontrado ')'", this.scanner.getLine(), this.scanner.getColumn());            
            }   
            this.nextToken();
        }
        else if(this.lk.getTypeToken() == Classification.ID.ordinal()) { 
            symbol.setLexema(this.lk.getLexema());     
            symbol.setScope(this.scope); 
            
            if(this.table.search(symbol) == null) {
                throw new ErroSemanticException("Variável não foi declarada", this.scanner.getLexema(),this.scanner.getLine(),this.scanner.getColumn());
            }
            
            symbol.setType(this.table.search(symbol).getType());
            
            this.id();
        }
        else if(this.lk.getTypeToken() == Classification.TIPOFLOAT.ordinal()) {
            symbol.setType(this.lk.getTypeToken());
            symbol.setLexema(this.lk.getLexema()); 
            symbol.setScope(this.scope);           
            this.nextToken(); 
        }
        else if(this.lk.getTypeToken() == Classification.TIPOINT.ordinal()) {
            symbol.setType(this.lk.getTypeToken()); 
            symbol.setLexema(this.lk.getLexema());   
            symbol.setScope(this.scope);               
            this.nextToken(); 
        }        
        else if(this.lk.getTypeToken() == Classification.TIPOCHAR.ordinal()) {
            symbol.setType(this.lk.getTypeToken());
            symbol.setLexema(this.lk.getLexema());   
            symbol.setScope(this.scope);            
            this.nextToken(); 
        }         
        else {
            throw new ErroParserException("Não encontrado expressão", this.scanner.getLine(), this.scanner.getColumn());
        }
        return symbol; 
    }    
}
