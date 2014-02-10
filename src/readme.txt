Mohammad Shaarif Zia
6646435391
mohammsz@usc.edu

Language used-Java

PROGRAM DESCRIPTION
The program is divided into two classes:
pl.java
Rule.java

An object of Rule.java represents a logical rule. It has fields like precedent and consequent part of rule.It has also one more field called 'used' which tells the state in the case of forward chaining. 

pl.java contains following functions:
main()-Takes the arguments, processes the input file and selects the task to perform.
processKBfile() & processquery()-Takes the input files, parses it and stores the Knowledge base and query.
forwardchain() performs forward chaining with the help of other utility function like myequal() and comb2().
backwardchain() performs backward chaining with the help of recursive function backchain().
For task 3, it has parent function resolution() which makes use of other function like convertToCNF() which converts rule into CNF and resolve() which tells if any two clauses can be resolved or not.
 

ANALYSIS OF SIMILARITIES/DIFFERENCES BETWEEN TASK 2 AND 3
In all the tasks, we are performing algorithms in order to tell whether a particular query is entailed by the knowledge base or not.

The Differences among tasks can be stated as follows. In task 1, we started with the facts and then moved in forward direction to get newly entailed facts. In task 2, we started with the goal and then go in a backward direction to get more goals which can be true. There is a possiblity of cycle in task 2. In task 3, we perform multiple combinations of the given clauses and add new ones in order to resolve the clause to empty.



HOW TO COMPILE/EXECUTE
The Program needs java version 1.6 or higher on aludra.
For compiling(we have to compile all the java files(Rule.java,pl.java))

javac *.java

For execution

java pl -t <task> -kb <kb_input> -q <input_file> -oe <output_entail> -ol <output_log>

where task can be 1,2,3