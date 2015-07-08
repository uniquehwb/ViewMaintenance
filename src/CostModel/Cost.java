package CostModel;

public class Cost {
	private double getQuantity;
	private double putQuantity;
	private double deleteQuantity;
	private double forwardQuantity;
	
	private double getConsumption;
	private double putConsumption;
	private double deleteConsumption;
	private double forwardConsumption;
	
	public Cost(){
		setGetQuantity(0);
		setPutQuantity(0);
		setDeleteQuantity(0);
		setForwardQuantity(0);
		
		setGetConsumption(8.0);
		setPutConsumption(10.0);
		setDeleteConsumption(5.0);
		setForwardConsumption(3.0);
	}
	
	public double getGetQuantity() {
		return getQuantity;
	}
	public void setGetQuantity(double getQuantity) {
		this.getQuantity = getQuantity;
	}
	public double getPutQuantity() {
		return putQuantity;
	}
	public void setPutQuantity(double putQuantity) {
		this.putQuantity = putQuantity;
	}
	public double getDeleteQuantity() {
		return deleteQuantity;
	}
	public void setDeleteQuantity(double deleteQuantity) {
		this.deleteQuantity = deleteQuantity;
	}
	public double getForwardQuantity() {
		return forwardQuantity;
	}
	public void setForwardQuantity(double totalQuantity) {
		this.forwardQuantity = totalQuantity;
	}
	public double getGetConsumption() {
		return getConsumption;
	}
	public void setGetConsumption(double getConsumption) {
		this.getConsumption = getConsumption;
	}
	public double getPutConsumption() {
		return putConsumption;
	}
	public void setPutConsumption(double putConsumption) {
		this.putConsumption = putConsumption;
	}
	public double getDeleteConsumption() {
		return deleteConsumption;
	}
	public void setDeleteConsumption(double deleteConsumption) {
		this.deleteConsumption = deleteConsumption;
	}
	public double getForwardConsumption() {
		return forwardConsumption;
	}
	public void setForwardConsumption(double forwardConsumption) {
		this.forwardConsumption = forwardConsumption;
	}
	
	public double computeTotalCost(){
		double totalCost = getQuantity * getConsumption + putQuantity * putConsumption + deleteQuantity * deleteConsumption + forwardQuantity * forwardConsumption;
		return totalCost;
	}
}
