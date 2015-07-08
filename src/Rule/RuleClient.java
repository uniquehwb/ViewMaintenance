package Rule;

import java.util.List;

import ViewModels.Operation;

public class RuleClient {
	
	private List<Operation> operationList;
	
	public RuleClient(List<Operation> operationList) {
		this.operationList = operationList;
	}
	
	public boolean checkAllRules() {
		boolean allRulesSatisfied = false;
		if (checkSelectionAggregationOrder() && checkDeltaAggregationOrder()) {
			allRulesSatisfied = true;
		}
		return allRulesSatisfied;
	}
	
	// The selection operation should be executed early than aggregation view.
	public boolean checkSelectionAggregationOrder() {
		boolean containSelection = false;
		boolean containAggregation = false;
		int selectionIndex = 0;
		int aggregationIndex = 0;
		for (int i = 0; i < operationList.size(); i++) {
			if (operationList.get(i).getKeyWords() == "SELECTION") {
				containSelection = true;
				selectionIndex = i;
			} else if (operationList.get(i).getKeyWords() == "AGGREGATION") {
				containAggregation = true;
				aggregationIndex = i;
			}
		}
		// If both operations are contained
		if (containSelection && containAggregation) {
			if (selectionIndex < aggregationIndex) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	// The delta operation should be exactly before the aggregation view.
	public boolean checkDeltaAggregationOrder() {
		boolean correctOrder = false;
		for (int i = 0; i < operationList.size(); i++) {
			if (operationList.get(i).getKeyWords() == "AGGREGATION") {
				if (operationList.get(i - 1).getKeyWords() == "DELTA") {
					correctOrder = true;
				}
			}
		}
		return correctOrder;
	}
}
