package ViewModels;

import Stream.Stream;
import CostModel.Cost;

public interface Operation {
	
	String getKeyWords();
	String getExpression();
	Cost getCost();
	void setKeyWords(String string);
	void setExpression(String string);
	void setInput(Stream input);
	void setOutput(Stream output);
	public Stream getOutput();
	void updateHBase();
	void propagateUpdate();

}
