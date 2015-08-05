package ViewModels;

import Stream.Stream;
import CostModel.Cost;
import DummyData.DummyInputStream;


public class SelectionOperation implements Operation{
	// keyWords is name of the base table where the selection will be performed
	private String keyWords;
	private String expression;
	// Default materialized
	private boolean pipelined = false;
	// Input stream is decided by output stream of last operation, including type and transactions
	private Stream input = null;
	// Output stream is decided by current operation, including type and transactions
	private Stream output = null;
	// For selection there are get cost, put cost, delete cost and forward cost
	private Cost cost = new Cost();
	// Probability to satisfy the condition
	private double hitRatio = 0.1;
	// Percentage of update in the put
	private double updateRatio = 0.5;
	
	public Cost getCost() {
		return cost;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords; 
	}
	public String getKeyWords() {
		return keyWords;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public boolean getPipelined() {
		return pipelined;
	}
	public void setPipelined(boolean pipelined) {
		if (input.getType() == "delta") {
			this.pipelined = pipelined;
		} else {
			this.pipelined = false;
		}
	}
	public Stream getInput() {
		return input;
	}
	public void setInput(Stream input) {
		this.input = input;
		
		// After input stream is defined, cost model can be initialized.
		this.setCost();
		
		// After input stream is defined, output stream can be initialized as well
		DummyInputStream dummyInput = new DummyInputStream(50, "Raw");
		this.setOutput(dummyInput.getStream());
	}
	public Stream getOutput() {
		return output;
	}
	public void setOutput(Stream output) {
		this.output = output;
	}
	
	public void setCost(){
		if (pipelined == false) {
			if (input.getType() == "Raw") {
				// Firstly, insert and update should be distinguished from put. In order to get the
				// quantity of insert and update respectively, get operation has to be performed.
				cost.setGetQuantity(input.getPutOperations().length);
				// Quantity of put in the internal execution is quantity of put in the input stream
				// multiplied by hitRatio of the selectivity.
				cost.setPutQuantity(input.getPutOperations().length * hitRatio);
				// Quantity of delete in the internal execution is quantity of delete in the input 
				// stream plus quantity of update multiplied by (1 - hitRatio).
				double updateQuantity = input.getPutOperations().length * updateRatio;
				double deleteQuantity = input.getDeleteOperations().length + updateQuantity * (1 - hitRatio);
				cost.setDeleteQuantity(deleteQuantity);
				// Quantity of forward equals to size of input operations.
				cost.setForwardQuantity(input.getPutOperations().length + input.getDeleteOperations().length);
			} else if (input.getType() == "Delta") {
				// Quantity of put in the internal execution is quantity of put in the input stream
				// multiplied by hitRatio of the selectivity.
				cost.setPutQuantity(input.getPutOperations().length * hitRatio);
				// Quantity of delete in the internal execution is quantity of delete satisfying 
				// selectivity plus quantity of update multiplied by (1 - hitRatio).
				double updateQuantity = input.getPutOperations().length * updateRatio;
				double deleteQuantity = input.getDeleteOperations().length * hitRatio + updateQuantity * (1 - hitRatio);
				cost.setDeleteQuantity(deleteQuantity);
				// Quantity of forward equals to size of input operations.
				cost.setForwardQuantity(input.getPutOperations().length + input.getDeleteOperations().length);
			}
		} else {
			// Quantity of forward equals to size of input operations.
			cost.setForwardQuantity(input.getPutOperations().length + input.getDeleteOperations().length);
		}
	}

	public double getHitRatio() {
		return hitRatio;
	}
	public void setHitRatio(double d) {
		this.hitRatio = d;
	}
	
//	public void setLocalOptimal(){
//		if (input == null) {
//			pipelined = true;
//		} else {
//			// only when input stream is delta stream, the operation can be pipelined.
//			setPipelined(true);
//		}
//	}
	public void updateHBase() {
		for (int i = 0; i < input.getPutOperations().length; i++) {
			// Operation is pipelined
			if (pipelined) {
				// If condition is meet, then 
				//    1. construct a output transaction according to rules (raw or delta)
				//    2. put it into transactions of output stream
			} 
			// Operation is materialized
			else 
			{
				// If condition is meet, then 
				//    1. apply transaction, HBase.put
				//    2. construct a output transaction according to rules (raw or delta)
				//    3. put it into transactions of output stream
			}
		}
	}
	public void propagateUpdate() {
		// Set output stream as input stream of next operation
	}
}
