package SingleIntersectSolve;

import java.util.ArrayList;
import java.util.HashMap;

import Coord.Coord2D;
import Coord.CoordWithRotationAndIndex;
//import Cuboid.SymmetryResolver.SymmetryResolver;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmallIntersectSolutions;
import SolutionResolver.StandardResolverUsingMemory;
import GraphUtils.PivotCellDescription;
import Model.CuboidLidToFoldOn;
import Model.Utils;

public class DFSIntersectFinder2 {

	
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_NEIGHBOURS = NUM_ROTATIONS;
	
	
	public static void solveCuboidIntersections(CuboidLidToFoldOn cuboidToWrap, CuboidLidToFoldOn cuboidToBringAlong) {
		solveCuboidIntersections(cuboidToWrap, cuboidToBringAlong, true);
	}
	
	public static void solveCuboidIntersections(CuboidLidToFoldOn cuboidToBuild, CuboidLidToFoldOn cuboidToBringAlong, boolean skipSymmetries) {
		SolutionResolverInterface solutionResolver = null;
		
		
		if(Utils.getTotalAreaLid(cuboidToBuild.getDimensions()) != Utils.getTotalAreaLid(cuboidToBringAlong.getDimensions())) {
			System.out.println("ERROR: The two cuboid to intersect don't have the same area.");
			System.exit(1);
		}
		
		// Set the solution resolver to different things depending on the size of the cuboid:
		
		if(Utils.cuboidDimensionsMatch(cuboidToBuild, cuboidToBringAlong)) {
			//solutionResolver = new StandardResolverUsingMemory();
			solutionResolver = new StandardResolverForSmallIntersectSolutions();
		} else {
			solutionResolver = new StandardResolverForSmallIntersectSolutions();
		}
		
		
		solveCuboidIntersections(cuboidToBuild, cuboidToBringAlong, skipSymmetries, solutionResolver);
	}

	public static void solveCuboidIntersections(CuboidLidToFoldOn cuboidToBuild, CuboidLidToFoldOn cuboidToBringAlong, boolean skipSymmetries, SolutionResolverInterface solutionResolver) {
		
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
		//cube.set start location 0 and rotation 0
		

		//TODO: LATER use hashes to help.. (record potential expansions, and no-nos...)
		Coord2D paperToDevelop[] = new Coord2D[Utils.getTotalAreaLid(cuboidToBuild.getDimensions())];
		for(int i=0; i<paperToDevelop.length; i++) {
			paperToDevelop[i] = null;
		}
		
		int GRID_SIZE = 2*Utils.getTotalArea(cuboidToBuild.getDimensions());
	
		boolean paperUsed[][] = new boolean[GRID_SIZE][GRID_SIZE];
		int indexCuboidOnPaper[][] = new int[GRID_SIZE][GRID_SIZE];

		int indexCuboidOnPaper2ndCuboid[][] = new int[GRID_SIZE][GRID_SIZE];
		
		for(int i=0; i<paperUsed.length; i++) {
			for(int j=0; j<paperUsed[0].length; j++) {
				paperUsed[i][j] = false;
				indexCuboidOnPaper[i][j] = -1;
				indexCuboidOnPaper2ndCuboid[i][j] = -1;
			}
		}

		//Default start location GRID_SIZE / 2, GRID_SIZE / 2
		int START_I = GRID_SIZE/2;
		int START_J = GRID_SIZE/2;
		
		CuboidLidToFoldOn cuboid = new CuboidLidToFoldOn(cuboidToBuild);
		//Insert start cell:
		
		//Once this reaches the total area, we're done!
		int numCellsUsedDepth = 0;

		int START_INDEX = 0;
		int START_ROTATION = 0;
		paperUsed[START_I][START_J] = true;
		paperToDevelop[numCellsUsedDepth] = new Coord2D(START_I, START_J);
		
		cuboid.setCell(START_INDEX, START_ROTATION);
		indexCuboidOnPaper[START_I][START_J] = START_INDEX;
		numCellsUsedDepth += 1;
		
		
		//TODO: Later try intersecting with all of them at once, so it's easier to get distinct solutions,
		// and maybe it's faster?

		//TODO: 2nd one
		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescription.getUniqueRotationListsWithCellInfo(cuboidToBringAlong);
		
		System.out.println("Num starting points and rotations to check: " + startingPointsAndRotationsToCheck.size());
		
		//(Set i=1 for non-trial Nx1x1 self-intersections (This is just a side-problem))
		//for(int i=1; i<startingPointsAndRotationsToCheck.size(); i++) {
		for(int i=0; i<startingPointsAndRotationsToCheck.size(); i++) {
			//if(i != 0 && i != 4 && i != 6 && i != 10 && i != 14) {
			//	continue;
			//}
		//for(int i=0; i<1; i++) {
		
			int startIndex2ndCuboid =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int startRotation2ndCuboid = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			CuboidLidToFoldOn cuboidToBringAlongStartRot = new CuboidLidToFoldOn(cuboidToBringAlong);

			cuboidToBringAlongStartRot.setCell(startIndex2ndCuboid, startRotation2ndCuboid);
			indexCuboidOnPaper2ndCuboid[START_I][START_J] = startIndex2ndCuboid;
			
			int topBottombridgeUsedNx1x1[] = new int[Utils.getTotalArea(cuboidToBuild.getDimensions())];
			
			long debugIterations[] = new long[Utils.getTotalAreaLid(cuboidToBuild.getDimensions())];
			
			HashMap<Integer, Integer> CellIndexToOrderOfDev = new HashMap <Integer, Integer>();
			CellIndexToOrderOfDev.put(0, 0);
		
			doDepthFirstSearch(paperToDevelop, indexCuboidOnPaper, paperUsed, cuboid, numCellsUsedDepth, -1L, skipSymmetries, solutionResolver, cuboidToBringAlongStartRot, indexCuboidOnPaper2ndCuboid, topBottombridgeUsedNx1x1, false, debugIterations, CellIndexToOrderOfDev, 0, 0);
			
			
			System.out.println("Done with trying to intersect 2nd cuboid that has a start index of " + startIndex2ndCuboid + " and a rotation index of " + startRotation2ndCuboid +".");
			System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		}
		
		//TODO: end todo 2nd one
		
		
		System.out.println("Final number of unique solutions: " + solutionResolver.getNumUniqueFound());
		
		System.out.println("Number of iterations: " + numIterations);
	}
	
	
	public static final int nugdeBasedOnRotation[][] = {{-1, 0, 1, 0}, {0, 1, 0 , -1}};
	public static long numIterations = 0;
	
