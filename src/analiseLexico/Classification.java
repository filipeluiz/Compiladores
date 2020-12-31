package analiseLexico;

/**
 *
 * @author filipe
 */

// Adotar uma representação interna para cada tipo de token
public enum Classification {
    $,                  //0
    ID,                 //1
    MENOR,              //2  
    MAIOR,              //3  
    MENORIGUAL,         //4  
    MAIORIGUAL,         //5       
    IGUAL,              //6  
    DIFERENTE,          //7  
    ADICAO,             //8   
    SUBTRACAO,          //9   
    MULTIPLICACAO,      //10    
    DIVISAO,            //11
    ATRIBUICAO,         //12
    FECHAPARENTESES,    //13 
    ABERTAPARENTESES,   //14
    FECHACHAVES,        //15
    ABERTACHAVES,       //16
    VIRGULA,            //17
    PONTOVIRGULA,       //18
    PONTO,              //19 
    MAIN,               //20  
    IF,                 //21 
    ELSE,               //22 
    WHILE,              //23      
    DO,                 //24 
    FOR,                //25 
    INT,                //26  
    FLOAT,              //27  
    CHAR,               //28  
    LITERAL,            //29 Deixei isso, sei que nao tem regra do projeto (String)
    TIPOINT,            //30 
    TIPOCHAR,           //31
    TIPOFLOAT,          //32 
}
