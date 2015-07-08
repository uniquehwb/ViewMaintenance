package DAG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DummyData.DummyInputStream;
import Rule.RuleClient;
import Stream.Stream;
import ViewModels.Operation;

public class DAG {
	private Node root;
	// Total view maintenance cost of the dag
	private double cost;
	private List<Operation> operationList;
	static public List<Operation> optimalOperationList;
	static public double minimalCost = 100000000;
	
	public double getCost(){
		return cost;
	}
	
	public DAG(List<Operation> operationList, int index, Stream input) {
		cost = 0;
		generateDAG(operationList, index, input);
	}
	
	public static class Node{
		private List<Node> successors;
		// value in node
		private Operation operation;
		public List<Node> getSuccessors() {
			return successors;
		}
		public void setSuccessors(List<Node> successors) {
			this.successors = successors;
		}
		public Operation getOperation() {
			return operation;
		}
		public void setOperation(Operation operation) {
			this.operation = operation;
		} 
   }

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	// recursively create a DAG
	public Node generateDAG (List<Operation> operationList, int index, Stream input) {
		if (index == operationList.size()) 
			return null;
	    Node currentNode = new Node();
	    if (index < operationList.size()) {
	    	Operation currentNodeOperation = operationList.get(index);
	    	currentNodeOperation.setInput(input);
	    	double nodeCost = currentNodeOperation.getCost().computeTotalCost();
	    	cost = cost + nodeCost; 
	    	// output should be reconstructed according to operation in real.
	    	// currentNodeOperation.updateHBase();
//	    	currentNodeOperation.setOutput(input);
	    	currentNode.setOperation(currentNodeOperation);
	    	List<Node> successors = new ArrayList<Node>();
			Node oneSuccessor = new Node();
			oneSuccessor = generateDAG(operationList, index+1, currentNodeOperation.getOutput());
			successors.add(oneSuccessor);
			currentNode.setSuccessors(successors);
			if (index == 0) {
				root = currentNode;
				// Save the operation list for traversing in the future
				this.operationList = new ArrayList<Operation>();
				this.operationList = operationList;
				
			}
	    }
	    return currentNode;
	}
	
	public void traverseAllStructures() {
		// print all possible operation orders
		getAllOrder(0, operationList.size() - 1); 
	}
	
	// get all orders of possible DAG
	public void getAllOrder(int begin, int end) {  
	    if (begin == end) {  
	    	check();  
	    } else {  
	    	for (int i = begin; i <= end; i++) {  
	    		swap(begin, i);  
	    		getAllOrder(begin + 1, end);  
	    		swap(i, begin);  
	    	}  
	    }  
	}  
	
	public void swap(int from, int to) {  
	    if (from == to) {  
	    	return;  
	    }  
	    Operation tmp = operationList.get(from);
	    operationList.set(from, operationList.get(to));  
	    operationList.set(to, tmp);  
	}  
	
	// Generation of the DAG is finished.
	public void check() {  
		RuleClient ruleClient = new RuleClient(operationList);
		if (!ruleClient.checkAllRules()) {
			return;
		}
		DummyInputStream dummyInput = new DummyInputStream(100, "Raw");
		DAG dag = new DAG(operationList, 0, dummyInput.getStream());
		System.out.println(operationList);
		System.out.println("total cost is: " + dag.getCost());
		if (dag.getCost() <= DAG.minimalCost) {
			DAG.minimalCost = dag.getCost();
			DAG.optimalOperationList = new ArrayList<Operation>();
			for (Operation operation: operationList) {
				DAG.optimalOperationList.add(operation);
			}
		}
	}
}
