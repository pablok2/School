// Pavel Gorelov
// Sam Capotosto
// Parker Watts

import java.util.*;

public class ScannerDriver{

   public static void main(String[] args){

      setScanner sc = new setScanner();
      Token tk = sc.lookahead();
      int i = 0;

      // with my skeletons, the next line throws an exception, because
      // the lookahead method returns a null value;
      while (tk.getTokenType() != Token.UNRECOGNIZED){
         System.out.println("Token #" + (i++) + ": " + tk);
         sc.consume();
         tk = sc.lookahead();
      }
      System.out.println("Last token, #" + i + ": " + tk);
   }
}