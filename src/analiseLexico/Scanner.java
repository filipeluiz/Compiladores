package analiseLexico;

import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author filipe
 */
public class Scanner {
    private Token token;
    private StringBuffer lexema;
    private FileInputStream file;
    private char lk;
    private int line;
    private int column;
    private int ascII;
    
    public Scanner(FileInputStream file) throws IOException{
        this.file = file;
        this.ascII = this.file.read();
        this.line = 1;
    }

    public StringBuffer getLexema() {
        return lexema;
    }

    public void setLexema(StringBuffer lexema) {
        this.lexema = lexema;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
    
    public boolean EOF() {
        return this.ascII == -1;
    }
    
    private char readChar() {
        return (char) this.ascII;
    }
    
    private char nextChar() throws IOException {
        this.ascII = this.file.read();
        this.column++;
        return (char) this.ascII;
    }
    
    private boolean getBlank() {
        return ((char) this.ascII == ' ');
    }
    
    private boolean getTab() {
        return ((char) this.ascII == '\t');
    }
    
    private boolean newLine() {
        return ((char) this.ascII == '\n');
    }      
    
    // Escrever a função SCAN. (ok)
    // Retornar dois resultados: classificação e lexema (ok)
    // Se for uma palavra reservada, operador, caracter especial ou delimitador: classificação apenas
    // ****** Coloquei palavra reservada, operador... com lexema, pois fica mais fácil ver console
    // ****** Quando usar com parser, eu vou deixar this.setLexema com null e classificação apenas ok?
    
    public Token scan() throws IOException, ErroScannerException {
        token = new Token();
        lexema = new StringBuffer();
        this.lk = this.readChar();

        while(!this.EOF()){
            this.getNoBlank(); // Ignore branco, tab e nova linha           
            
            /* ############# Palavra reserva ou ID ############# */
            if(Character.isLetter(this.lk) || this.lk == '_'){
                while(Character.isLetter(this.lk) || Character.isDigit(this.lk) || ((char)this.lk == '_')){
                    this.lexema.append(this.lk);
                    this.lk = this.nextChar();       
                }
                if(RegularExpression.isReserveWord(this.lexema.toString())){
                    this.token.setTypeToken(tableReserveWords(this.lexema));
                    this.token.setLexema(this.lexema);
                    return token;
                }

                if(RegularExpression.isID(this.lexema.toString())) {
                    this.token.setTypeToken(Classification.ID.ordinal());
                    this.token.setLexema(this.lexema); 
                    return token;
                }    
            }
            
            /* ############# Inteiro ou Float ############# */  
            if(Character.isDigit(this.lk)) { // Inteiro
                while(Character.isDigit(this.lk)){
                    this.lexema.append(this.lk);
                    this.lk = this.nextChar();
                }
                
                if(this.lk == '.' || this.lk == ','){ // Float
                    this.lexema.append(this.lk);
                    this.lk = this.nextChar();
                    if(Character.isDigit(this.lk)){
                        while(Character.isDigit(this.lk)){
                            this.lexema.append(this.lk);
                            this.lk = this.nextChar();
                        }
                        
                        if(this.lk == '.' || this.lk == ',') {
                            throw new ErroScannerException("Valor float mal formado", this.lexema, this.line, this.column);
                        }
                        this.token.setLexema(this.lexema);
                        this.token.setTypeToken(Classification.TIPOFLOAT.ordinal());
                        return this.token;
                    } 
                    else {
                        throw new ErroScannerException("Valor float mal formado", this.lexema, this.line, this.column);
                    }
                }
                this.token.setLexema(this.lexema);
                this.token.setTypeToken(Classification.TIPOINT.ordinal());
                return this.token;                
            }
            
                    
            if(this.lk == ',' || this.lk == '.') { // Virgula, Ponto ou float
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                
                if(Character.isDigit(this.lk)){  // float
                    while(Character.isDigit(this.lk)){
                        this.lexema.append(this.lk);
                        this.lk = this.nextChar();
                    }

                    if(this.lk == '.' || this.lk == ',') {
                        throw new ErroScannerException("Valor float mal formado", this.lexema, this.line, this.column);
                    }
                    this.token.setLexema(this.lexema);
                    this.token.setTypeToken(Classification.TIPOFLOAT.ordinal());
                    return this.token;
                }

                if(".".equals(this.lexema.toString())){ // EU modifiquei aqui, melhor testa novamente se dê tudo certo
                    this.token.setTypeToken(Classification.PONTO.ordinal());
                    this.token.setLexema(this.lexema);
                    return this.token;                         
                }
                
                this.token.setTypeToken(Classification.VIRGULA.ordinal());
                this.token.setLexema(this.lexema);
                return this.token;                
            }
            
            if(this.lk == '<') { // Menor
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                if(this.lk == '=') { // <=
                    this.lexema.append(this.lk);
                    this.token.setTypeToken(Classification.MENORIGUAL.ordinal());
                    this.token.setLexema(this.lexema);
                    this.lk = this.nextChar();
                    return this.token;
                } 
                this.token.setTypeToken(Classification.MENOR.ordinal());
                this.token.setLexema(this.lexema);  
                return this.token;
            }
            
            if(this.lk == '>') { // Maior
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                if(this.lk == '=') { // >=
                    this.lexema.append(this.lk);
                    this.token.setTypeToken(Classification.MAIORIGUAL.ordinal());
                    this.token.setLexema(this.lexema);
                    this.lk = this.nextChar();
                    return this.token;
                } 
                this.token.setTypeToken(Classification.MAIOR.ordinal());
                this.token.setLexema(this.lexema);    
                return this.token;
            }

            if(this.lk == '=') { // Atribuição ou Igual
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                if(this.lk == '=') { // == Igual
                    this.lexema.append(this.lk);
                    this.token.setTypeToken(Classification.IGUAL.ordinal());
                    this.token.setLexema(this.lexema);
                    this.lk = this.nextChar();
                    return this.token;
                }                

                this.token.setTypeToken(Classification.ATRIBUICAO.ordinal());
                this.token.setLexema(this.lexema);    
                return this.token;
            }   
            
            if(this.lk == '!') { 
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                if(this.lk == '=') { // != Diferente
                    this.lexema.append(this.lk);
                    this.token.setTypeToken(Classification.DIFERENTE.ordinal());
                    this.token.setLexema(this.lexema);
                    this.lk = this.nextChar();
                    return this.token;                    
                } else {
                    throw new ErroScannerException("Exclamação (‘!’) não seguida de ‘=’", this.lexema, this.line, this.column);
                }
            }             
            
            // Dá para fazer outros if´s como atribuição adição(+=), atribuição substração(-=) e ect...
            // Não fiz pq é o projeto, de acordo (http://www.c3.unicap.br/silvio/compiladores/scanner.html)                        
                        
            if(this.lk == '+') { // Adição
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                this.token.setTypeToken(Classification.ADICAO.ordinal());
                this.token.setLexema(this.lexema);  
                return this.token;
            }          
            
            if(this.lk == '-') { // Subtração
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.SUBTRACAO.ordinal());
                this.token.setLexema(this.lexema);  
                this.lk = this.nextChar();                
                return this.token;
            }    
            
            if(this.lk == '*') { // Multiplicação
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.MULTIPLICACAO.ordinal());
                this.token.setLexema(this.lexema);  
                this.lk = this.nextChar();                
                return this.token;
            }      
            
            /* ############# Comentário ou Divisão ############# */
            if(this.lk == '/'){
                if(!this.isComent()) {
                    this.token.setTypeToken(Classification.DIVISAO.ordinal());
                    this.token.setLexema(this.lexema);
                    return this.token;  
                } 
            }             

            if(this.lk == '(') { // Parentese aberta
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.ABERTAPARENTESES.ordinal());
                this.token.setLexema(this.lexema);  
                this.lk = this.nextChar();
                return this.token;
            }  
            