	public static long doDepthFirstSearch(Coord2D paperToDevelop[], int indexCuboidonPaper[][], boolean paperUsed[][], CuboidLidToFoldOn cuboid, int numCellsUsedDepth,
			long limitDupSolutions, boolean skipSymmetries, SolutionResolverInterface solutionResolver, CuboidLidToFoldOn cuboidToBringAlongStartRot, int indexCuboidOnPaper2ndCuboid[][],
			int topBottombridgeUsedNx1x1[],
			boolean debugNope, long debugIterations[],
			HashMap <Integer, Integer> CellIndexToOrderOfDev, int minIndexToUse, int minRotationToUse) {

		numIterations++;
		if(numCellsUsedDepth == Utils.getTotalAreaLid(cuboid.getDimensions())) {

			int indexes[][][] = new int[2][][];
			indexes[0] = indexCuboidonPaper;
			indexes[1] = indexCuboidOnPaper2ndCuboid;
			long tmp = solutionResolver.resolveSolution(cuboid, paperToDevelop, indexes, paperUsed);

			/*
			if(debugNope) {
				System.out.println("STOP!");
				System.out.println(numIterations);
				for(int i=0; i<numCellsUsedDepth; i++) {
					System.out.println("Iteration: " + debugIterations[i]);
				}
				System.exit(1);
			}
			*/
			
			return tmp;
		}

		//System.out.println(numIterations);
		//Utils.printFoldWithIndex(indexCuboidonPaper);
		
		//Display debug/what's-going-on update
		
		//if(numIterations % 10000000L == 0) {
		if(numIterations % 100000000L == 0) {
			
			System.out.println("Num iterations: " + numIterations);
			Utils.printFold(paperUsed);
			Utils.printFoldWithIndex(indexCuboidonPaper);
			Utils.printFoldWithIndex(indexCuboidOnPaper2ndCuboid);
			
			System.out.println("Solutions: " + solutionResolver.getNumUniqueFound());
			System.out.println();
			
			System.out.println("Last cell inserted: " + indexCuboidonPaper[paperToDevelop[numCellsUsedDepth - 1].i][paperToDevelop[numCellsUsedDepth - 1].j]);
			
			
		}
		//End display debug/what's-going-on update
		
		long retDuplicateSolutions = 0L;
		

		debugIterations[numCellsUsedDepth] = numIterations;
		
		//DEPTH-FIRST START:
		for(int curOrderedIndexToUse=minIndexToUse; curOrderedIndexToUse<numCellsUsedDepth && curOrderedIndexToUse<paperToDevelop.length && paperToDevelop[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {
			
			int indexToUse = indexCuboidonPaper[paperToDevelop[curOrderedIndexToUse].i][paperToDevelop[curOrderedIndexToUse].j];
			
			 /*if(SymmetryResolver.skipSearchBecauseOfASymmetryArgDontCareAboutRotation
					(cuboid, paperToDevelop, indexCuboidonPaper, curOrderedIndexToUse, indexToUse)
				&& skipSymmetries) {
				continue;

				
			} else if( 2*numCellsUsedDepth < paperToDevelop.length && SymmetryResolver.skipSearchBecauseCuboidCouldProvablyNotBeBuiltThisWay
					(cuboid, paperToDevelop, indexCuboidonPaper, curOrderedIndexToUse, indexToUse, topBottombridgeUsedNx1x1, CellIndexToOrderOfDev) && skipSymmetries
					) {
				
				break;
				
				//Maybe put this right after the contains key if condition? (regions[regionIndex].getCellIndexToOrderOfDev().containsKey(indexToUse))
			}*/

			
			CoordWithRotationAndIndex neighbours[] = cuboid.getNeighbours(indexToUse);
			
			int curRotation = cuboid.getRotationPaperRelativeToMap(indexToUse);
			
			int indexToUse2 = indexCuboidOnPaper2ndCuboid[paperToDevelop[curOrderedIndexToUse].i][paperToDevelop[curOrderedIndexToUse].j];
			int curRotationCuboid2 = cuboidToBringAlongStartRot.getRotationPaperRelativeToMap(indexToUse2);
			
			//Try to attach a cell onto indexToUse using all 4 rotations:
			for(int dirNewCellAdd=0; dirNewCellAdd<NUM_ROTATIONS; dirNewCellAdd++) {
				
				int neighbourArrayIndex = (dirNewCellAdd - curRotation + NUM_ROTATIONS) % NUM_ROTATIONS;
				
				if(neighbours[neighbourArrayIndex] == null
						|| cuboid.isCellIndexUsed(neighbours[neighbourArrayIndex].getIndex())) {
					
					//Don't reuse a used cell:
					continue;
					
				} else if(CellIndexToOrderOfDev.containsKey(indexToUse) &&
						CellIndexToOrderOfDev.get(indexToUse) == minIndexToUse
						&& dirNewCellAdd <  minRotationToUse) {
					continue;
				}

				int neighbourIndexCuboid2 = (dirNewCellAdd - curRotationCuboid2 + NUM_ROTATIONS) % NUM_ROTATIONS;


				if(cuboidToBringAlongStartRot.getNeighbours(indexToUse2)[neighbourIndexCuboid2] == null) {
					//went off the lid for the 2nd cuboid:
					continue;
				}
				
				int indexNewCell2 = cuboidToBringAlongStartRot.getNeighbours(indexToUse2)[neighbourIndexCuboid2].getIndex();
				
				if(cuboidToBringAlongStartRot.isCellIndexUsed(indexNewCell2)) {
					//no good!
					continue;
				}
				
				
				int new_i = paperToDevelop[curOrderedIndexToUse].i + nugdeBasedOnRotation[0][dirNewCellAdd];
				int new_j = paperToDevelop[curOrderedIndexToUse].j + nugdeBasedOnRotation[1][dirNewCellAdd];

				int indexNewCell = neighbours[neighbourArrayIndex].getIndex();
				
				
				if(paperUsed[new_i][new_j]) {
					//Cell we are considering to add is already there...
					continue;
				}
				
				
				int rotationNeighbourPaperRelativeToMap = (curRotation - neighbours[neighbourArrayIndex].getRot() + NUM_ROTATIONS) % NUM_ROTATIONS;
				int rotationNeighbourPaperRelativeToMap2 = (curRotationCuboid2 - cuboidToBringAlongStartRot.getNeighbours(indexToUse2)[neighbourIndexCuboid2].getRot() + NUM_ROTATIONS)  % NUM_ROTATIONS;
				
				/*if(SymmetryResolver.skipSearchBecauseOfASymmetryArg
						(cuboid, paperToDevelop, curOrderedIndexToUse, indexCuboidonPaper, dirNewCellAdd, curRotation, paperUsed, indexToUse, indexNewCell)
					&& skipSymmetries == true) {
					continue;
				}*/
				
				boolean cantAddCellBecauseOfOtherPaperNeighbours = //cantAddCellBecauseOfOtherPaperNeighbours(paperToDevelop, indexCuboidonPaper,
						ALLOW_HOLES_cantAddCellBecauseOfOtherPaperNeighbours(paperToDevelop, indexCuboidonPaper,
						paperUsed, cuboid, numCellsUsedDepth,
						CellIndexToOrderOfDev, minIndexToUse, minRotationToUse,
						indexNewCell, new_i, new_j, curOrderedIndexToUse
					);
				
				
				if( ! cantAddCellBecauseOfOtherPaperNeighbours) {
					
					//Setup for adding new cell:
					cuboid.setCell(indexNewCell, rotationNeighbourPaperRelativeToMap);
					cuboidToBringAlongStartRot.setCell(indexNewCell2, rotationNeighbourPaperRelativeToMap2);
					
					paperUsed[new_i][new_j] = true;
					indexCuboidonPaper[new_i][new_j] = indexNewCell;
					paperToDevelop[numCellsUsedDepth] = new Coord2D(new_i, new_j);

					if(indexToUse == 0) {
						topBottombridgeUsedNx1x1[indexNewCell] = dirNewCellAdd;
					} else if(indexToUse == cuboid.getCellsUsed().length - 1) {
						topBottombridgeUsedNx1x1[indexNewCell] = NUM_ROTATIONS + dirNewCellAdd;
					} else {
						topBottombridgeUsedNx1x1[indexNewCell] = topBottombridgeUsedNx1x1[indexToUse];
					}

					indexCuboidOnPaper2ndCuboid[new_i][new_j] = indexNewCell2;

					
					//End setup

					long newLimitDupSolutions = limitDupSolutions;
					if(limitDupSolutions >= 0) {
						newLimitDupSolutions -= retDuplicateSolutions;
					}
					
					CellIndexToOrderOfDev.put(indexNewCell, numCellsUsedDepth);
					numCellsUsedDepth += 1;
					
					retDuplicateSolutions += doDepthFirstSearch(paperToDevelop, indexCuboidonPaper, paperUsed, cuboid, numCellsUsedDepth, newLimitDupSolutions, skipSymmetries, solutionResolver, cuboidToBringAlongStartRot, indexCuboidOnPaper2ndCuboid, topBottombridgeUsedNx1x1, debugNope, debugIterations, CellIndexToOrderOfDev, curOrderedIndexToUse, dirNewCellAdd);

					//Tear down (undo add of new cell)
					numCellsUsedDepth -= 1;

					CellIndexToOrderOfDev.remove(indexNewCell);
					
					

					
					paperUsed[new_i][new_j] = false;
					indexCuboidonPaper[new_i][new_j] = -1;
					paperToDevelop[numCellsUsedDepth] = null;

					indexCuboidOnPaper2ndCuboid[new_i][new_j] = -1;
					
					cuboid.removeCell(indexNewCell);
					cuboidToBringAlongStartRot.removeCell(indexNewCell2);
					
					//End tear down


				} // End recursive if cond
			} // End loop rotation
		} //End loop index

		return retDuplicateSolutions;
	}
	
	public static final int ONE_EIGHTY_ROTATION = 2;
	
	public static boolean cantAddCellBecauseOfOtherPaperNeighbours(Coord2D paperToDevelop[], int indexCuboidonPaper[][],
			boolean paperUsed[][], CuboidLidToFoldOn cuboid, int numCellsUsedDepth,
			HashMap <Integer, Integer> CellIndexToOrderOfDev, int minIndexToUse, int minRotationToUse,
			int indexNewCell, int new_i, int new_j, int curOrderedIndexToUse
		) {	
	boolean cantAddCellBecauseOfOtherPaperNeighbours = false;
	
	int neighboursBasedOnRotation[][] = {{new_i-1, new_j}, {new_i, new_j+1},{new_i+1, new_j},{new_i, new_j - 1}};

	
	for(int rotReq=0; rotReq<neighboursBasedOnRotation.length; rotReq++) {
		
		int i1 = neighboursBasedOnRotation[rotReq][0];
		int j1 = neighboursBasedOnRotation[rotReq][1];
	
		if(paperToDevelop[curOrderedIndexToUse].i == i1 && paperToDevelop[curOrderedIndexToUse].j == j1) {
			continue;
		}
		
		//System.out.println("Paper neighbour:" + i1 + ", " + j1);
		
		if(paperUsed[i1][j1]) {
			//System.out.println("Connected to another paper");
			
			int indexOtherCell = indexCuboidonPaper[i1][j1];
			int rotationOtherCell = cuboid.getRotationPaperRelativeToMap(indexOtherCell);

			if(CellIndexToOrderOfDev.containsKey(indexOtherCell)
					&& CellIndexToOrderOfDev.get(indexOtherCell) < curOrderedIndexToUse ) {
				cantAddCellBecauseOfOtherPaperNeighbours = true;
				break;
			}
			
			//There's a 180 rotation because the neighbour is attaching to the new cell (so it's flipped!)
			int neighbourIndexNeeded = (rotReq + ONE_EIGHTY_ROTATION - rotationOtherCell+ NUM_ROTATIONS) % NUM_ROTATIONS;


			if(cuboid.getNeighbours(indexOtherCell)[neighbourIndexNeeded].getIndex() != indexNewCell) {
				cantAddCellBecauseOfOtherPaperNeighbours = true;
				break;
			}
		}
	}
	return cantAddCellBecauseOfOtherPaperNeighbours;
}
	 
	public static boolean ALLOW_HOLES_cantAddCellBecauseOfOtherPaperNeighbours(Coord2D paperToDevelop[], int indexCuboidonPaper[][],
			boolean paperUsed[][], CuboidLidToFoldOn cuboid, int numCellsUsedDepth,
			HashMap <Integer, Integer> CellIndexToOrderOfDev, int minIndexToUse, int minRotationToUse,
			int indexNewCell, int new_i, int new_j, int curOrderedIndexToUse
		) {	
	boolean cantAddCellBecauseOfOtherPaperNeighbours = false;
	
	int neighboursBasedOnRotation[][] = {{new_i-1, new_j}, {new_i, new_j+1},{new_i+1, new_j},{new_i, new_j - 1}};

	
	for(int rotReq=0; rotReq<neighboursBasedOnRotation.length; rotReq++) {
		
		int i1 = neighboursBasedOnRotation[rotReq][0];
		int j1 = neighboursBasedOnRotation[rotReq][1];
	
		if(paperToDevelop[curOrderedIndexToUse].i == i1 && paperToDevelop[curOrderedIndexToUse].j == j1) {
			continue;
		}
		
		//System.out.println("Paper neighbour:" + i1 + ", " + j1);
		
		if(paperUsed[i1][j1]) {
			//System.out.println("Connected to another paper");
			
			int indexOtherCell = indexCuboidonPaper[i1][j1];
			int rotationOtherCell = cuboid.getRotationPaperRelativeToMap(indexOtherCell);
			
			//There's a 180 rotation because the neighbour is attaching to the new cell (so it's flipped!)
			int neighbourIndexNeeded = (rotReq + ONE_EIGHTY_ROTATION - rotationOtherCell+ NUM_ROTATIONS) % NUM_ROTATIONS;

			if(cuboid.getNeighbours(indexOtherCell)[neighbourIndexNeeded] == null) {
				continue;
			}
			
			if(cuboid.getNeighbours(indexOtherCell)[neighbourIndexNeeded].getIndex() != indexNewCell) {
				//In this case, there's an implied hole...
				// I want to see what happens when we allow this...
				continue;
				
			} else if(CellIndexToOrderOfDev.containsKey(indexOtherCell)
					&& CellIndexToOrderOfDev.get(indexOtherCell) < curOrderedIndexToUse ) {
				cantAddCellBecauseOfOtherPaperNeighbours = true;
				break;
			}
			
		

			
		}
	}
	return cantAddCellBecauseOfOtherPaperNeighbours;
}
	/*https://www.sciencedirect.com/science/article/pii/S0925772117300160
	 * 
	 *  "From the necessary condition, the smallest possible surface area that can fold into two boxes is 22,
	 *   and the smallest possible surface area for three different boxes is 46.
	 *   (...) However, the area 46 is too huge to search. "
	 *  
	 *  Challenge accepted!
	 */

	public static void main(String args[]) {
		System.out.println("Fold Resolver Ordered Regions intersection skip symmetries Nx1x1:");

		
		solveCuboidIntersections(new CuboidLidToFoldOn(1, 7, 5), new CuboidLidToFoldOn(2, 11, 1));
		//solveCuboidIntersections(new CuboidLidToFoldOn(1, 1, 1), new CuboidLidToFoldOn(1, 1, 1));
		
		
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
	}
	/*
54	1 � 1 � 13, 1 � 3 � 6, 3 � 3 � 3
58	1 � 1 � 14, 1 � 2 � 9, 1 � 4 � 5
62	1 � 1 � 15, 1 � 3 � 7, 2 � 3 � 5
64	1 � 2 � 10, 2 � 2 � 7, 2 � 4 � 4
70	1 � 1 � 17, 1 � 2 � 11, 1 � 3 � 8, 1 � 5 � 5
88	1 � 2 � 14, 1 � 4 � 8, 2 � 2 � 10, 2 � 4 � 6
*/

	
}
