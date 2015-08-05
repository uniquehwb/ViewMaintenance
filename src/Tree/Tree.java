package Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DummyData.DummyInputStream;
import Plans.LogicPlan;
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
	private Boolean validTree;
	private List<Operation> operationList;
	// store operation list based on different base table
	private Map<String, List<Operation>> operationListMap;
	static public List<Operation> optimalOperationList;
	static public double minimalCost = 100000000;
	// All table involved
	static public List dataSource;
	// From which table
	static public String fromTable;
	
	public Boolean getValidTree() {
		return validTree;
	}
	
	public Tree(List<Operation> operationList, Map<String, List<Operation>> operationListMap, int index) {
		cost = 0;
		this.operationList = operationList;
		this.operationListMap = operationListMap;
		// no join
		if (operationListMap.isEmpty()) {
			generateChain(operationList, index);
		} else {
			Iterator it = operationListMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				List<Operation> list = (List<Operation>) pair.getValue();
				Node node = generateChain(list, index);
			}
		}
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
	
	// Bottom-up recursively create a chain, if there is no join.
	public Node generateChain (List<Operation> operationList, int index) {
		if (index == operationList.size()) {
			RuleClient ruleClient = new RuleClient(operationList);
			if (ruleClient.checkAllRules()) {
				validTree = true;
			} else {
				validTree = false;
			}
			return null;
		}
	    Node currentNode = new Node();
	    if (index < operationList.size()) {
	    	Operation currentNodeOperation = operationList.get(index);
	    	currentNode.setOperation(currentNodeOperation);
			Node parent = new Node();
			parent = generateChain(operationList, index+1);
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
			} 
			currentNode.setParent(parent);
			if (index == 0) {
				// Add data source to the tree.
				BaseTable bt = new BaseTable();
	    		bt.setKeyWords(Tree.fromTable.toString());
	    		Node oneSuccessor = new Node();
	    		oneSuccessor.setOperation(bt);
	    		oneSuccessor.setParent(currentNode);
	    		root = oneSuccessor;
	    		List<Node> successors;
	    		if (currentNode.getSuccessors() != null) {
					successors = currentNode.getSuccessors();
				} else {
					successors = new ArrayList<Node>();
				}
				successors.add(oneSuccessor);
	    		currentNode.setSuccessors(successors);
				
			}
	    }
	    return currentNode;
	}
	
	// bottom-up recursively create a tree
	public Node generateTree (List<Operation> operationList, int index) {
		if (index == operationList.size()) {
			RuleClient ruleClient = new RuleClient(operationList);
			if (ruleClient.checkAllRules()) {
				validTree = true;
			} else {
				validTree = false;
			}
			return null;
		}
	    Node currentNode = new Node();
	    if (index < operationList.size()) {
	    	Operation currentNodeOperation = operationList.get(index);
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
			parent = generateTree(operationList, index+1);
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
		Tree tree = new Tree(operationList, operationListMap, 0);
		LogicPlan logicPlan = new LogicPlan(tree);
		double totalCost = logicPlan.executePlan();
		System.out.println(operationList);
		System.out.println("total cost is: " + totalCost);
		if (totalCost <= Tree.minimalCost) {
			Tree.minimalCost = totalCost;
			Tree.optimalOperationList = new ArrayList<Operation>();
			for (Operation operation: operationList) {
				Tree.optimalOperationList.add(operation);
			}
		}
	}
}