            if(this.lk == ')') { // Parentese fechada
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.FECHAPARENTESES.ordinal());
                this.token.setLexema(this.lexema);  
                this.lk = this.nextChar();
                return this.token;
            } 
            
            if(this.lk == '{') { // Chave aberta
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.ABERTACHAVES.ordinal());
                this.token.setLexema(this.lexema);  
                this.lk = this.nextChar();
                return this.token;
            }        
            
            if(this.lk == '}') { // Chave fechada
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.FECHACHAVES.ordinal());
                this.token.setLexema(this.lexema);  
                this.lk = this.nextChar();
                return this.token;
            }   
            
            if(this.lk == ';') { // Ponto virgula
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.PONTOVIRGULA.ordinal());
                this.token.setLexema(this.lexema);  
                this.lk = this.nextChar();
                return this.token;
            }    
            
            if(this.lk == '\''){ // Tipo char
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                if(Character.isLetterOrDigit(this.lk) || RegularExpression.isChar(Character.toString(this.lk))) {
                    this.lexema.append(this.lk);
                    this.lk = this.nextChar();                    
                    if(this.lk == '\''){
                        this.lexema.append(this.lk);
                        this.token.setTypeToken(Classification.TIPOCHAR.ordinal());
                        this.token.setLexema(this.lexema);  
                        this.lk = this.nextChar();
                        return this.token;
                    } else {
                        throw new ErroScannerException("Valor char mal formado", this.lexema, this.line, this.column);
                    }
                }
            }
            
            if(this.lk == '"'){ // Literal
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                
                while(!(this.lk == '"')){
                    this.lexema.append(this.lk);
                    this.lk = this.nextChar();
                }
                this.lexema.append(this.lk);
                this.token.setTypeToken(Classification.LITERAL.ordinal());
                this.token.setLexema(this.lexema); 
                this.lk = this.nextChar();
                return this.token;                    
            }
            
            // Caracter especial
            if(this.lk == '@' || this.lk == '#' || this.lk == '$' || this.lk == '-' || this.lk == 'ª' || this.lk == '§' || this.lk == '¬' || this.lk == '^' || this.lk == 'ç' || this.lk == '°' || this.lk == '£' || this.lk == '¢') {
                this.lexema.append(this.lk);
                this.lk = this.nextChar();
                throw new ErroScannerException("Caracter inválido", this.lexema, this.line, this.column);
            }
               
        } // fim while

        this.lexema.append("$");
        this.token.setLexema(this.lexema);             
        return token;           
    }
         
    private void getNoBlank() throws IOException {
        while(this.getBlank() || this.getTab() || this.newLine()){
            if(this.getTab()){
                this.column += 4;               
            }             
            if(this.getBlank()){
                this.column++; 
            }               
            if(this.newLine()){
                this.line++;
                this.column = 0;
            }  
            this.ascII = file.read();
            this.lk = this.readChar(); 
        } 
    }    
    
    private boolean isComent() throws IOException, ErroScannerException {
        this.lk = this.nextChar();
        boolean isComent = false;        
        
        if(this.lk == '/') { // Comentario linha
            while(!(this.lk == '\n') && !this.EOF()) {
                this.lk = this.nextChar();
            }
            isComent = true;
        }
        else if(this.lk == '*') {
            boolean isCM = true;
            while(isCM){
                this.getNoBlank();
                if(this.lk == '*'){
                    this.lk = this.nextChar();
                    if(this.lk == '/') {
                        isCM = false;
                    }
                }
                else if(this.EOF()) {
                    throw new ErroScannerException("Erro de comentário não fechado", this.lexema, this.line, this.column);
                }                
                else {
                    this.lk = this.nextChar();
                }
            }
            this.nextChar();
            isComent = true;
        }
        else {
            this.lexema.append('/');
        }
        return isComent;
    }    
   
    // Organizar a tabela de palavras reservadas (ok)
    private int tableReserveWords(StringBuffer lexema) { // tentei Vetor com string, mas achei melhor switch.
        String lx = lexema.toString();
        
        switch(lx) {
            case "main":
                return Classification.MAIN.ordinal();
            case "if":
                return Classification.IF.ordinal();
            case "else":
                return Classification.ELSE.ordinal();                
            case "while":
                return Classification.WHILE.ordinal();
            case "do":
                return Classification.DO.ordinal();
            case "for":
                return Classification.FOR.ordinal();
            case "int":
                return Classification.INT.ordinal();
            case "float":
                return Classification.FLOAT.ordinal();
            case "char":
                return Classification.CHAR.ordinal();    
            default:
                break;
        }
        
        return -1; // não existe  
    }
    
    public void fileClosed() throws IOException { // Adicionei por causa de Parser
        this.file.close();
    }
}
