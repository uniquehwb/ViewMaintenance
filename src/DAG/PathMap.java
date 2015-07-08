package DAG;

import java.util.HashMap;
import java.util.List;

class PathMap {
	HashMap<DAG.Node, List<DAG.Node> > pathMap;

	public List<DAG.Node> getPathFromRoot(DAG.Node n) {
		List<DAG.Node> pathFromRoot = pathMap.get(n);
		return pathFromRoot;
	}
}
