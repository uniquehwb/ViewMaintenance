package Client;

import DAG.DAG;
import Sql.SqlParser;

public class Client {

	public static void main(String[] args) {
		String queryString = "SELECT c1, SUM (c2) FROM t1 GROUP BY (c1) WHERE c2 < 10";
		SqlParser parser = new SqlParser(queryString);
		System.out.println("All legal orders: \n");
		// DAG contains a rootNode, whose successors are created recursively.
		DAG dag = parser.parse();
		dag.traverseAllStructures();
		System.out.println("");
		System.out.println("Minimal cost is: " + DAG.minimalCost);
		System.out.println("Corresponding operation order is: " + DAG.optimalOperationList);
	}
	
}
