// Pavel Gorelov

public class hm2
{
	private static final double ln3 = Math.log(3);

	private static String enumAStar(long k)
	{
	   /*
	
	   returns "" if k <= 0
	   else returns the String value s such that s corresponds to the value of k
	   in the listing(I'm leaving off the "'s)
	   
	   1  2  3  4   5   6   7   8   9  10  11  12  13
	   a  b  c aa  ab  ac  ba  bb  bc  ca  cb  cc aaa ...
	   
	   Hint: This is the list of A^1, A^2, A^3, A^4, ... with each A^i
	   listed lexicographically with a < b < c.
	   
	   A^i will have 3^i values in it (why?), and the sum of 
	   3^i, for i running from 0 to n is (3^(n+1) - 1)/2
	   
	   (that is from the general formula for the sum of a geometric
	   sequence, a sequence in which each term is a fixed multiple of
	   a prior term)
	
	   So, 
	
	   A^0, A^1, ..., A^n
	
	   will use the numbers
	
	   0, 1, 2, ..., (3^(n+1) - 1)/2 - 1
	
	   assuming k >= 1 you want to find the SMALLEST n such that
		
	   k <= (3^(n+1) - 1)/2 - 1 
	
	   That will give you the LENGTH of the string s, n.  Next,
	   calculate
	
	   k - ((3^n - 1)/2 - 1) - 1
	
	   that will give you the OFFSET for k within the list of
	   A^n.  Regarding a as 0, b as 1, c as 2, construct the string
	   s as a base 3 numeral for the offset.  If the length of the
	   numeral is less than n, pad out on the left with a's to make
	   it length n.
	
	   For example, suppose k is 55.  The smallest n such that
	
	   k <= (3^(n+1) - 1)/2 - 1 
	
	   is 4, since 
	
	   (3^4 - 1)/2 - 1 = 39
	   (3^5 - 1)/2 - 1 = 121
	
	   So, the string has length 4.  Now, 
	
	   55 - (3^4 - 1)/2  - 1 = 55 - 39 - 1 = 15,
	
	   so we consider the base 3 numeral for 15.  It's 9 + 6,
	   so it's  120, or bca using the characters a, b, and c.  Pad out with a on
	   the left to make it length 4, and we have
	
	   abca
	
	   for the result.
	
	   Note, it's more efficient to rework the inequalities
	
	   (3^n - 1)/2 - 1 < k <= (3^(n+1) - 1)/2 - 1 
	
	   to isolate n as a floor or ceiling of some expression involving k.
	   This will involved taking some logarithms, too, and casting the result
	   back to int.
	
	
	   */
		   
		if(k <= 0)
		{
			return "" ;
		}
		else
		{
			boolean findN = true;
			int lenghtS = 1;
			
			// Find the smallest n
			for(int n = 1; findN; n++)
			{
				if((((Math.pow(3,n) - 1)/2 - 1) < k) && (k <= (Math.pow(3,n+1) - 1)/2 - 1))
				{
					findN = false;
					lenghtS = n;
				}
			}
			
			int offset = (int) (k - ((Math.pow(3,lenghtS) - 1)/2 - 1) - 1);
			
			// Base 3 of offset
			String base3 = Integer.toString(offset, 3);
			
			// String representation
			String stringRep = "";

			// Convert the base 3 number to a b c version
			for(int i = 0; i < base3.length(); i++)
			{
				char c = base3.charAt(i);
				switch (c){
					case '0':
						stringRep += "a";
						break;
					case '1':
						stringRep += "b";
						break;
					case '2':
						stringRep += "c";
						break;
				}						
			}
			
			// Append a's if the length is small
			while(stringRep.length() != lenghtS)
			{
				stringRep = "a" + stringRep;
			}			
			
			return stringRep;
		}
	}


	private static long indexOf (String s)
	{
	   /*
	   
	   if s is empty or if s contains any characters other than 'a', 'b'
	   or 'c', return 0
	
	   else (s is a nonempty string of a's, b's, and c's)
	   return the nonnegative integer k such that k is the value
	   associated with s in the list above.
	
	   Hint: basically reverse the approach for the last
	
	   1. using the length of s, (say it's m) calculate the index
	   n of the first string of the enumeration of A^m, aaa...a (m a's).
	
	   2. determine the value of s as a base 3 numeral where a is 0,
	   b is 1, and c is 2.  Call that long value p.
	
	   3. add p to n and return the result
	
	   */

		if(s.length() == 0)
		{
			return 0L;
		}
		
		// Don't allow other characters
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(!(c == 'a' || c == 'b' || c == 'c'))
			{
				return 0L;
			}
		}
						
		int power = 0;
		long index = 0;
		
		// Read last letter first
		for(int i = s.length(); i > 0; i--)
		{
			// Use A^i of the letter's position
			char c = s.charAt(i - 1);
			int powerIncr = (int) Math.pow(3, power);
			switch (c){
				case 'a':
					index += powerIncr;
					break;
				case 'b':
					index += 2 * powerIncr;
					break;
				case 'c':
					index += 3 * powerIncr;
					break;
			}
			
			power++;
		}				
		
		return index;
	}

	public static void main(String[] args)
	{
		String[] testS = { "", "a", "b", "c", "aa", "ab", "ac", "ba", "bb",
	                      "bc", "ca", "cb", "cc", "aaa", "aab", "aac",
	                      "aba", "abb", "abc", "aca", "acb", "acc",
	                      "bbb", "ccc", "aaab", "abcabcabc", "aaabbbccc" };

		long[] testL = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
	                   14, 15, 16, 17, 55,
	                   1000, 10000, 100000, 1000000 };

		int i;
				
		for (i = 0; i < testS.length; i++)
		{
			System.out.println("$" + testS[i] + '$' + " has index " + indexOf(testS[i]));
	    }
	
	    System.out.println();
	
	    for (i = 0; i < testL.length; i++)
	    {
	    	System.out.println("The string with index " + testL[i] + " is $" + enumAStar(testL[i]) + '$');
	    }
	}
}
