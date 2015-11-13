// Pavel Gorelov
// Sam Capotosto
// Parker Watts

import java.util.*;
public class setScanner{

   private Scanner src;
   private Token currToken;
   // I would do this line by line, but you could do something
   // else
   // NOTE: NO TOKEN CAN CROSS A LINE BOUNDARY
   private char[] currline = {};
   private int currPos; // 0 <= currPos <= currLine.length
                        // to record where we are on the current line

    private List<Character> zeroToNine = new ArrayList<Character>();
    private void fillZeroToNine() {
        char[] chars = {'0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' };
        for(char c : chars)
        {
            zeroToNine.add(c);
        }
    }

    public setScanner(){
        src = new Scanner(System.in);
        fillZeroToNine();

        // load currToken with the first token
        if(src.hasNext())
        {
            currline = src.next().toCharArray();
            currPos = 0;
        }
        else
        {
            currToken = new Token(Token.UNRECOGNIZED);
        }

       this.consume();
   }

   // returns the current token w/o advancing
   public Token lookahead(){
      return currToken;
   }

   public void consume(){


     //if the current token is already UNRECOGNIZED this
     //operation has no effect(do it this way; another time
     //I might have it discard the current character and try
     //again)

       //TODO: This part sounds ambiguous... not sure how to proceed.

     //skip over WS in src until either reaches end of file
     //or a non WS char(I'd do it line by line, but you may
     //do it some other way)

       // Walk through the currline look for whitespace
       while(currPos < currline.length && currline[currPos] == ' ')
       {
           currPos++;
       }

       //if reaches eof w/o seeing nonWS loads
       //    currToken with UNRECOGNIZED

       // Reached the end of this line or line is empty
       if(currPos == currline.length)
       {
           if(src.hasNext())
           {
               currline = src.next().toCharArray();
               currPos = 0;
               consume();
           }
           else
           {
               currToken = new Token(Token.UNRECOGNIZED);
           }
       }

       //else
       //    scans the src from the current non-ws position and
       //    loads currToken with the longest prefix that
       //    matches a token definition; if no prefix matches,
       //    loads currToken with UNRECOGNIZED

       else
       {
           char firstChar = currline[currPos];
           currPos++;

           // check the current char against the token definitions
           switch(firstChar)
           {
               // Single letter cases
               case '{':
                   currToken = new Token(Token.LEFTBRACE);
                   break;
               case '}':
                   currToken = new Token(Token.RIGHTBRACE);
                   break;
               case '(':
                   currToken = new Token(Token.LEFTPAREN);
                   break;
               case ')':
                   currToken = new Token(Token.RIGHTPAREN);
                   break;
               case ';':
                   currToken = new Token(Token.SEMICOLON);
                   break;
               case '.':
                   currToken = new Token(Token.PERIOD);
                   break;
               case ',':
                   currToken = new Token(Token.COMMA);
                   break;
               case '=':
                   currToken = new Token(Token.EQUALS);
                   break;
               case '*':
                   currToken = new Token(Token.INTERSECTION);
                   break;
               case '+':
                   currToken = new Token(Token.UNION);
                   break;
               case '\\':
                   currToken = new Token(Token.SETDIFFERENCE);
                   break;
               case '-':
                   currToken = new Token(Token.COMPLEMENT);
                   break;

               // Multi symbol cases
               case ':':
                   if(currPos != currline.length && currline[currPos] == '=') {
                       currToken = new Token(Token.ASSIGN);
                       currPos++;
                   }
                   else
                   {
                       currToken = new Token(Token.UNRECOGNIZED);
                   }
                   break;
               case '<':
                   if(currPos != currline.length && currline[currPos] == '=') {
                       currToken = new Token(Token.SUBSET);
                       currPos++;
                   }
                   else
                   {
                       currToken = new Token(Token.UNRECOGNIZED);
                   }
                   break;

               // Number cases
               //NATCONST = 10, // 0|[1-9][0-9]*
               case '0':
                   currToken = new Token(Token.NATCONST, "0");
                   break;

               case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
               StringBuilder sb = new StringBuilder();
               sb.append(firstChar);
               while(currPos < currline.length && zeroToNine.contains(currline[currPos]))
               {
                   sb.append(currline[currPos]);
                   currPos++;
               }
               currToken = new Token(Token.NATCONST, sb.toString());
               break;

               // Reserved word cases
               //IS_IN  = 26, //  "in"  for set membership; so "in" is reserved
               //IF = 5, //  "if"       a reserved word
               case 'i':
                   if(currPos < currline.length && currline[currPos] == 'n')
                   {
                       if(currPos < currline.length - 1 && isValidFollowingVarChar(currline[currPos + 1]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.IS_IN);
                           currPos++;
                       }
                   }
                   else if(currPos < currline.length && currline[currPos] == 'f')
                   {
                       if(currPos < currline.length - 1 && isValidFollowingVarChar(currline[currPos + 1]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.IF);
                           currPos++;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //END = 4, // "end"      a reserved word
               //ELSE = 6, // "else"     a reserved word
               //ENDIF = 7, // "endif"    a reserved word

               case 'e':
                   if (currPos < currline.length - 3 && currline[currPos] == 'n' && currline[currPos + 1] == 'd' && currline[currPos + 2] == 'i' && currline[currPos + 3] == 'f')
                   {
                       if(currPos < currline.length - 4 && isValidFollowingVarChar(currline[currPos + 4]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.ENDIF);
                           currPos += 4;
                       }
                   }
                   else if(currPos < currline.length - 2 && currline[currPos] == 'l' && currline[currPos + 1] == 's' && currline[currPos + 2] == 'e')
                   {
                       if(currPos < currline.length - 3 && isValidFollowingVarChar(currline[currPos + 3]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.ELSE);
                           currPos += 3;
                       }
                   }
                   else if(currPos < currline.length - 1 && currline[currPos] == 'n' && currline[currPos + 1] == 'd')
                   {
                       if(currPos < currline.length - 2 && isValidFollowingVarChar(currline[currPos + 2]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.END);
                           currPos += 2;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //NAT = 8, //  "nat"      a reserved word
               //NOT =  21, //  "not" for boolean negation; so "not" is reserved

               case 'n':
                   if (currPos < currline.length - 1 && currline[currPos] == 'a' && currline[currPos + 1] == 't')
                   {
                       if(currPos < currline.length - 2 && isValidFollowingVarChar(currline[currPos + 2]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.NAT);
                           currPos += 2;
                       }
                   }
                   else if(currPos < currline.length - 1 && currline[currPos] == 'o' && currline[currPos + 1] == 't')
                   {
                       if(currPos < currline.length - 2 && isValidFollowingVarChar(currline[currPos + 2]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.NOT);
                           currPos += 2;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //SET = 9, // "set"      a reserved word
               case 's':
                   if (currPos < currline.length - 1 && currline[currPos] == 'e' && currline[currPos + 1] == 't')
                   {
                       if(currPos < currline.length - 2 && isValidFollowingVarChar(currline[currPos + 2]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.SET);
                           currPos += 2;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //CMP = 28, // "CMP" a reserved word
               case 'C':
                   if (currPos < currline.length - 1 && currline[currPos] == 'M' && currline[currPos + 1] == 'P')
                   {
                       if(currPos < currline.length - 2 && isValidFollowingVarChar(currline[currPos + 2]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.CMP);
                           currPos += 2;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //VAR = 2, // "var"      a reserved word
               case 'v':
                   if (currPos < currline.length - 1 && currline[currPos] == 'a' && currline[currPos + 1] == 'r')
                   {
                       if(currPos < currline.length - 2 && isValidFollowingVarChar(currline[currPos + 2]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.VAR);
                           currPos += 2;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //THEN = 27, // "then" a reserved word
               case 't':
                   if (currPos < currline.length - 2 && currline[currPos] == 'h' && currline[currPos + 1] == 'e' && currline[currPos + 2] == 'n')
                   {
                       if(currPos < currline.length - 3 && isValidFollowingVarChar(currline[currPos + 3]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.THEN);
                           currPos += 3;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //BEGIN = 3, // "begin"    a reserved word
               case 'b':
                   if (currPos < currline.length - 3 && currline[currPos] == 'e' && currline[currPos + 1] == 'g' && currline[currPos + 2] == 'i' && currline[currPos + 3] == 'n')
                   {
                       if(currPos < currline.length - 4 && isValidFollowingVarChar(currline[currPos + 4]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.BEGIN);
                           currPos += 4;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               //PROGRAM = 0, //  "program"  a reserved word
               case 'p':
                   if (currPos < currline.length - 5 && currline[currPos] == 'r'
                           && currline[currPos + 1] == 'o'
                           && currline[currPos + 2] == 'g'
                           && currline[currPos + 3] == 'r'
                           && currline[currPos + 4] == 'a'
                           && currline[currPos + 5] == 'm')
                   {
                       if(currPos < currline.length - 6 && isValidFollowingVarChar(currline[currPos + 6]))
                       {
                           getVariableName(firstChar);
                       }
                       else {
                           currToken = new Token(Token.PROGRAM);
                           currPos += 6;
                       }
                   }
                   else
                   {
                       getVariableName(firstChar);
                   }
                   break;

               // Variable names
               //ID = 1, // [a-zA-Z]+[a-zA-Z0-9]*

               default:
                   if(currPos < currline.length)
                   {
                       getVariableName(firstChar);
                   }
                   else
                   {
                       currToken = new Token(Token.UNRECOGNIZED);
                   }
                   break;
           }
       }
   }

    private void getVariableName(char startingChar)
    {
        if(isValidFirstVarChar(startingChar) && currPos < currline.length && isValidFollowingVarChar(currline[currPos]))
        {
            StringBuilder sb = new StringBuilder();
            sb.append(startingChar);
            sb.append(currline[currPos]);
            currPos++;
            while (currPos < currline.length && isValidFollowingVarChar(currline[currPos]))
            {
                sb.append(currline[currPos]);
                currPos++;
            }
            currToken = new Token(Token.ID, sb.toString());
        }
        else
        {
            currToken = new Token(Token.UNRECOGNIZED);
        }
    }

    private boolean isValidFirstVarChar(char c)
    {
        return Character.toString(c).matches("[a-zA-Z]");
    }

    private boolean isValidFollowingVarChar(char c)
    {
        return Character.toString(c).matches("[a-zA-Z0-9]");
    }
}

