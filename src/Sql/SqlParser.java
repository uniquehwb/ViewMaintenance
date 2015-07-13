package Sql;

import java.util.ArrayList;
import java.util.List;

import DummyData.DummyInputStream;
import Tree.Tree;
import ViewModels.AggregationOperation;
import ViewModels.DeltaOperation;
import ViewModels.JoinOperation;
import ViewModels.Operation;
import ViewModels.ProjectionOperation;
import ViewModels.SelectionOperation;


public class SqlParser {
	String sqlString;
	SqlInterpreter interpreter;
	// store all operation objects parsed from query
	List<Operation> operationList;
	String dataSource = "";
	
	public SqlParser(String sqlString) {
		this.sqlString = sqlString;
		interpreter = new SqlInterpreter(sqlString.toCharArray());
	}
	
	public String getSqlString() {
		return sqlString;
	}

	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}
	
	// Generate the dag according to interpreter.
	public Tree parse() {
		StringBuilder word = new StringBuilder();
		Operation currentOperation = null;
		operationList = new ArrayList<Operation>();
		Boolean shouldAddAggregationKey = false;
		Boolean shouldAddDataSource = false;
		
		while(interpreter.hasNext()){
			char currentChar = interpreter.read();
			// 1.not a space
			if(currentChar != ' '){
				word.append(currentChar);
			}
			// 2. a space
			else{
				Operation operation = null;
				// 2.1 select
				if(word.toString().equalsIgnoreCase("select")){
					operation = new ProjectionOperation();
					operationList.add(operation);
					currentOperation = operation;
				}
				// 2.2 where
				else if(word.toString().equalsIgnoreCase("where")){
					operation = new SelectionOperation();
					operationList.add(operation);
					currentOperation = operation;
				}
				// 2.3 aggregation
				else if(word.toString().equalsIgnoreCase("sum") || 
						word.toString().equalsIgnoreCase("count") ||
						word.toString().equalsIgnoreCase("min") ||
						word.toString().equalsIgnoreCase("max") ||
						word.toString().equalsIgnoreCase("avg")){
					operation = new AggregationOperation();
					operation.setExpression(word.toString());
					operationList.add(operation);
					// Add a delta operation if there is an aggregation operation
					Operation deltaOperation = new DeltaOperation();
					operationList.add(deltaOperation);
					
					currentOperation = operation;
				}
				// 2.4 and
				else if(word.toString().equalsIgnoreCase("and")){
					String expression = currentOperation.getExpression();
					expression += " and ";
					currentOperation.setExpression(expression);
				}
				// 2.5 or
				else if(word.toString().equalsIgnoreCase("or")){
					String expression = currentOperation.getExpression();
					expression += " or ";
					currentOperation.setExpression(expression);
				}
				// 2.6 group by, which decides aggregation key
				else if (word.toString().equalsIgnoreCase("group") || word.toString().equalsIgnoreCase("by")){
					shouldAddAggregationKey = true;
				}
				// 2.7
				else if (word.toString().equalsIgnoreCase("from")) {
					shouldAddDataSource = true;
				}
				else if (word.toString().equalsIgnoreCase("join")) {
					operation = new JoinOperation();
					operationList.add(operation);
					currentOperation = operation;
					shouldAddDataSource = true;
				}
				// 2.9 add one word
				else if(!word.toString().trim().equals("")){
					if (shouldAddAggregationKey) {
						for (Operation op: operationList) {
							// Group by belongs to aggregation view to define the aggregation key.
							if (op.getKeyWords() == "AGGREGATION") {
								String expression = op.getExpression();
								expression = expression + " GROUPBY" + word.toString().trim();
								op.setExpression(expression);
								break;
							}
						}
						shouldAddAggregationKey = false;
					} else if (shouldAddDataSource) {
						dataSource = dataSource + " " + word.toString();
						shouldAddDataSource = false;
					} else {
						String expression = currentOperation.getExpression();
	
						if (expression == null) {
							expression = word.toString().trim();
						} else {
							expression += word.toString().trim();
						}
						currentOperation.setExpression(expression);
					}
				}
				
				// Reset the current word
				word = new StringBuilder();
			}
		}
		// Add last word to expression
		if(!word.toString().trim().equals("")){
			String expression = currentOperation.getExpression();
			if (expression == null) {
				expression = word.toString().trim();
			} else {
				expression += word.toString().trim();
			}
			currentOperation.setExpression(expression);
		}
		
		DummyInputStream dummyInput = new DummyInputStream(100, "Raw");
		Tree dag = new Tree(operationList, 0, dummyInput.getStream());
//		Tree.dataSource = dataSource;
		return dag;
	}

	public class SqlInterpreter{
		public char[] sqlString = null;
		public int readIndex = 0;
		
		public SqlInterpreter(char[] sqlString){
			this.sqlString = sqlString;
		}
		
		/**
		 * Read one char
		 * @return
		 */
		public char read(){
			return sqlString[readIndex++];
		}
		
		/**
		 * Has next char
		 * @return
		 */
		public boolean hasNext(){
			return readIndex<sqlString.length;
		}
	}
	
}