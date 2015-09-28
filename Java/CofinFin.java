import java.util.*;

public class CofinFin{

	private boolean complement;
	private TreeSet<Integer> finite; // should never be null, even if
                                 // the set is empty
	private int boundValue;

	/**
	
	This class implements an instance of a subset of the natural numbers,
	{ 0, 1, 2, ... }, that is either finite or cofinite(that is, its complement
	with respect to the natural numbers is finite) according to the 
	following scheme.
	
	There are two mutually exclusive cases
	
	Case I: the represented set is finite
	
	complement is false
	
	finite has exactly the members of the set which are ordered ascending in
	the tree.
	
	boundValue is the smallest value such that all values >= boundValue are
	not in the TreeSet.  If the set is empty, boundValue will be 0; if the set
	is nonempty and has largest value n, boundValue will be n+1;  this is to 
	eliminate the need to scan the set to test for membership for 
	value >= boundValue.    In terms of the bit string rep, this is the 
	smallest index such that for this index and all larger indexes, the bit
	at that index is 0.
	
	A simple test for empty is boundValue == 0.
	
	Examples of finite and boundValue
	
	1. empty set   boundValue = 0
	2. { 1, 12, 234 }  boundValue = 235
	3. { 0, 8, 20 }  boundValue = 21
	
	
	Case II: the represented is cofinite
	
	complement is true and all the other data members represent the 
	complement of the set being represented, that is
	
	finite has the set of values that are NOT in the represented cofinite
	set
	
	boundValue is the smallest value such that all values >= boundValue
	are not in the complement, which means they are in the cofinite set
	being represented.  In terms of the bit string rep of the cofinite 
	set,  this is the smallest index such that for this index and all
	larger indexes, the bit at that index is 1.
	
	
	Examples of finite and boundValue(these are to be understood in the
	context of representing the complement of the set given in finite)
	
	1. empty set   boundValue = 0    // represents all of the natural numbers
	2. { 1, 12, 234 }  boundValue = 235  
	   // represents { 0,2,3,...,11,13,14,...,233,235,236,...}
	3. { 0, 8, 20 }  boundValue = 21
	   // represents { 1,2,3,...,7,9,10,11,...,19,21,22,...}
	
	Generally, the boundValue should be 0 when finite is empty,
	else 1 greater than the maximum value in finite.  
	
	Class invariants:
	
	1. boundValue >= 0
	2. finite does not contain any negative integers
	3. finite is empty iff boundValue == 0
	4. boundValue > 0 -> boundValue = 1 + maximumValue in finite
	   (which also implies that boundValue - 1 is the maximum value
	    in finite)
	
	
	NOTE: the constructors need to establish these conventions,
	and mutators need to preserve them.
	
	
	***/
	
	// constructors
	
	public CofinFin(){
		
		/// constructs the rep of the empty set
		
		this.complement = false;
		this.boundValue = 0;
		this.finite = new TreeSet<Integer>();	
	}


	public CofinFin(boolean comp, int n){
		/***
		
		if comp is false, constructs the rep of { n }
		else constructs the rep of the complement of { n }
		
		***/
		
		this.complement = comp;		
		this.finite = new TreeSet<Integer>();
		this.finite.add(n);
		this.boundValue = n + 1;		
	}

	public CofinFin(boolean comp, int [] A){

		/***
		
		if comp is false
		   if A is null 
		      constructs the rep of the empty set
		   else
		      constructs the rep of the distinct values in A that are >= 0
		      (could be none, e.g., if A is { -1,-2,-3 })
		else
		   if A is null 
		      constructs the rep of all of the natural numbers
		   else
		      constructs the rep of the complement of all of the natural numbers
		      (which are all >= 0) that are elements of A
		**/
		
		this.complement = comp;
		this.finite = new TreeSet<Integer>();		
		
		if(A == null || A.length == 0)
		{
			this.boundValue = 0;
		}
		else
		{	
			for(int i = 0; i < A.length; i++)
			{				
				int value = A[i];
				
				// Get only positive numbers
				if(value >= 0)
				{					
					// Add the value to finite
					this.finite.add(value);
				}
			}
				
			// Add one to the greatest value in finite
			this.boundValue = this.finite.size() > 0 ? this.finite.last() + 1 : 0;
		}	
	}

