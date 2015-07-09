package Client;

import DAG.DAG;
import Sql.SqlParser;

public class Client {

	public static void main(String[] args) {
//		String queryString = "SELECT t1.c1, SUM (t1.c2) "
//						   + "FROM t1 JOIN t2 "
//						   + "GROUP BY (t1.c1) "
//						   + "WHERE t1.c2 < 10 AND t1.c1 = t2.c1";
		String queryString = "SELECT c1, SUM (c2) FROM t1 GROUP BY (c1) WHERE c2 < 10";
		SqlParser parser = new SqlParser(queryString);
		System.out.println("All legal orders: \n");
		// DAG contains a rootNode, whose successors are created recursively.
		DAG dag = parser.parse();
		dag.traverseAllStructures();
		System.out.println("");
		System.out.println("Data source is: " + DAG.dataSource);
		System.out.println("");
		System.out.println("Minimal cost is: " + DAG.minimalCost);
		System.out.println("Corresponding operation order is: " + DAG.optimalOperationList);
	}
	
}
