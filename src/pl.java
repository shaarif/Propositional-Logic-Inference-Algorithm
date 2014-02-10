import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;


public class pl {
	/**
	 * @param args
	 */
	static ArrayList<Rule> knowledgeBase = new ArrayList<Rule>();
	static ArrayList<String> query=new ArrayList<String>();
	static ArrayList<String> fact=new ArrayList<String>();
	static PrintWriter writerLog;
	static PrintWriter writerOutput;
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		int task=Integer.parseInt(args[1]);
		String kbfile=args[3];
	    String queryfile=args[5];
	    String outputentail=args[7];
	    String outputlog=args[9];
	    writerLog = new PrintWriter(outputlog, "UTF-8");
	    writerOutput= new PrintWriter(outputentail, "UTF-8");
	    processKBfile(kbfile);
	    processquery(queryfile);
	    switch (task)
	    {
	    case 1:
	    	forwardchain();
	    	break;
	    case 2:
	    	backwardchain();
	    	break;
	    case 3:
	    	resolution();
	    }
	}
	static void resolution()
	{
		writerLog.println("Resolving clause 1#Resolving clause 2#Added clause");
		/* Conversion in CNF*/
		ArrayList<String> clauses_CNF = new ArrayList<String>();
		for(String entail:query)
			{
			clauses_CNF=convertToCNF();
			clauses_CNF.add(negation(entail));
			boolean result=checkresolution(clauses_CNF,entail);
			if(result==true)
				writerOutput.println("YES");
			else
				writerOutput.println("NO");
			writerLog.println("------------------------------------------------------------------");
			}
		writerLog.close();
		writerOutput.close();
		
	}
	static boolean checkresolution(ArrayList<String> clauses_CNF,String entail)
	{
		ArrayList<String> newClauses=new ArrayList<String>();
			int iteration=1;
			//start the loop
			while(iteration<=6)
			{
				
				writerLog.println("ITERATION = "+iteration);
			for(int i=0; i<clauses_CNF.size(); i++)   // selecting the first clause
			{
				for(int j=i+1; j<clauses_CNF.size(); j++)
				{
					String resolvedClause=null;
					String clause1=clauses_CNF.get(i);
					String clause2=clauses_CNF.get(j);
					if(!clause1.equals(clause2))
					{
					resolvedClause=resolve(clause1,clause2);
					if(resolvedClause!=null)
						writerLog.println(clause1+" # "+clause2+" # "+resolvedClause);
					if(resolvedClause!=null && !resolvedClause.equals("Empty"))
						newClauses.add(resolvedClause);
					if(resolvedClause!=null && resolvedClause.equals("Empty"))
						return true;
					}
					
				}
			}
			for(String claus:newClauses)
			{
				if(!checkifPresent(claus,clauses_CNF))
					{ 
					  clauses_CNF.add(claus);
					}
			}
			iteration++;
			newClauses.clear();
			}
      return false;
	}
	static boolean checkifPresent(String newclause,ArrayList<String> clauses_CNF) 
	{
		String[] newclausetokens=newclause.split(" OR ");
		Arrays.sort(newclausetokens);
		String newc="";
		int flag=0;
		for(String b:newclausetokens)
		{
			newc+=b;
		}
		for(String clause:clauses_CNF)
		{
			String[] clausetokens=clause.split(" OR ");
			Arrays.sort(clausetokens);
			String oldc="";
			for(String a:clausetokens)
			{
				oldc+=a;
			}
			if(oldc.equals(newc))
				flag=1;
		}
		if(flag==1)
			return true;
		else
			return false;
	}
	static String resolve(String clause1,String clause2)
	{
		int flag=0;
		String result="";
		String[] clause1_tokens=clause1.split(" ");
		String[] clause2_tokens=clause2.split(" ");
		ArrayList<String> clause1tok=new ArrayList<String>();
		ArrayList<String> clause2tok=new ArrayList<String>();
		//removing OR's
		for(String c1:clause1_tokens)
		{
			if(!c1.equals("OR"))
				clause1tok.add(c1);
		}
		for(String c2:clause2_tokens)
		{
			if(!c2.equals("OR"))
				clause2tok.add(c2);
		}
		Iterator<String> it = clause1tok.iterator();
		while(it.hasNext())
		{
			String klaus=it.next();
			String neg=negation(klaus);
			if(clause2tok.contains(neg))   //only resolve if the negation exist in other clause
			{
				clause2tok.remove(neg);
				it.remove();
				flag++;
			}
		}
		if(flag>1)
		{
			return null;
		}
		if(clause1tok.isEmpty() && clause2tok.isEmpty())
			return "Empty";
		if(flag==1)
		{
		ArrayList<String> finalList=new ArrayList<String>();
		Iterator<String> it1 = clause1tok.iterator();
		Iterator<String> it2 = clause2tok.iterator();
		while(it1.hasNext())
		{   String str=it1.next();
			if(!finalList.contains(str))
			finalList.add(str);
		}
		while(it2.hasNext())
		{   String str=it2.next();
		    if(!finalList.contains(str))
			finalList.add(str);
		}
		int i=1;
		for(String str:finalList)
		{
			result+=str;
			if(i<finalList.size())
				result+=" OR ";
			i++;
		}
		return result;
		}
		return null;
	}
	static String negation(String str)
	{
		String result;
		if(str.charAt(0)!='-')
			result="-"+str;
		    else
		    	result=Character.toString(str.charAt(1));
		return result;
	}
	static ArrayList<String> convertToCNF()
	{
		ArrayList<String> clauses_CNF = new ArrayList<String>();
		for(Rule entry: knowledgeBase)
		{
			String cnf="";
			if(entry.consequent!=null)
			{
				String prec= entry.precedent;
				int len=prec.length()-1;
				int i=0;
				while(i<=len)
				{
					cnf+="-"+prec.charAt(i)+" OR ";
					i++;
				}
				cnf+=entry.consequent;
				
			}
			else
			{
				cnf=entry.precedent;
			}
			clauses_CNF.add(cnf);
		}
		return clauses_CNF;
	}
	static void backwardchain()
	{
		writerLog.println("<Queue of Goals>#Relevant Rules/Fact#New Goal Introduced");
		for(String entail:query)
		{
			ArrayList<String> facts= new ArrayList<String>();
			ArrayList<Rule> clauses = new ArrayList<Rule>();
			LinkedList<String> goals=new LinkedList<String>();
			//get the facts from knowledge base
			for(Rule entry: knowledgeBase)
			{
				if(entry.consequent==null)
				{
					facts.add(entry.precedent);
				}
				else
				{
					clauses.add(entry);
				}
			}
			goals.add(entail);   // add to the goal queue
			int flag=0;
			boolean result = backchain(facts,clauses,goals,entail,flag);
			if(result == true)
		    {
				writerOutput.println("YES");
		    }
		    else if(result == false)
		    {
		    	writerOutput.println("NO");
		    }
			writerLog.println("-------------------------------------------------------------");
		}
		writerOutput.close();
		writerLog.close();
	}
	static boolean backchain(ArrayList<String> facts,ArrayList<Rule> clauses,LinkedList<String> goals,String entail,int flag)
	{
		if(goals.isEmpty())
			return true;
		else
		{
			String topOfQueue=goals.remove();
			/*Cycle detection starts */ 
	    	 if((topOfQueue.equals(entail)) && flag !=0)
	    	 {
	    		 writerLog.println(topOfQueue+" # CYCLE DETECTED # "+"N/A");
	    		 return false;
	    	 }
	    	 flag = 1; 
	    	 /*Cycle detection ends */
	    	 // check if topofQueue is a fact
	    	 if(facts.contains(topOfQueue))
			 {
	    		 writerLog.println(topOfQueue+" # "+topOfQueue+" # N/A");
	    		 return backchain(facts,clauses,goals,entail,flag);
			 }
	    	 else
	    	 {   int flag2=0;
	    		 for (Iterator<Rule> iterator = clauses.iterator(); iterator.hasNext(); ) {
						Rule entry=iterator.next();
						if(entry.consequent.equals(topOfQueue))
						{
							flag2=1;
							String prec=entry.precedent;
							char[] precs=prec.toCharArray();
							String newgoals="";
							writerLog.print(topOfQueue+" # "+topOfQueue+" :- ");
							for(int i=0;i<precs.length;i++)
							{
								writerLog.print(precs[i]);
								newgoals=newgoals+precs[i];
								if(i<precs.length-1)
								{
									writerLog.print(",");
									newgoals=newgoals+", ";
								}		
							}
							writerLog.println(" # "+newgoals);
							for(int i=precs.length-1;i>=0;i--)
							{
										goals.addFirst(Character.toString(precs[i]));	
							}
							boolean result = backchain(facts,clauses,goals,entail,flag);
			    			if(result == true)
			    			   {
			    				   return true;
			    			   }
			    			else
			    			  {
			    				goals.clear();
			    			  }  
						}
						
	    	 }
	    		 if(flag2==0)
	    		 {
	    			 writerLog.println(topOfQueue+" # N/A # N/A");
					return false;
	    		 }
	    		 return false;
		     }
		}
	}
	static void forwardchain()
	{
		int flag=0;
		int flag2=0;
		writerLog.println("<Known/Deducted facts>#Rules Fires#NewlyEntailedFacts");
		// for each string in query do the forward chain
		for(String entail:query)
		{
		// add to the facts
			String facts="";
			ArrayList<Rule> localknowledgeBase = new ArrayList<Rule>();
			for (Rule entry : knowledgeBase) {
				localknowledgeBase.add(entry);
				if(entry.consequent==null)
				{
						facts=facts+""+entry.precedent;
				}
			}
		// check knowledge base for fired rules
		while(!facts.contains(entail))
		{
			//get the different form of facts
			comb2(facts); //it gives all the combination of facts into fact
			for(String f: fact) //for every fact
			{
				flag=0;
				for (Iterator<Rule> iterator = localknowledgeBase.iterator(); iterator.hasNext(); ) {
				Rule entry=iterator.next();
				//do not check for null values keys
				if(entry.consequent!=null)
				{
					String prec=entry.precedent;
					/*check if the rule exist*/
					//check if different form of facts exist  as prec if not then say it is not entailed
						if(myequal(f, prec))
						{
							flag=1;
							char[] fac=facts.toCharArray();
							int start=0;
							while(start<fac.length)
							{
								writerLog.print(fac[start]);
								start++;
								if(start<fac.length)
									writerLog.print(", ");
							}
							writerLog.print("#"+entry.consequent+" :- ");
							char[] precs=prec.toCharArray();
							int index=0;
							while(index<precs.length)
							{
								writerLog.print(precs[index]);
								index++;
								if(index<precs.length)
									writerLog.print(",");
							}
							writerLog.println(" # "+entry.consequent);
							facts=facts+entry.consequent;
							iterator.remove();
						}
					}	
				}
			if(flag==1)
				break;
			}
			if(flag==0)
			{
				flag2=1;
				break;
			}
			}
		if(flag2==1)
			writerOutput.println("NO");
		else
			writerOutput.println("YES");
		flag2=0;
		writerLog.println("---------------------------------------------------------");
		}
		writerOutput.close();
		writerLog.close();
		}
	static boolean myequal(String a, String b)
	{
		if(a.length()!=b.length())
			return false;
		else
		{
			int i=0;
			while(i<a.length())
			{
				String str=Character.toString(b.charAt(i));
				if(!a.contains(str))
					return false;
				i++;
			}
		}
		return true;
	}
	static void comb2(String s)
	{ comb2("", s); }
    static void comb2(String prefix, String s) {
        if(prefix!="")
    	fact.add(prefix);
        for (int i = 0; i < s.length(); i++)
            comb2(prefix + s.charAt(i), s.substring(i + 1));
    } 
	static void processquery(String path)
	{
		BufferedReader br;
		try{
			br= new BufferedReader( new FileReader(path));
			String line;
			while ((line = br.readLine()) != null) {
				query.add(line);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	static void processKBfile(String path)
	{
		BufferedReader br;
		try{
			br= new BufferedReader( new FileReader(path));
			String line,consequent,prec;
	        while ((line = br.readLine()) != null) {
	        	ArrayList<String> precedent=new ArrayList<String>();
	        	if(line.contains(":-"))
	        	{
	        		String[] tokens=line.split(":-");
	        		consequent=tokens[0].replaceAll("\\s+","");
	        		prec=tokens[1].replaceAll("\\s+","");
	        		if(prec.contains(","))
	        			{
	        			String[] tok=prec.split(",");
	        				for(String t: tok)
	        				{
	        					precedent.add(t);
	        				}
	        			}
	        		else
	        			{
	        				precedent.add(prec);
	        			}
	        		String preced="";
	        		for(String p:precedent)
	        			preced=preced+p;
	        		Rule r=new Rule(preced,consequent);
	        		knowledgeBase.add(r);
	        	}
	        	else //facts
	        	{
	        		precedent.add(line.replaceAll("\\s+",""));
	        		String preced="";
	        		for(String p:precedent)
	        			preced=preced+p;
	        		Rule r=new Rule(preced,null);
	        		knowledgeBase.add(r);
	        	}	
	        }
	        br.close();
		}catch (Exception e)
		{
			System.out.println(e);
		}
	}

}