	// mutators

	public void remove(int n){
		// if n < 0 or n is not in this, no changes are made
		// else, the rep is modified to reflect the removal of n from this

		if(n >= 0 && this.isIn(n))
		{
			if(this.complement)
			{
				this.finite.add(n);
				
				// Change the BV to the highest + 1
				if(n >= this.boundValue)
				{
					this.boundValue = n + 1;
				}
			}
			else
			{
				this.finite.remove(n);
				
				// Change the bound value
				this.boundValue = this.finite.size() > 0 ? this.finite.last() + 1 : 0;
			}
		}	
	
	}

	public void add(int n){
		// if n < 0 or n is in this, no changes are made
		// else, the rep is modified to reflect the addition of n to this
		
		if(n >= 0 && !this.isIn(n))
		{
			if(this.complement)
			{
				this.finite.remove(n);
				
				// Change the bound value
				this.boundValue = this.finite.size() > 0 ? this.finite.last() + 1 : 0;
			}
			else
			{
				this.finite.add(n);
				
				// Change the BV to the highest + 1
				if(n >= this.boundValue)
				{
					this.boundValue = n + 1;
				}
			}
		}		
	}

	// operations

	public CofinFin union (CofinFin other){
		// creates and returns a new value that represents the union of this and other;
		// this and other are NOT modified
		
		// This is NOT DeMorgan's version
		
		// Create new empty objects
		CofinFin newCofinFin = new CofinFin();
		TreeSet<Integer> union = new TreeSet<Integer>();
		
		// Both are finite
		if(!this.complement && !other.complement)
		{	
			if(this.boundValue == 0 && other.boundValue == 0)
			{
				newCofinFin.complement = false;
			}
			else
			{
				//Iterate through the two finite sets
				Iterator<Integer> iter = this.finite.iterator();
				Iterator<Integer> otherIter = other.finite.iterator();
				
				while(iter.hasNext())
				{
					union.add(iter.next());
				}
				
				while(otherIter.hasNext())
				{
					int nextVal = otherIter.next();
					if(!union.contains(nextVal))
					{
						union.add(nextVal);
					}
				}
				
				newCofinFin.complement = false;
			}
		}
		
		// Both are cofin
		else if (this.complement && other.complement)
		{
			if(this.boundValue == 0 || other.boundValue == 0)
			{
				newCofinFin.complement = true;
			}
			else
			{
				//Iterate through the two finite sets that represent what's NOT in each set
				Iterator<Integer> iter = this.finite.iterator();
			
				while(iter.hasNext())
				{
					int nextVal = iter.next();
					if(other.finite.contains(nextVal))
					{
						union.add(nextVal);
					}
				}
			
				newCofinFin.complement = true;
			}
		}
		
		// Then one is cofin and the other finite
		else
		{
			// This is cofin
			if(this.complement)
			{
				if(this.boundValue != 0)
				{
					Iterator<Integer> iter = this.finite.iterator();
					
					while(iter.hasNext())
					{
						int nextVal = iter.next();
						if(!other.finite.contains(nextVal))
						{
							union.add(nextVal);
						}
					}
				}
			}
			
			// Other is cofin
			else
			{
				if(other.boundValue != 0)
				{
					Iterator<Integer> iter = other.finite.iterator();
					
					while(iter.hasNext())
					{
						int nextVal = iter.next();
						if(!this.finite.contains(nextVal))
						{
							union.add(nextVal);
						}
					}
				}
			}			
			
			newCofinFin.complement = true;			
		}		
		
		newCofinFin.finite = union;
		newCofinFin.boundValue = newCofinFin.finite.size() > 0 ? newCofinFin.finite.last() + 1 : 0;

		return newCofinFin;
	}

