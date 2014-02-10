public class Rule {
String precedent;
String consequent;
int used;
public Rule(String p, String c)
{
	this.precedent=p;
	this.consequent=c;
	used=0;
}
}
