package DummyData;

import Stream.DeltaStream;
import Stream.RawStream;
import Stream.Stream;

public class DummyInputStream {
	private Stream stream;

	public DummyInputStream (int number, String type){
		
		if (type == "Raw") {
			stream = new RawStream();
		} else if (type == "Delta") {
			stream = new DeltaStream();
		}
		String[] operations = new String[number];
//		operations[0] = "a";
//		operations[1] = "b";
//		operations[2] = "c";
//		operations[3] = "d";
//		operations[4] = "e";
		stream.setPutOperations(operations);
		stream.setDeleteOperations(operations);
	}
	
	public Stream getStream() {
		return stream;
	}

	public void setStream(Stream stream) {
		this.stream = stream;
	}
	
}
