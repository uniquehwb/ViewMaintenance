package ViewModels;

import CostModel.Cost;
import Stream.Stream;

public class JoinOperation implements Operation{
	private String keyWords = "JOIN";
	private String expression;
	// Default materialized
	private boolean pipelined = false;
	// Input stream is decided by output stream of last operation, including type and transactions
	private Stream input = null;
	// Output stream is decided by current operation, including type and transactions
	private Stream output = null;
	// For join there are get cost, put cost, delete cost and forward cost
	private Cost cost = new Cost();
	
	@Override
	public String getKeyWords() {
		// TODO Auto-generated method stub
		return keyWords;
	}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return expression;
	}

	@Override
	public Cost getCost() {
		// TODO Auto-generated method stub
		return cost;
	}

	@Override
	public void setExpression(String expression) {
		// TODO Auto-generated method stub
		this.expression = expression;
	}

	@Override
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
	
	public void setCost(){
		// A get operation should be executed before every put and delete operation in order to perform
		// an incremental update on the aggregation view.
		cost.setGetQuantity(input.getPutOperations().length + input.getDeleteOperations().length);
		// Quantity of forward equals to size of input operations.
		cost.setForwardQuantity(input.getPutOperations().length + input.getDeleteOperations().length);
	}

	@Override
	public void setOutput(Stream output) {
		// TODO Auto-generated method stub
		this.output = output;
	}

	@Override
	public Stream getOutput() {
		// TODO Auto-generated method stub
		return output;
	}
	
	public void setPipelined(boolean pipelined) {
		if (input.getType() == "delta") {
			this.pipelined = pipelined;
		} else {
			this.pipelined = false;
		}
	}
	
	@Override
	public void updateHBase() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propagateUpdate() {
		// TODO Auto-generated method stub
		
	}

}
