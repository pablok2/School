// Pavel Gorelov
// Sam Capotosto
// Parker Watts

public class Token{

   private static final String[] TOKEN_LABELS = { "program", "identifier",
   "var", "begin", "end", "if", "else", "endif", "nat", "set",
   "natconstant", "leftbrace", "rightbrace", "leftparen",
   "rightparen", "semicolon", "period", "comma", "assign", "subset",
   "equals", "not", "intersection", "union", "setdifference", "complement",
   "is in", "then", "CMP",  "unrecognized"};

   public static final int // for the token types
      PROGRAM = 0, //  "program"  a reserved word
      ID = 1, // [a-zA-Z]+[a-zA-Z0-9]* 
      VAR = 2, // "var"      a reserved word
      BEGIN = 3, // "begin"    a reserved word
      END = 4, // "end"      a reserved word
      IF = 5, //  "if"       a reserved word
      ELSE = 6, // "else"     a reserved word
      ENDIF = 7, // "endif"    a reserved word
      NAT = 8, //  "nat"      a reserved word
      SET = 9, // "set"      a reserved word
      NATCONST = 10, // 0|[1-9][0-9]*
      LEFTBRACE = 11,    // '{'
      RIGHTBRACE = 12,    // '}'
      LEFTPAREN = 13,    // '('
      RIGHTPAREN = 14,    // ')'
      SEMICOLON = 15,    // ';'
      PERIOD  =  16,    // '.'
      COMMA =  17,    // ','
      ASSIGN = 18, //  ":="  for assignment
      SUBSET = 19, //  "<="  for is subset of 
      EQUALS = 20,  // '='   for equality comparisons of sets
      NOT =  21, //  "not" for boolean negation; so "not" is reserved
      INTERSECTION = 22 ,  // '*'   for set intersection
      UNION = 23,  // '+'   for set union
      SETDIFFERENCE = 24, // '\'   for binary set difference
      COMPLEMENT = 25,  // '-'   for unary set complement
      IS_IN  = 26, //  "in"  for set membership; so "in" is reserved
      THEN = 27, // "then" a reserved word
      CMP = 28, // "CMP" a reserved word
      UNRECOGNIZED = 29; // for anything else
      /***

         Another time I would add EOF, but this will do for this time 
         around.

      ***/

   int lineNum,  // line of the source file where the token came from
       tokenType;  // uses the constants defined above
   String tokenString;

   public Token(int tkCode){
      tokenType = tkCode; // enough for all but NATCONST and ID,                    
   }

   // to handle NATCONST, ID, OPERATOR
   public Token(int tkCode, String tkString){
      tokenType = tkCode;
      tokenString = tkString;
   }

   // getters

   public int getTokenType(){
      return tokenType;
   }

   public int getLineNum(){
      return lineNum;
   }

   public String getTokenString(){
      return tokenString;
   }

   public String toString(){

      return TOKEN_LABELS[tokenType] +
      (tokenType == NATCONST ||
       tokenType == ID ? ": " + tokenString : "");
   } 

   public static void main(String[] args){
      Token tk = new Token(THEN);
      System.out.println("" + tk);
   }
}
      
