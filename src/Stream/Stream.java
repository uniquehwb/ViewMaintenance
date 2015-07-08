package Stream;

public interface Stream {

	public String[] getPutOperations();
	public void setPutOperations(String[] putOperations);
	public String[] getDeleteOperations();
	public void setDeleteOperations(String[] deleteOperations);
	public String getType();
	public void setType(String type);
}
