package Tree;

import java.util.HashMap;
import java.util.List;

class PathMap {
	HashMap<Tree.Node, List<Tree.Node> > pathMap;

	public List<Tree.Node> getPathFromRoot(Tree.Node n) {
		List<Tree.Node> pathFromRoot = pathMap.get(n);
		return pathFromRoot;
	}
}
