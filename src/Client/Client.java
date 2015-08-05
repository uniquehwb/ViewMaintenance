package Client;

import java.util.List;

import Plans.LogicPlan;
import Sql.AggregationsFinder;
import Sql.JSqlParser;
import Tree.Tree;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Client {

	public static void main(String[] args) throws JSQLParserException {
//		String queryString = "SELECT c1, SUM (c2) FROM t1 WHERE c2 < 10 GROUP BY (c1)";
		String queryString = "SELECT t1.c1, t2.c2, SUM (t1.c2) "
						   + "FROM t1 INNER JOIN t2 ON t1.c1 = t2.c1 "
						   + "WHERE t1.c2 > 10 AND t2.c3 = 20 "
						   + "GROUP BY t1.c1 ";
//		String queryString = "SELECT c1, c4, SUM(c2) FROM MY_TABLE1, MY_TABLE2, (SELECT c1 FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 "+
//							 "WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6) "+
//							 "GROUP BY c1";
		System.out.println("All legal orders: \n");
		
		
		// From SQL string to operation list
		JSqlParser parser = new JSqlParser(queryString);
		// From operation list to tree
		Tree tree = parser.parse();
		// From tree to logic plan
		LogicPlan logicPlan = new LogicPlan(tree);
		// From logic plan to cost model
		double totalCost = logicPlan.executePlan();
		
		
		System.out.println(totalCost);
		tree.traverseAllStructures();
//		System.out.println("");
//		System.out.println("Data source is: " + tree.dataSource);
//		System.out.println("");
//		System.out.println("Minimal cost is: " + tree.minimalCost);
//		System.out.println("Corresponding operation order is: " + tree.optimalOperationList);
		
	}
}
