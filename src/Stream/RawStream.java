package Stream;

public class RawStream implements Stream{
	private String type = "Raw";
	// raw stream has both put operations and delete operations
	private String[] putOperations;
	private String[] deleteOperations;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String[] getPutOperations() {
		return putOperations;
	}

	@Override
	public void setPutOperations(String[] putOperations) {
		this.putOperations = putOperations;
	}
	
	@Override
	public String[] getDeleteOperations() {
		return deleteOperations;
	}
	
	@Override
	public void setDeleteOperations(String[] deleteOperations) {
		this.deleteOperations = deleteOperations;
	}
}
