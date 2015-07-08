package ViewModels;

import Stream.Stream;
import CostModel.Cost;
import DummyData.DummyInputStream;


public class AggregationOperation implements Operation{
	private String keyWords = "AGGREGATION";
	private String expression;
	// Default materialized
	private boolean pipelined = false;
	// Input stream is decided by output stream of last operation, including type and transactions
	private Stream input = null;
	// Output stream is decided by current operation, including type and transactions
	private Stream output = null;
	// For aggregation there are get cost, put cost, delete cost and forward cost
	private Cost cost = new Cost();
	// Not zero update ratio
	private double notZeroUpdateRatio = 0.5;
	// Not zero delete ratio
	private double notZeroDeleteRatio = 0.5;
	// Update ratio in the put
	private double updateRatio = 0.5;
	
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
		if (input.getType() == "Delta") {
			this.setCost();
			// After input stream is defined, output stream can be initialized as well.
			input.setType("Raw");
			// The type of output stream should be raw
			this.setOutput(input);
		} else {
			// In order to not interrupt the whole DAG generation process, still set cost and output.
			this.setCost();
			this.setOutput(input);
		}
		
	}
	public Stream getOutput() {
		return output;
	}
	public void setOutput(Stream output) {
		this.output = output;
	}
	
	public void setCost(){
		// A get operation should be executed before every put and delete operation in order to perform
		// an incremental update on the aggregation view.
		cost.setGetQuantity(input.getPutOperations().length + input.getDeleteOperations().length);
		// Quantity of put in the internal execution is quantity of insert plus quantity of not zero
		// update plus not zero delete.
		double insertQuantity = input.getPutOperations().length * (1 - updateRatio);
		double notZeroUpdateQuantity = input.getPutOperations().length * updateRatio * notZeroUpdateRatio;
		double notZeroDeleteQuantity = input.getDeleteOperations().length * notZeroDeleteRatio;
		double totalPutQuantity = insertQuantity + notZeroUpdateQuantity + notZeroDeleteQuantity;
		cost.setPutQuantity(totalPutQuantity);
		// Quantity of delete in the internal execution is quantity of zero delete plus zero update.
		double zeroUpdateQuantity = input.getPutOperations().length * updateRatio * (1 - notZeroUpdateRatio);
		double zeroDeleteQuantity = input.getDeleteOperations().length * (1 - notZeroDeleteRatio);
		double totalDeleteQuantity = zeroUpdateQuantity + zeroDeleteQuantity;
		cost.setDeleteQuantity(totalDeleteQuantity);
		// Quantity of forward equals to size of input operations.
		cost.setForwardQuantity(input.getPutOperations().length + input.getDeleteOperations().length);
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
