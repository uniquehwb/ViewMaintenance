package Rule;

import java.util.List;

import ViewModels.AggregationOperation;
import ViewModels.DeltaOperation;
import ViewModels.JoinOperation;
import ViewModels.Operation;
import ViewModels.SelectionOperation;

public class RuleClient {
	
	private List<Operation> operationList;
	
	public RuleClient(List<Operation> operationList) {
		this.operationList = operationList;
	}
	
	public boolean checkAllRules() {
		boolean allRulesSatisfied = false;
		if (checkSelectionAggregationOrder() && 
				checkDeltaAggregationOrder() &&
				checkJoinAggregationOrder()) {
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
			if (operationList.get(i).getClass().equals(SelectionOperation.class)) {
				containSelection = true;
				selectionIndex = i;
			} else if (operationList.get(i).getClass().equals(AggregationOperation.class)) {
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
			if (operationList.get(i).getClass().equals(AggregationOperation.class)) {
				if (operationList.get(i - 1).getClass().equals(DeltaOperation.class)) {
					correctOrder = true;
				}
			}
		}
		return correctOrder;
	}
	
	// The join operation should be executed early than aggregation view.
	public boolean checkJoinAggregationOrder() {
		boolean containJoin = false;
		boolean containAggregation = false;
		int joinIndex = 0;
		int aggregationIndex = 0;
		for (int i = 0; i < operationList.size(); i++) {
			if (operationList.get(i).getClass().equals(JoinOperation.class)) {
				containJoin = true;
				joinIndex = i;
			} else if (operationList.get(i).getClass().equals(AggregationOperation.class)) {
				containAggregation = true;
				aggregationIndex = i;
			}
		}
		// If both operations are contained
		if (containJoin && containAggregation) {
			if (joinIndex < aggregationIndex) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}
