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
public class Symbol {
    private StringBuffer lexema;
    private int type;
    private int scope;
    
    public Symbol(){} 
    
    public Symbol(StringBuffer lx){
        this.lexema = lx;
    }     
    
    public Symbol(StringBuffer lx, int type){
        this.lexema = lx;
        this.type = type;
    } 
    
    public StringBuffer getLexema() {
        return lexema;
    }

    public void setLexema(StringBuffer lexema) {
        this.lexema = lexema;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "(type = " + type + ", lexema = " + lexema + ", scope = " + scope + ")";
    }  
}
