package SolutionResolver;

import Coord.Coord2D;

public interface SolutionResolverInterface {

	
	public long resolveSolution(Model.CuboidLidToFoldOn cuboidDimensionsAndNeighbours, Coord2D paperToDevelop[], int indexCuboidonPaper[][][], boolean paperUsed[][]);
	
	
	public long getNumUniqueFound();
}
