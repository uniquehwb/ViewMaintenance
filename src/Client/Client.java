package Client;

import java.util.List;

import Sql.SqlParser;
import Tree.Tree;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Client {

	public static void main(String[] args) throws JSQLParserException {
//		String queryString = "SELECT t1.c1, SUM (t1.c2) "
//						   + "FROM t1 JOIN t2 "
//						   + "GROUP BY (t1.c1) "
//						   + "WHERE t1.c2 < 10 AND t1.c1 = t2.c1";
		String queryString = "SELECT c1, SUM (c2) FROM t1 GROUP BY (c1) WHERE c2 < 10";
		SqlParser parser = new SqlParser(queryString);
		System.out.println("All legal orders: \n");
		// DAG contains a rootNode, whose successors are created recursively.
		Tree tree = parser.parse();
		tree.traverseAllStructures();
		System.out.println("");
		System.out.println("Data source is: " + tree.dataSource);
		System.out.println("");
		System.out.println("Minimal cost is: " + tree.minimalCost);
		System.out.println("Corresponding operation order is: " + tree.optimalOperationList);
		
//		String sql = "SELECT * FROM MY_TABLE1, MY_TABLE2, (SELECT c1 FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 "+
//				" WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6)" ;
//		Statement statement = CCJSqlParserUtil.parse(sql);
//		if (statement instanceof Select) {
//			SelectBody select = ((Select) statement).getSelectBody();
//			// Single
//			if (select instanceof PlainSelect) {
//				PlainSelect pselect = (PlainSelect) select;
//				String joinSql = pselect.getJoins().get(1).toString();
//				Statement s = CCJSqlParserUtil.parse(joinSql);
//				if (s instanceof Select) {
//					PlainSelect psl = (PlainSelect) ((Select) s).getSelectBody();
//					TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//					List<String> tableList = tablesNamesFinder.getTableList((Select) s);
//					System.out.println(tableList);
//				}
//			}
//		} 
			
		
//		Select selectStatement = (Select) statement;
//		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//		List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
//		System.out.println(tableList);
	}
	
}