	public CofinFin intersect(CofinFin other){
		// creates and returns a new value that represents the intersection
		// of this and other;
		// this and other are NOT modified
		
		// Using DeMorgan's law for this intersection
		// A intersect B = --(A intersect B) = -(-A union -B)

		
		// Temporarily flip the complement data member of this and other.
		this.complement = !this.complement;
		other.complement = !other.complement;
		
	    // Calculate the flipped operation into a local variable.
		CofinFin intersection = union(other);

	    // Flip the complement of this and other back to their orginal values.
		this.complement = !this.complement;
		other.complement = !other.complement;

	    // Flip the complement of the local variable.
		intersection.complement = !intersection.complement;

	    // Return the local variable.
		return intersection;	
	}

	public CofinFin complement(){
		// creates and returns a new value that represents the complement of this;
		// this is NOT modified
		///(TOO EASY!!  Just create a separate object that copies this(the TreeSet 
		/// should be an entirely new TreeSet object) and flip complement.

		CofinFin compCofinFin = new CofinFin();
		compCofinFin.complement = !this.complement;
		compCofinFin.boundValue = this.boundValue;
		compCofinFin.finite = (TreeSet<Integer>) this.finite.clone();
	
		return compCofinFin;
	}
		
	public boolean isIn(int n){
	
		// returns false if n < 0 or n is not in this
		/// else returns true 
		
		if(n < 0)
		{
			return false;
		}
		
		if(this.boundValue == 0)
		{
			// Return true if cofin, false if finite
			return this.complement;
		}
		
		boolean inFiniteRep = this.finite.contains(n);
		
		return this.complement ? !inFiniteRep : inFiniteRep;
	}
	
	public String toString(){
		/**
		
		If complement is false
		  if the set is empty
		     returns the string "{}"
		  else
		     returns the string "{ v1, v2, ... , vk }"
		     where the vi are the distinct k values in the set sorted in increasing order
		     For example, { 0, 2, 3 } should return the string "{ 0, 2, 3 }"
		else
		   returns the string CMPx
		   where x is the string for the set if complement were false, for example,
		   all the natural numbers would have the string "CMP{}", the set that is all
		   natural numbers except { 1, 3, 5 }, would have the string "CMP{ 1, 3, 5 }",
		   etc.
		
		YOU SHOULD USE StringBuffer or StringBuilder FOR THIS ONE, NOT + WITH STRINGS.
		**/
		
		boolean notEmpt = !finite.isEmpty();
	      
	      StringBuffer bf = new StringBuffer();
	      
	      if (complement)
	         bf.append("CMP");
	      
	      bf.append('{');
	      if (notEmpt)
	         bf.append(' ');
	      
	      Iterator<Integer> iter = finite.iterator();
	      
	      while (iter.hasNext()) {
	         bf.append(iter.next().toString());
	         if (iter.hasNext())
	            bf.append(", ");
	      }
	      if (notEmpt)
	         bf.append(' ');
	      
	      bf.append('}');
	      
	      return bf.toString();
	
	}

	public boolean equals(CofinFin other){
		// returns true exactly when other and this represent the same set
		// Hint: test complement, then boundValue, and if they both agree,
		// test if the finite sets are the same
		
		if(this.complement == other.complement
				&& this.boundValue == other.boundValue)
		{
			if(this.boundValue == 0)
			{
				return true;
			}
			
			boolean match = true;
			
			//Iterate through the two finite sets
			Iterator<Integer> iter = this.finite.iterator();
			Iterator<Integer> otherIter = other.finite.iterator();
		      
		    while (iter.hasNext() && otherIter.hasNext() && match)
		    {		    	
		    	if(match)
		    	{
		    		// Check to see if the values are the same
		    		match = iter.next() == otherIter.next();
		    	}		         
		    }
		    
		    return match;
		}
		else
		{
			return false;
		}
	}

