package geradorCodigoIntermediario;

import analiseLexico.Classification;
import analiseSintaticoSemantico.Symbol;

/**
 *
 * @author filipe
 */
public class GCI {
    private String temporary;
    private String label;   
    public int tempCount, labCount, scope;
    
    public GCI() {
        this.tempCount = -1;
        this.labCount = -1;
    }

    public String getTemporary() {
        temporary = "_t" + this.tempCount;
        return temporary;
    }

    public String getLabel() {
        label = "_L" + this.labCount;
        return label;
    }
    
    public void setScope(int x) {
        this.scope = x;
    }
    
    public void incrementLabel() {
        this.labCount++;
    }
    
    public void translationTeste(Symbol a, String op, Symbol b) {
        String temp, temp2;
        
        this.tempCount++;
        temp = this.getTemporary();   
          
        if("=".equals(op)){
            if(a.getType() == Classification.FLOAT.ordinal() && b.getType() == Classification.INT.ordinal() ||
               a.getType() == Classification.FLOAT.ordinal() && b.getType() == Classification.TIPOINT.ordinal() ||
               a.getType() == Classification.TIPOFLOAT.ordinal() && b.getType() == Classification.INT.ordinal() ||    
               a.getType() == Classification.TIPOFLOAT.ordinal() && b.getType() == Classification.TIPOINT.ordinal()){
                if(this.scope > 0) {
                    System.out.println("\t" + a.getLexema() + " " + op + " " + "float " + b.getLexema() + ";");
                    this.tempCount--;
                }
                else {
                    System.out.println(a.getLexema() + " " + op + " " + "float " + b.getLexema() + ";");
                    this.tempCount--;
                }                 
            }
            else {
                if(this.scope > 0) {
                    System.out.println("\t" + a.getLexema() + " " + op + " " + b.getLexema() + ";");
                    this.tempCount--;
                }
                else {
                    System.out.println(a.getLexema() + " " + op + " " + b.getLexema() + ";");
                    this.tempCount--;    
                }                 
            }
        }
        else if(a.getType() == Classification.FLOAT.ordinal() && b.getType() == Classification.INT.ordinal() || 
                a.getType() == Classification.FLOAT.ordinal() && b.getType() == Classification.TIPOINT.ordinal() ||
                a.getType() == Classification.TIPOFLOAT.ordinal() && b.getType() == Classification.INT.ordinal() ||    
                a.getType() == Classification.TIPOFLOAT.ordinal() && b.getType() == Classification.TIPOINT.ordinal()) {
            if(this.scope > 0) {
                System.out.println("\t" + temp + " = " + "float " + b.getLexema() + ";");
                this.tempCount++;
                System.out.println("\t" + this.getTemporary() + " = " + a.getLexema() + " " + op + " " + temp);
            }
            else {
                System.out.println(temp + " = " + "float " + b.getLexema() + ";");
                this.tempCount++;
                System.out.println(this.getTemporary() + " = " + a.getLexema() + " " + op + " " + temp);             
            }            
        }
        else if(a.getType() == Classification.INT.ordinal() && b.getType() == Classification.FLOAT.ordinal() ||
                a.getType() == Classification.INT.ordinal() && b.getType() == Classification.TIPOFLOAT.ordinal() ||
                a.getType() == Classification.TIPOINT.ordinal() && b.getType() == Classification.FLOAT.ordinal() ||
                a.getType() == Classification.TIPOINT.ordinal() && b.getType() == Classification.TIPOFLOAT.ordinal()) {
            if(this.scope > 0) {
                System.out.println("\t" + temp + " = " + "float " + a.getLexema() + ";");
                this.tempCount++;
                System.out.println("\t" + this.getTemporary() + " = " + temp + " " + op + " " + b.getLexema());
            }
            else {
                System.out.println(temp + " = " + "float " + a.getLexema() + ";");
                this.tempCount++;
                System.out.println(this.getTemporary() + " = " + temp + " " + op + " " + b.getLexema());                
            }
        }
        else {
            if(this.scope > 0) {
                System.out.println("\t" + temp + " = " + a.getLexema() + " " + op + " " + b.getLexema());
            }
            else {
                System.out.println(temp + " = " + a.getLexema() + " " + op + " " + b.getLexema());
            }            
        }
    }
}
