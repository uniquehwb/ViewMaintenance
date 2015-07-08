package ViewModels;

import Stream.Stream;
import CostModel.Cost;


public class ProjectionOperation implements Operation{
	private String keyWords = "PROJECTION";
	private String expression;
	// Default materialized
	private boolean pipelined = false;
	// Input stream is decided by output stream of last operation, including type and transactions
	private Stream input = null;
	// Output stream is decided by current operation, including type and transactions
	private Stream output = null;
	// For projection there are put cost, delete cost and forward cost
	private Cost cost = new Cost();
	
	public Cost getCost() {
		return cost;
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
		input.setType("Raw");
		// The type of output stream should be raw
		this.setOutput(input);
	}
	public Stream getOutput() {
		return output;
	}
	public void setOutput(Stream output) {
		this.output = output;
	}
	
	public void setCost(){
		if (pipelined == false) {
			// Internal execution is same as input stream
			double putQuantity = input.getPutOperations().length;
			cost.setPutQuantity(putQuantity);
			double deleteQuantity = input.getDeleteOperations().length;
			cost.setDeleteQuantity(deleteQuantity);
			double forwardQuantity = putQuantity + deleteQuantity;
			cost.setForwardQuantity(forwardQuantity);
		} else {
			// Quantity of forward equals to size of input operations.
			double putQuantity = input.getPutOperations().length;
			double deleteQuantity = input.getDeleteOperations().length;
			double forwardQuantity = putQuantity + deleteQuantity;
			cost.setForwardQuantity(forwardQuantity);
		}
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
