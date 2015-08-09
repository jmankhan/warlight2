package map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.la4j.matrix.dense.Basic2DMatrix;

import bot.BotState;
import debug.Log;

public class MapMatrix {

	private Map fullMap;
	private BotState state;

	/**
	 * Create a map based on the string provided Expected format:
	 * region;owner;armies Runs on separate Thread
	 * 
	 * @param mapString
	 */
	public MapMatrix(BotState state, Map full) {
		this.state = state;
		this.fullMap = full;

		Runnable runner = new Runnable() {

			@Override
			public void run() {
				int regions = fullMap.getRegions().size();

				int[][] adjMatrix = createAdjacencyMatrix(regions);
				int[][] matrix = createSuperAdjacencyMatrix(adjMatrix);
				int[] degrees = createDegreeMatrix(adjMatrix);
				int[][] lapMatrix = createLaplacianMatrix(degrees, adjMatrix);
				int[][] sLapMatrix = shiftLaplacianMatrix(lapMatrix);

				// this is the number of spanning trees
				// *or* the amount of paths possible
				int det = determinant(sLapMatrix);
				Log.log("determinant: " + det);
			}

		};
		runner.run();
	}

	/**
	 * Creates a matrix where array[x][y] = has_connection 1 = has_connection 0
	 * = no connection Not using booleans because we use this for math
	 * 
	 * @param regions
	 *            amount of regions
	 * @return int[x][y] where x and y are tile numbers
	 */
	public int[][] createAdjacencyMatrix(int regions) {

		int[][] adjMatrix = new int[regions][regions];

		// 1 signifies a connection
		int value = 1;

		// make an adjacency matrix
		for (Region r : fullMap.getRegions()) {
			// regions start at 1, we start at 0
			int x = r.getId() - 1;

			// fill in 1 for connections
			for (Region n : r.getNeighbors()) {
				int y = n.getId() - 1;
				adjMatrix[x][y] = value;
			}

		}

		printMatrix(adjMatrix, "adjacency_matrix");

		return adjMatrix;
	}

	public int[][] createSuperAdjacencyMatrix(int[][] adjMatrix) {

		int nSupers = fullMap.getSuperRegions().size();

		int[][] superRegions = new int[nSupers][];
		for(int superIndex=0; superIndex<nSupers; superIndex++) {
			//initialize an array to hold each subregion of a super region 
			int numRegions = fullMap.getSuperRegions().get(superIndex).getSubRegions().size();
			int[] region = new int[numRegions];
			
			//populate each sub region array with the id of each region
			int count = 0;
			while(count < numRegions) {
				region[count] = fullMap.getSuperRegions().get(superIndex).getSubRegions().get(count).getId();
				count++;
			}
			superRegions[superIndex] = region;
		}
		
		int[][] sums = new int[superRegions.length][superRegions.length];

		for (int sup1 = 0; sup1 < superRegions.length - 1; sup1++) { //first super region being compared
			for (int sup2 = sup1+1; sup2 < superRegions.length; sup2++) { //second super region being compared
				for (int i = 0; i < superRegions[sup1].length; i++) { //i counts regions in super region 1
					for (int j = 0; j < superRegions[sup2].length; j++) { //j counts regions in super region 2
						int row = superRegions[sup1][i];
						int col = superRegions[sup2][j];
						sums[sup1][sup2] += adjMatrix[row-1][col-1];
						sums[sup2][sup1] += adjMatrix[row-1][col-1];
					}
				}
			}
		}

		printMatrix(sums, "super_adjacency_matrix");
		printMatrix(superRegions, "super_region_list");
		
		return null;
	}

