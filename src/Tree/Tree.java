package Tree;

import java.util.ArrayList;
import java.util.List;

import DummyData.DummyInputStream;
import Rule.RuleClient;
import Stream.Stream;
import ViewModels.AggregationOperation;
import ViewModels.BaseTable;
import ViewModels.JoinOperation;
import ViewModels.Operation;

public class Tree {
	private Node root;
	// Total view maintenance cost of the tree
	private double cost;
	private List<Operation> operationList;
	static public List<Operation> optimalOperationList;
	static public double minimalCost = 100000000;
	// All table involved
	static public List dataSource;
	// From which table
	static public String fromTable;
	
	public double getCost(){
		return cost;
	}
	
	public Tree(List<Operation> operationList, int index, Stream input) {
		cost = 0;
		generateTree(operationList, index, input);
	}
	
	public static class Node{
		private List<Node> successors;
		private Node parent;
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
		public Node getParent() {
			return parent;
		}
		public void setParent(Node parent) {
			this.parent = parent;
		} 
   }

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	// bottom-up recursively create a tree
	public Node generateTree (List<Operation> operationList, int index, Stream input) {
		if (index == operationList.size()) {
			RuleClient ruleClient = new RuleClient(operationList);
			if (ruleClient.checkAllRules()) {
				System.out.println(operationList);
				System.out.println("total cost is: " + cost);
			}
			return null;
		}
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
	    	// Add base table to join operation
	    	if (currentNode.getOperation().getClass().equals(JoinOperation.class)) {
	    		BaseTable bt = new BaseTable();
	    		bt.setKeyWords(currentNode.getOperation().getExpression());
	    		Node oneSuccessor = new Node();
	    		oneSuccessor.setOperation(bt);
	    		List<Node> successors;
	    		if (currentNode.getSuccessors() != null) {
					successors = currentNode.getSuccessors();
				} else {
					successors = new ArrayList<Node>();
				}
				successors.add(oneSuccessor);
	    		currentNode.setSuccessors(successors);
	    	}
			Node parent = new Node();
			parent = generateTree(operationList, index+1, currentNodeOperation.getOutput());
			// Append the current node as successor of the parent.
			if (parent != null) {
				Node oneSuccessor = currentNode;
				List<Node> successors;
				if (parent.getSuccessors() != null) {
					successors = parent.getSuccessors();
				} else {
					successors = new ArrayList<Node>();
				}
				successors.add(oneSuccessor);
				parent.setSuccessors(successors);
			} else {
				// If parent of the current node is null, 
				// then the current node should be the root node.
				root = currentNode;
			}
			currentNode.setParent(parent);
			if (index == 0) {
				// Add data source to the tree.
				BaseTable bt = new BaseTable();
	    		bt.setKeyWords(Tree.fromTable.toString());
	    		Node oneSuccessor = new Node();
	    		oneSuccessor.setOperation(bt);
	    		List<Node> successors;
	    		if (currentNode.getSuccessors() != null) {
					successors = currentNode.getSuccessors();
				} else {
					successors = new ArrayList<Node>();
				}
				successors.add(oneSuccessor);
	    		currentNode.setSuccessors(successors);
	    		
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
	
	// get all orders of possible trees
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
		Tree dag = new Tree(operationList, 0, dummyInput.getStream());
		System.out.println(operationList);
		System.out.println("total cost is: " + dag.getCost());
		if (dag.getCost() <= Tree.minimalCost) {
			Tree.minimalCost = dag.getCost();
			Tree.optimalOperationList = new ArrayList<Operation>();
			for (Operation operation: operationList) {
				Tree.optimalOperationList.add(operation);
			}
		}
	}
}
