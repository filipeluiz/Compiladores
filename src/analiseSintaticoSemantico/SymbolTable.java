package analiseSintaticoSemantico;

import analiseLexico.Classification;
import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author filipe
 */
public class SymbolTable {
    private Stack<Symbol> table;
    
    public SymbolTable() {
        this.table = new Stack<>();
    };

    public Stack<Symbol> getTable() {
        return this.table;
    }
    
    public void push(Symbol s) {
        this.table.push(s);
    }
    
    public void pop() {
        this.table.pop();
    }    
    
    public boolean isAlreadyDeclaredScope(Symbol s, int scope) {
        Iterator i = this.table.iterator();
        while(i.hasNext()) {
            Symbol symb = (Symbol) i.next();
            
            if(symb.getLexema().toString().equals(s.getLexema().toString()) && symb.getScope() == scope){
                return true;
            }                
        }
        return false;
    }   
    
    public Symbol search(Symbol s) {
        Iterator i = this.table.iterator();
        int scope = s.getScope();
        boolean searchScope = true;    
        
        while(searchScope) {
            searchScope = false;
            while(i.hasNext()){
                Symbol symb = (Symbol) i.next(); 
                if( symb.getLexema().toString().equals(s.getLexema().toString()) && symb.getScope() == scope ){
                    return symb;
                }
            }
            if(scope > 1){
                i = this.table.iterator();
                scope--;
                searchScope = true;
            }
        }
        return null;  
    }
    
    public int checkType(Symbol a, int op, Symbol b) { // verificar tipo dominante
        Symbol aux1, aux2;      
        int dominantType;
        
        if(op == Classification.DIVISAO.ordinal()) {       
            if((a.getType() == Classification.INT.ordinal() || a.getType() == Classification.TIPOINT.ordinal()) && (b.getType() == Classification.INT.ordinal() || b.getType() == Classification.TIPOINT.ordinal()) ) {
                return Classification.FLOAT.ordinal();
            }
        }        
        
        if(a.getType() == 1){
            aux1 = this.search(a);
        }
        else {
            aux1 = a;
        }
        
        if(b.getType() == 1){
            aux2 = this.search(b); 
        }
        else {
            aux2 = b;
        }
        
        if(a.getLexema().toString().equals("T0")) {
            aux1 = a;
        }
        
        if(b.getLexema().toString().equals("T0")) {
            aux2 = b;
        }        
        
        if(a.getType() == Classification.TIPOINT.ordinal() || a.getType() == Classification.TIPOFLOAT.ordinal() || a.getType() == Classification.TIPOCHAR.ordinal()) {
            aux1 = a;
        }
        
        if(b.getType() == Classification.TIPOINT.ordinal() || b.getType() == Classification.TIPOFLOAT.ordinal() || b.getType() == Classification.TIPOCHAR.ordinal()) {
            aux2 = b;
        }
        
        if(aux1.getType() == Classification.INT.ordinal() || aux1.getType() == Classification.TIPOINT.ordinal()) {
            if(aux2.getType() == Classification.INT.ordinal() || aux2.getType() == Classification.TIPOINT.ordinal()) {
                dominantType = Classification.INT.ordinal();
            }
            else if(aux2.getType() == Classification.FLOAT.ordinal() || aux2.getType() == Classification.TIPOFLOAT.ordinal() ) {
                dominantType = Classification.FLOAT.ordinal();
            }   
            else {
                dominantType = -1;
            }
        }
        else if(aux1.getType() == Classification.FLOAT.ordinal() || aux1.getType() == Classification.TIPOFLOAT.ordinal()) {
            if(aux2.getType() == Classification.CHAR.ordinal() || aux2.getType() == Classification.TIPOCHAR.ordinal()) {
                dominantType = -1;
            }
            else {
                dominantType = Classification.FLOAT.ordinal();
            }          
        }
        else if(aux1.getType() == Classification.CHAR.ordinal() || aux1.getType() == Classification.TIPOCHAR.ordinal()) {
            if(aux2.getType() == Classification.CHAR.ordinal() || aux2.getType() == Classification.TIPOCHAR.ordinal()) {
                dominantType = Classification.CHAR.ordinal();
            }
            else {
                dominantType = -1;
            }
        }
        else {
            dominantType = -1;
        }

        return dominantType;
    }
    
    public boolean compatible(Symbol a1, Symbol a2) {
        if(a1.getType() == Classification.INT.ordinal() || a1.getType() == Classification.TIPOINT.ordinal()) {
            if(a2.getType() == Classification.INT.ordinal() || a2.getType() == Classification.TIPOINT.ordinal()){
                return true;
            }
            else {
                return false;
            }
        }
        else if(a1.getType() == Classification.FLOAT.ordinal() || a1.getType() == Classification.TIPOFLOAT.ordinal()) {
            if(a2.getType() == Classification.INT.ordinal() || a2.getType() == Classification.FLOAT.ordinal() || 
               a2.getType() == Classification.TIPOINT.ordinal() || a2.getType() == Classification.TIPOFLOAT.ordinal()){
                return true;
            }
            else {
                return false;
            }            
        }
        else if(a1.getType() == Classification.CHAR.ordinal() || a1.getType() == Classification.TIPOCHAR.ordinal()) {
            if(a2.getType() == Classification.CHAR.ordinal() || a2.getType() == Classification.TIPOCHAR.ordinal()){
                return true;
            }
            else {
                return false;
            }             
        }
        else {
            return false;
        }
    }
}