	/**
	 * lapMatrix = degrees - adjMatrix
	 * 
	 * @param degrees
	 *            = assume 0 for non-diagonal values, so only one array is
	 *            needed
	 * @param adjMatrix
	 *            = assumed to be a square matrix
	 */
	public int[][] createLaplacianMatrix(int[] degrees, int[][] adjMatrix) {
		// assume square matrix
		int regions = adjMatrix[0].length;
		int[][] lapMatrix = new int[regions][regions];

		for (int y = 0; y < regions; y++) {
			for (int x = 0; x < regions; x++) {
				if (x == y) {
					lapMatrix[x][y] = degrees[x] - adjMatrix[x][y];
				} else {
					lapMatrix[x][y] = -adjMatrix[x][y];
				}
			}
		}

		printMatrix(lapMatrix, "laplacian_matrix");
		return lapMatrix;
	}

	/**
	 * creates a 1-d array of super_region[n] = amount of regions contained
	 * 
	 * @return int[] supers
	 */
	public int[] createSuperRegions() {
		int[] supers = new int[fullMap.getSuperRegions().size()];
		printArray(supers, "super_regions");
		return supers;
	}

	/**
	 * Calculates how many connections, or neighbors, all the regions have 1-d
	 * array because we can assume no connection (or 0) for all other values
	 * 
	 * array[i] = Sum all rows from adjMatrix Complexity: O(n^2) n=tiles
	 * 
	 * @param regions
	 *            amount of regions
	 * @return int[] degrees where degree[n] = amount of connections
	 */
	public int[] createDegreeMatrix(int[][] adjMatrix) {

		int[] degrees = new int[adjMatrix[0].length];

		int sum = 0;

		for (int y = 0; y < adjMatrix[0].length; y++) {
			for (int x = 0; x < adjMatrix[0].length; x++) {
				sum += adjMatrix[x][y];
			}
			degrees[y] = sum;
			sum = 0;
		}

		printArray(degrees, "degrees");
		return degrees;
	}

	/**
	 * Apparently you have to delete any n row and n column before taking the
	 * determinant I will just remove the last row and column
	 * 
	 * @param lapMatrix
	 *            int[n][n] the Laplacian Matrix to shift
	 * @return int[n-1][n-1] the shifted matrix
	 */
	public int[][] shiftLaplacianMatrix(int[][] lapMatrix) {

		// create an array one less in each dimension
		int size = lapMatrix[0].length - 1;
		int[][] shiftedMatrix = new int[size][size];

		// fill it up all the way, so the last row/column in lapMatrix is left
		// out
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				shiftedMatrix[x][y] = lapMatrix[x][y];
			}
		}

		printMatrix(shiftedMatrix, "shifted_laplacian_matrix");
		return shiftedMatrix;
	}

	/**
	 * Print a 1 dimensional array to '../warlight2-engine/matrix/name.txt'
	 * 
	 * @param array
	 * @param name
	 */
	public void printArray(int[] array, String name) {
		File file = new File("/home/jalal/workspace/warlight2-engine/matrix/"
				+ name + ".txt");
		try {
			if (file.exists())
				file.delete();

			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String out = "";
		for (int i : array) {
			out += i + System.lineSeparator();
		}
		Log.log(file, out);
	}

	/**
	 * Prints to ../warlight2-engine/matrix/name.txt overwriting any previous
	 * file format: 'value, ' - one space after comma
	 * 
	 * @param matrix
	 */
	public void printMatrix(int[][] matrix, String name) {
		File file = new File("/home/jalal/workspace/warlight2-engine/matrix/"
				+ name + ".txt");
		try {
			if (file.exists())
				file.delete();

			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String out = "";
		for (int x[] : matrix) {
			for (int y : x) {
				out += y + ", ";
			}
			out += System.lineSeparator();
		}

		Log.log(file, out);
	}

	public int determinant(int[][] m) {

		// the library didn't use generics
		// cri everytime
		double[][] m2 = new double[m[0].length][m[0].length];

		for (int i = 0; i < m[0].length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				m2[i][j] = m[i][j];
			}
		}

		return (int) Basic2DMatrix.from2DArray(m2).determinant();
	}
}
