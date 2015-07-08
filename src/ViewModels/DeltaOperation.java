package ViewModels;

import Stream.Stream;
import CostModel.Cost;


public class DeltaOperation implements Operation{
	private String keyWords = "DELTA";
	private String expression;
	// Default materialized
	private boolean pipelined = false;
	// Input stream is usually raw stream
	private Stream input = null;
	// Output stream is usually delta stream
	private Stream output = null;
	// For delta there are get cost, put cost and forward cost
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
		this.pipelined = pipelined;
	}
	public Stream getInput() {
		return input;
	}
	public void setInput(Stream input) {
		this.input = input;
		
		// After input stream is defined, cost model can be initialized.
		this.setCost();
		
		// After input stream is defined, output stream can be initialized as well
		input.setType("Delta");
		// The output stream of the delta view should be delta
		this.setOutput(input);
	}
	public Stream getOutput() {
		return output;
	}
	public void setOutput(Stream output) {
		this.output = output;
	}
	
	public void setCost(){
		// Delta view can't be pipeline.
		// All put and delete operation in the input stream are converted to get and put operations.
		double totalQuantity = input.getPutOperations().length + input.getDeleteOperations().length;
		cost.setGetQuantity(totalQuantity);
		cost.setPutQuantity(totalQuantity);
		cost.setForwardQuantity(totalQuantity);
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
			// operation is pipelined
			if (pipelined) {
				// if condition is meet, then 
				//    1. construct a output transaction according to rules (delta)
				//    2. put it into transactions of output stream
			} 
			// operation is materialized
			else 
			{
				// if condition is meet, then 
				//    1. apply transaction, HBase.put
				//    2. construct a output transaction according to rules (delta)
				//    3. put it into transactions of output stream
			}
		}
	}
	public void propagateUpdate() {
		// set output stream as input stream of next operation
	}
}