	public boolean isSubsetOf(CofinFin other){
		/*
		
		returns true when this is a subset of other.
		This might be a little tricky, but it should be doable.
		
		*/
		
		// This is cofinite
		if(this.complement)
		{
			if(!other.complement)
			{
				return false;
			}
			
			if(other.boundValue == 0)
			{
				return true;
			}
			
			Iterator<Integer> iter = this.finite.iterator();
			boolean subset = true;
			
			while(iter.hasNext() && subset)
			{
				int nextVal = iter.next();
				subset = !other.isIn(nextVal);
			}
			
			return subset;
		}
		
		// This is finite
		else
		{
			if(this.boundValue == 0)
			{
				// Empty set is always a subset
				return true;
			}
			
			Iterator<Integer> iter = this.finite.iterator();
			boolean subset = true;
			
			while(iter.hasNext() && subset)
			{
				int nextVal = iter.next();
				subset = other.isIn(nextVal);
			}
			
			return subset;
		}
	}


	//  the driver tests the methods
	public static void main (String[] args){
		
		int []         
			      A = { -1, -2, -3 },         
			      B = { 0, 1, 2, 3},         
			      C = { 4, 5, -3, -1 },         
			      D = { 2, 3, 5, 7, 11, 13 },         
			      E = { 0, 2, 4, 6, 8, 10, 12, 14 },         
			      F = { 1, 3, 5, 7, 9, 11, 13 },         
			      G = { 0,1,5,6,7,10,11,13},         
			      H = { 2,3,6,8,9 },         
			      I = { 2,4,6,8,10,13 },         
			      J = { 13,15,17,19,21},         
			      K = { 3,5,7,11},         
			      L = {2,3,6,7 },         
			      M  = { 2,3,6,7,8},         
			      N = {2,3,6,7,10 },         
			      O = { 0,1,4,5,6,7,9},         
			      P = { 0,1,4,5,6,7,9,10},         
			      Q = { 0,1,4,5,6,7,8,9 },         
			      R = { 0,1,4,5,6,7,8,10,11,12};         
			            
		CofinFin         
			   a = new CofinFin(true, 0),  // \{ 1, 2, 3, ... \}         
			   b = new CofinFin(false,1), // \{ 1 \}         
			   c = new CofinFin(),   // empty         
			   d = c.complement(),   // all natural numbers         
			   e = new CofinFin(true, A), // all natural numbers         
			   f = new CofinFin(false, A), // empty         
			   g = new CofinFin(true,B), // \{ 4, 5, ... \}         
			   h = new CofinFin(false,B), // \{ 0,1,2,3 \}         
			   j = new CofinFin(true, C), // \{ 0,1,2,3,6,7,...\}         
			   k = new CofinFin(false,C), // \{ 4,5 \}         
			   m = new CofinFin(true,D),  // \{ 0,1,4,6,8,9,10,12,14,15,...\}         
			   n = new CofinFin(false,D), // \{ 2,3,5,7,11,13 \}         
			   o = new CofinFin(true,E),  // \{ 1,3,5,7,9,11,13,15,16,17,... \}         
			   p = new CofinFin(false,E), // \{ 0,2,4,6,8,10,12,14\}         
			   q = new CofinFin(true,F),  // \{ 0,2,4,6,8,10,12,14,15,16, ... \}         
			   r = new CofinFin(false,F), // \{ 1,3,5,7,9,11,13\}         
			   s = new CofinFin(false,G),         
			   t = new CofinFin(false,H),         
			   u = new CofinFin(false,I),         
			   v = new CofinFin(false,J),         
			   w = new CofinFin(false,0),         
			   x = new CofinFin(false,K),         
			   y = new CofinFin(true,L),         
			   z = new CofinFin(true,M),         
			   aa = new CofinFin(true,N),         
			   bb = new CofinFin(false, O),         
			   cc = new CofinFin(false, P),         
			   dd = new CofinFin(false,Q),         
			   ee = new CofinFin(false,R);         
			            
			   // toString         
			   System.out.println("toString tests\n" + a + '\n' + b + '\n' + c + '\n' + d + '\n'         
			      + e + '\n' + f         
			      + '\n' + g + '\n' + h + '\n' + j + '\n' + k + '\n' + m + '\n' + n         
			      + '\n' + o + '\n' + p + '\n' + q + '\n' + r);         
			            
			   // complement         
			   System.out.println("\n\ncomplement tests\n" + a.complement() + '\n' + b.complement() + '\n' +         
			      c.complement() + '\n' + d.complement() + '\n' + e.complement() + '\n' +          
			      f.complement() + '\n' + g.complement() + '\n' + h.complement() + '\n' +         
			      j.complement() + '\n' + k.complement() + '\n' + m.complement() + '\n' +         
			      n.complement() + '\n' + o.complement() + '\n' + p.complement() + '\n' +         
			      q.complement() + '\n' + r.complement());         
			            
			            
			// union         
			   System.out.println("\n\nunion tests");         
			   System.out.println("" + "\n\n" +  c.union(a) + '\n' +         
			      a.union(c) + '\n' +         
			      c.union(h) + '\n' +         
			      h.union(c) + '\n' +         
			      d.union(a) + '\n' +         
			      a.union(d) + '\n' +         
			      d.union(b) + '\n' +           
			      p.union(r) + '\n' +         
			      h.union(n) + '\n' +         
			      n.union(h) + '\n' +         
			      n.union(o) + '\n' +         
			      o.union(n) + '\n' +         
			      m.union(p) + '\n' +         
			      p.union(m) + '\n' +         
			      g.union(o) + '\n' +         
			      o.union(g) + '\n' +         
			      j.union(m) + '\n' +         
			      m.union(j) + '\n' +         
			      e.union(f) + '\n' +         
			      f.union(e) + '\n' +         
			      c.union(n) + '\n' +         
			      n.union(c) + '\n' +         
			      p.union(q) + '\n' +         
			      q.union(p) + '\n' +         
			      s.union(t) + '\n' +         
			      t.union(s) + '\n' +         
			      n.union(t) + '\n' +         
			      t.union(n) + '\n' +         
			      r.union(u) + '\n' +         
			      u.union(r) + '\n' +         
			      u.union(v) + '\n' +         
			      v.union(u) + '\n' +         
			      u.union(h) + '\n' +         
			      h.union(u) + '\n' +         
			      k.union(s) + '\n' +         
			      s.union(k) + '\n' +         
			      t.union(p) + '\n' +         
			      p.union(t) + '\n' +         
			      w.union(a) + '\n' +         
			      a.union(w) + '\n' +         
			      c.union(a) + '\n' +         
			      a.union(c) + '\n' +         
			      x.union(m) + '\n' +         
			      m.union(x) + '\n' +         
			      k.union(m) + '\n' +         
			      m.union(k) + '\n' +         
			      u.union(o) + '\n' +         
			      o.union(u) + '\n' +         
			      cc.union(y) + '\n' +         
			      y.union(cc) + '\n' +         
			      bb.union(z) + '\n' +         
			      z.union(bb) + '\n' +         
			      dd.union(aa) + '\n' +         
			      aa.union(dd) + '\n' +         
			      ee.union(aa) + '\n' +         
			      aa.union(ee) + '\n' +         
			      d.union(m) + '\n' +         
			      m.union(d) + '\n' +         
			      d.union(e) + '\n' +         
			      e.union(d) + '\n' +         
			      q.union(m) + '\n' +         
			      m.union(q) + '\n' +         
			      g.union(o) + '\n' +         
			      o.union(g));         
			            
			            
			            
			            
			   // intersect         
			   System.out.println("\n\nintersect tests\n");         
			   System.out.println("" + c.intersect(a) + '\n' +         
			      a.intersect(c) + '\n' +         
			      c.intersect(h) + '\n' +         
			      h.intersect(c) + '\n' +         
			      d.intersect(a) + '\n' +         
			      a.intersect(d) + '\n' +         
			      d.intersect(b) + '\n' +           
			      p.intersect(r) + '\n' +         
			      h.intersect(n) + '\n' +         
			      n.intersect(h) + '\n' +         
			      n.intersect(o) + '\n' +         
			      o.intersect(n) + '\n' +         
			      m.intersect(p) + '\n' +         
			      p.intersect(m) + '\n' +         
			      g.intersect(o) + '\n' +         
			      o.intersect(g) + '\n' +         
			      j.intersect(m) + '\n' +         
			      m.intersect(j) + '\n' +         
			      e.intersect(f) + '\n' +         
			      f.intersect(e) + '\n' +         
			      c.intersect(n) + '\n' +         
			      n.intersect(c) + '\n' +         
			      p.intersect(q) + '\n' +         
			      q.intersect(p) + '\n' +         
			      s.intersect(t) + '\n' +         
			      t.intersect(s) + '\n' +         
			      n.intersect(t) + '\n' +         
			      t.intersect(n) + '\n' +         
			      r.intersect(u) + '\n' +  // r is \{ 1,3,5,7,9,11,13\}  u is \{ 2,4,6,8,10,13 \},         
			      u.intersect(r) + '\n' +         
			      u.intersect(v) + '\n' +         
			      v.intersect(u) + '\n' +         
			      u.intersect(h) + '\n' +         
			      h.intersect(u) + '\n' +         
			      k.intersect(s) + '\n' +         
			      s.intersect(k) + '\n' +         
			      t.intersect(p) + '\n' +         
			      p.intersect(t) + '\n' +         
			      w.intersect(a) + '\n' +         
			      a.intersect(w) + '\n' +         
			      c.intersect(a) + '\n' +         
			      a.intersect(c) + '\n' +         
			      x.intersect(m) + '\n' +         
			      m.intersect(x) + '\n' +         
			      k.intersect(m) + '\n' +         
			      m.intersect(k) + '\n' +         
			      u.intersect(o) + '\n' +         
			      o.intersect(u) + '\n' +         
			      cc.intersect(y) + '\n' +         
			      y.intersect(cc) + '\n' +         
			      bb.intersect(z) + '\n' +         
			      z.intersect(bb) + '\n' +         
			      dd.intersect(aa) + '\n' +         
			      aa.intersect(dd) + '\n' +         
			      ee.intersect(aa) + '\n' +         
			      aa.intersect(ee) + '\n' +         
			      d.intersect(m) + '\n' +         
			      m.intersect(d) + '\n' +         
			      d.intersect(e) + '\n' +         
			      e.intersect(d) + '\n' +         
			      q.intersect(m) + '\n' +         
			      m.intersect(q) + '\n' +         
			      g.intersect(o) + '\n' +         
			      o.intersect(g));         
			            
			            
			            
			            
			            
			            
			   // equals         
			   System.out.println("\n\nequals tests\n" +  c.equals(a) + '\n' +         
			      a.equals(a) + '\n' +         
			      b.equals(b) + '\n' +         
			      c.equals(c) + '\n' +         
			      d.equals(e) + '\n' +         
			      a.equals(d) + '\n' +         
			      d.equals(b) + '\n' +           
			      p.equals(r) + '\n' +         
			      h.equals(n) + '\n' +         
			      n.equals(h) + '\n' +         
			      n.equals(o) + '\n' +         
			      o.equals(n) + '\n' +         
			      m.equals(p) + '\n' +         
			      p.equals(m) + '\n' +         
			      g.equals(o) + '\n' +         
			      o.equals(g) + '\n' +         
			      j.equals(m) + '\n' +         
			      m.equals(j) + '\n' +         
			      e.equals(f) + '\n' +         
			      f.equals(e));         
			            
			   // subset         
			   System.out.println("\n\nsubset tests\n" +  c.isSubsetOf(a) + '\n' +         
			      a.isSubsetOf(a) + '\n' +         
			      b.isSubsetOf(b) + '\n' +         
			      c.isSubsetOf(c) + '\n' +         
			      d.isSubsetOf(e) + '\n' +         
			      a.isSubsetOf(d) + '\n' +         
			      d.isSubsetOf(b) + '\n' +           
			      p.isSubsetOf(r) + '\n' +         
			      h.isSubsetOf(n) + '\n' +         
			      n.isSubsetOf(h) + '\n' +         
			      n.isSubsetOf(o) + '\n' +         
			      o.isSubsetOf(n) + '\n' +         
			      m.isSubsetOf(p) + '\n' +         
			      p.isSubsetOf(m) + '\n' +         
			      g.isSubsetOf(o) + '\n' +         
			      o.isSubsetOf(g) + '\n' +         
			      j.isSubsetOf(m) + '\n' +         
			      m.isSubsetOf(j) + '\n' +         
			      e.isSubsetOf(f) + '\n' +         
			      c.isSubsetOf(f) + '\n' +         
			      c.isSubsetOf(b) + '\n' +         
			      h.isSubsetOf(c) + '\n' +         
			      h.isSubsetOf(bb) + '\n' +         
			      h.isSubsetOf(n) + '\n' +         
			      k.isSubsetOf(p) + '\n' +         
			      x.isSubsetOf(r) + '\n' +         
			      y.isSubsetOf(h) + '\n' +         
			      z.isSubsetOf(ee) + '\n' +         
			      y.isSubsetOf(z) + '\n' +         
			      z.isSubsetOf(y) + '\n' +         
			      y.isSubsetOf(m) + '\n' +         
			      m.isSubsetOf(y) + '\n' +         
			      x.isSubsetOf(m) + '\n' +         
			      u.isSubsetOf(q) + '\n' +         
			      h.isSubsetOf(m) + '\n' +         
			      r.isSubsetOf(o) + '\n' +         
			      f.isSubsetOf(e));         
			            
			            
			            
			   // mutators and isIn         
			   System.out.println("\n\nisIn tests" +  a.isIn(0) + '\n' +         
			      a.isIn(1) + '\n' +         
			      b.isIn(2) + '\n' +         
			      c.isIn(3) + '\n' +         
			      d.isIn(4) + '\n' +         
			      a.isIn(5) + '\n' +         
			      d.isIn(6) + '\n' +           
			      p.isIn(7) + '\n' +         
			      h.isIn(8) + '\n' +         
			      n.isIn(9) + '\n' +         
			      n.isIn(10) + '\n' +         
			      o.isIn(11) + '\n' +         
			      m.isIn(12) + '\n' +         
			      p.isIn(13) + '\n' +         
			      g.isIn(14) + '\n' +         
			      o.isIn(15) + '\n' +         
			      j.isIn(16) + '\n' +         
			      m.isIn(17) + '\n' +         
			      e.isIn(18) + '\n' +         
			      f.isIn(19));         
			            
			            
			   // remove         
			   System.out.println("\n\nremove tests");         
			   b.remove(1);         
			   System.out.println("b contains 1 = " + b.isIn(1));         
			   r.remove(1);         
			   System.out.println("r contains 1 = " + r.isIn(1));         
			   n.remove(13);         
			   System.out.println("n contains 13 = " + n.isIn(13));         
			   p.remove(8);         
			   System.out.println("p contains 8 = " + p.isIn(8));         
			   j.remove(2);         
			   System.out.println("j contains 2 = " + j.isIn(2));         
			   m.remove(6);         
			   System.out.println("m contains 6 = " + m.isIn(6));         
			   q.remove(14);         
			   System.out.println("q contains 14 = " + q.isIn(14));         
			   a.remove(6);         
			   System.out.println("a contains 6 = " + a.isIn(6));         
			   d.remove(10);         
			   System.out.println("d contains 10 = " + d.isIn(10));         
			            
			   // display results         
			   System.out.println("" + b + '\n' + r + '\n' + n + '\n' + p + '\n'         
			      + j + '\n' + m         
			      + '\n' + q + '\n' + a + '\n' + d);         
			            
			   // add in the just removed items and repeat the output         
			   System.out.println("\n\nadd tests");         
			            
			   b.add(1);         
			   System.out.println("b contains 1 = " + b.isIn(1));         
			   r.add(1);         
			   System.out.println("r contains 1 = " + r.isIn(1));         
			   n.add(13);         
			   System.out.println("n contains 13 = " + n.isIn(13));         
			   p.add(8);         
			   System.out.println("p contains 8 = " + p.isIn(8));         
			   j.add(2);         
			   System.out.println("j contains 2 = " + j.isIn(2));         
			   m.add(6);         
			   System.out.println("m contains 6 = " + m.isIn(6));         
			   q.add(14);         
			   System.out.println("q contains 14 = " + q.isIn(14));         
			   a.add(6);         
			   System.out.println("a contains 6 = " + a.isIn(6));         
			   d.add(10);         
			   System.out.println("d contains 10 = " + d.isIn(10));         
			   k.add(6);         
			   System.out.println("k contains 6 = " + k.isIn(6));         
			   h.add(8);         
			   System.out.println("h contains 8 = " + h.isIn(8));         
			            
			            
			   // display results         
			   System.out.println("" + b + '\n' + r + '\n' + n + '\n' + p + '\n'         
			      + j + '\n' + m         
			      + '\n' + q + '\n' + a + '\n' + d + '\n' + k + '\n' + h);         
			            
			            
			            
			            
			            
			            
			            
			   a.add(0);         
			   a.remove(1);         
			   a.remove(5);         
			   b.add(2);         
			   c.add(3);         
			   d.remove(6);         
			   p.add(7);         
			   p.add(13);         
			   h.add(8);         
			   n.add(10);         
			   n.add(9);         
			   o.remove(11);         
			   o.remove(15);         
			   m.add(12);         
			   m.add(17);         
			   g.add(14);         
			   j.remove(16);         
			   e.remove(18);         
			   f.add(19);         
			            
			   System.out.println("\n\nmore mutator tests\n" +  a.isIn(0) + '\n' +         
			      a.isIn(1) + '\n' +         
			      b.isIn(2) + '\n' +         
			      c.isIn(3) + '\n' +         
			      d.isIn(4) + '\n' +         
			      a.isIn(5) + '\n' +         
			      d.isIn(6) + '\n' +           
			      p.isIn(7) + '\n' +         
			      h.isIn(8) + '\n' +         
			      n.isIn(9) + '\n' +         
			      n.isIn(10) + '\n' +         
			      o.isIn(11) + '\n' +         
			      m.isIn(12) + '\n' +         
			      p.isIn(13) + '\n' +         
			      g.isIn(14) + '\n' +         
			      o.isIn(15) + '\n' +         
			      j.isIn(16) + '\n' +         
			      m.isIn(17) + '\n' +         
			      e.isIn(18) + '\n' +         
			      f.isIn(19));         
			            
			   // complement and isIn         
			   // should see just the opposite values from the previous bunch, no?         
			   System.out.println("" + "\n\n" +  a.complement().isIn(0) + '\n' +         
			      a.complement().isIn(1) + '\n' +         
			      b.complement().isIn(2) + '\n' +         
			      c.complement().isIn(3) + '\n' +         
			      d.complement().isIn(4) + '\n' +         
			      a.complement().isIn(5) + '\n' +         
			      d.complement().isIn(6) + '\n' +           
			      p.complement().isIn(7) + '\n' +         
			      h.complement().isIn(8) + '\n' +         
			      n.complement().isIn(9) + '\n' +         
			      n.complement().isIn(10) + '\n' +         
			      o.complement().isIn(11) + '\n' +         
			      m.complement().isIn(12) + '\n' +         
			      p.complement().isIn(13) + '\n' +         
			      g.complement().isIn(14) + '\n' +         
			      o.complement().isIn(15) + '\n' +         
			      j.complement().isIn(16) + '\n' +         
			      m.complement().isIn(17) + '\n' +         
			      e.complement().isIn(18) + '\n' +         
			      f.complement().isIn(19));   
	
	}
}
