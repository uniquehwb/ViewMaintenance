package Sql;

import java.util.ArrayList;
import java.util.List;

import DAG.DAG;
import DummyData.DummyInputStream;
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
	public DAG parse() {
		StringBuilder word = new StringBuilder();
		Operation currentOperation = null;
		operationList = new ArrayList<Operation>();
		
		while(interpreter.hasNext()){
			char currentChar = interpreter.read();
//			System.out.println("current char is " + currentChar);
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
//				// 2.6 from, maybe a join
//				else if (!word.toString().trim().equals("from")){
//					operation = new JoinOperation();
//					currentOperation = operation;
//				}
				// 2.7 add one word
				else if(!word.toString().trim().equals("")){
					String expression = currentOperation.getExpression();
//					if (currentOperation.getKeyWords() == "FROM") {
//						String[] expressions = word.toString().split(",");
//						// not a join
//						if (expressions.length == 1) {
//							
//						}
//					}
					if (expression == null) {
						expression = word.toString().trim();
					} else {
						expression += word.toString().trim();
					}
					currentOperation.setExpression(expression);
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
		DAG dag = new DAG(operationList, 0, dummyInput.getStream());
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