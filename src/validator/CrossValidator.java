package validator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Durga Sai Preetham Palagummi
 * @Net ID: DXP141030
 * @UTD ID: 202 120 4112
 * 
 */

/*
 * CrossValidator class 
 */
public class CrossValidator {

	char[][] examples;
	String inputFileOnePath = "D:\\UTD_dxp141030\\Summer_2015\\CS6375_Machine Learning\\Assignments\\Graded_1\\first_file.txt";	// File that contains permutations
	String inputFileTwoPath = "D:\\UTD_dxp141030\\Summer_2015\\CS6375_Machine Learning\\Assignments\\Graded_1\\second_file.txt";	// File that contains the examples
	String outputFilePath = "D:\\UTD_dxp141030\\Summer_2015\\CS6375_Machine Learning\\Assignments\\Graded_1\\dxp141030.txt"; //The output is written to this file

	BufferedWriter output;
	int numberOfFolds;
	int numberOfExamples;
	int numberOfPermutations;
	Map<Integer, Integer[]> exampleMap;
	int[][] permutations;

	/*
	 * Method that is used to read the input from the two files
	 */
	public BufferedReader inputFileReader (String filePath) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return reader;
	}

	/*
	 * Method used to read the parameters present in the file
	 */
	public void readCrossValidationParams() {
		BufferedReader inputFileOne;
		try {
			inputFileOne = inputFileReader(inputFileOnePath);
			String[] crossValidationParams = inputFileOne.readLine().split(" ");

			numberOfFolds = Integer.parseInt(crossValidationParams[0]);
			numberOfExamples = Integer.parseInt(crossValidationParams[1]);
			numberOfPermutations = Integer.parseInt(crossValidationParams[2]);

			permutations = new int[numberOfPermutations][numberOfExamples];

			String inputs = inputFileOne.readLine();
			int i = 0;
			int j = 0;

			while (inputs != null) {
				if (i < numberOfPermutations) {
					String[] inputsArray = inputs.split(" ");
					if (inputsArray.length == numberOfExamples) {
						for (int k = 0; k < inputsArray.length; k++) {
							permutations[i][j++] = Integer.parseInt(inputsArray[k]);
						}
						i++;
						j = 0;
						inputs = inputFileOne.readLine();
					}
					else {
						System.out.println("The permutation combination in line " + (i+2) + " does not match the number of examples in line 1.");
						System.exit(0);
					}
				}
				else {
					System.out.println("The number of permutations is more than given in line 1.");
					System.exit(0);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Method used to read the examples
	 */
	public void readExamples() {
		BufferedReader inputFileTwo;
		try {
			inputFileTwo = inputFileReader(inputFileTwoPath);
			String[] exampleSize = inputFileTwo.readLine().split(" ");

			int x1Size = Integer.parseInt(exampleSize[0]);
			int x2Size = Integer.parseInt(exampleSize[1]);
			examples = new char[x1Size][x2Size];
			exampleMap = new HashMap<Integer, Integer[]>();

			for (char[] row: examples) {
				Arrays.fill(row, '.');
			}

			String inputs = inputFileTwo.readLine();
			int i = 0;
			int j = 0;
			int key = 0;

			while (inputs != null) {
				char[] inputsArray = inputs.replaceAll(" ", "").toCharArray();
				if (i < x1Size) {
					if (inputsArray.length <= x2Size) {
						for (int k = 0; k < inputsArray.length; k++) {
							examples[i][j++] = inputsArray[k];
							if (examples[i][j-1] != '.') {
								Integer[] ex = {i, j-1};
								exampleMap.put(key++, ex);
							}
						}
						i++;
						j = 0;
						inputs = inputFileTwo.readLine();
					}
					else {
						System.out.println("Number of columns is more than given in line 1.");
						System.exit(0);
					}
				}
				else {
					System.out.println("Number of rows is more than given in line 1.");
					System.exit(0);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * The main logic for nearest neighbours is present in this method
	 */
	public void nearest(int i, int j, int nn, char estimatedSamples[][],  char newExamples[][]) {
		int m = 1, n = 0, flag = 0;
		int countPlus = 0;
		int countNegative = 0;
		int totalPlusCount = 0;
		int totalNegativeCount = 0;
		int totalCount = 0;
		int temp = 0;

		while (i+m < newExamples.length || i-m >= 0 || j+m < newExamples[i].length || j-m >= 0) {
			for (n = 0; n <= m; n++) {
				if (i-m >= 0 && j+n < newExamples[i-m].length && newExamples[i-m][j+n] != '.') {
					if (newExamples[i-m][j+n] == '+')
						countPlus++;
					else
						countNegative++;
				}

				if (n != 0 && j-n >= 0 && i-m >= 0 && newExamples[i-m][j-n] != '.') {
					if (newExamples[i-m][j-n] == '+')
						countPlus++;
					else
						countNegative++;
				}

				if (i+m < newExamples.length && j+n < newExamples[i+m].length && newExamples[i+m][j+n] != '.') {
					if (newExamples[i+m][j+n] == '+')
						countPlus++;
					else
						countNegative++;
				}

				if (n != 0 && i+m < newExamples.length && j-n >= 0 && newExamples[i+m][j-n] != '.') {
					if (newExamples[i+m][j-n] == '+')
						countPlus++;
					else
						countNegative++;
				}

				if (m != n) {				
					if (i-n >= 0 && j+m < newExamples[i-n].length && newExamples[i-n][j+m] != '.') {
						if (newExamples[i-n][j+m] == '+')
							countPlus++;
						else
							countNegative++;
					}

					if (i-n >= 0 && j-m >= 0 && newExamples[i-n][j-m] != '.') {
						if (newExamples[i-n][j-m] == '+')
							countPlus++;
						else
							countNegative++;
					}

					if (n != 0 && i+n < newExamples.length && j+m < newExamples[i+n].length && newExamples[i+n][j+m] != '.') {
						if (newExamples[i+n][j+m] == '+')
							countPlus++;
						else
							countNegative++;
					}

					if (n != 0 && i+n < newExamples.length && j-m >= 0 && newExamples[i+n][j-m] != '.') {
						if (newExamples[i+n][j-m] == '+')
							countPlus++;
						else
							countNegative++;
					}
				}

				temp = nn - totalCount;

				if ((totalCount + countPlus + countNegative) >= nn) {
					if (totalCount + countPlus + countNegative == nn) {
						if (totalPlusCount + countPlus > totalNegativeCount + countNegative)
							estimatedSamples[i][j] = '+';
						else
							estimatedSamples[i][j] = '-';
					}
					else {
						if (countNegative != 0) {
							if (temp >= countNegative) {
								if ((totalPlusCount + (temp - countNegative)) > (totalNegativeCount + countNegative))
									estimatedSamples[i][j] = '+';
								else
									estimatedSamples[i][j] = '-';
							}
							else
								if (totalPlusCount > totalNegativeCount + temp)
									estimatedSamples[i][j] = '+';
								else
									estimatedSamples[i][j] = '-';
						}
						else {
							if (totalPlusCount + temp > totalNegativeCount)
								estimatedSamples[i][j] = '+';
							else
								estimatedSamples[i][j] = '-';
						}
					}
					flag = 1;
					break;
				}

				totalPlusCount = totalPlusCount + countPlus;
				totalNegativeCount = totalNegativeCount + countNegative;
				totalCount = totalPlusCount + totalNegativeCount;
				countPlus = 0;
				countNegative = 0;
			}
			if (flag == 1)
				break;
			m++;
		}
	}
	
	/*
	 * Used to write to the output file
	 */
	public void output (int k, double e, double sigma, char estimatedSamples[][]) {
		try {
			String str = "k=" + k + " " + "e=" + e + " " + "sigma=" + sigma + "\n";		
			output.write(str);
			for (int i = 0; i < examples.length; i++) {
				for (int j = 0; j < examples[i].length; j++) {
					output.write(estimatedSamples[i][j] + " ");
				}
				output.write("\n");
			}
			output.write("\n");
			output.flush();
			//output.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/*
	 * Helper method to drive the other functions
	 */
	public void execute() {
		readCrossValidationParams();
		readExamples();

		int i, j, p, k;

		char[][] newExamples = new char[examples.length][];
		char[][] estimatedSamples = new char[examples.length][];

		for (i = 0; i < examples.length; i++) {
			newExamples[i] = Arrays.copyOf(examples[i], examples[i].length);
			estimatedSamples[i] = Arrays.copyOf(examples[i], examples[i].length);
		}

		int folds = numberOfExamples/numberOfFolds;
		int adjustFold = numberOfExamples%numberOfFolds;
		int foldLength = 0;
		double meanError = 0;
		float[] averageError = new float[numberOfPermutations];

		for (k = 1; (k <= 5 ); k++) {
			for (p = 0; p < numberOfPermutations; p++) {
				int foldIncrement = 0;
				int errorCount = 0;

				for (i = 0; i < numberOfFolds; i++) {
					if (adjustFold != 0 && i == (numberOfFolds - adjustFold)) {
						foldIncrement = folds + 1;
						--adjustFold;
					}
					else
						foldIncrement = folds;

					for (j = foldLength; j < foldLength + foldIncrement; j++) {
						Integer[] x = exampleMap.get(permutations[p][j]);
						newExamples[x[0]][x[1]] = '.';
					}

					for (j = foldLength; j < foldLength + foldIncrement; j++) {
						Integer[] x = exampleMap.get(permutations[p][j]);
						nearest(x[0], x[1], k, estimatedSamples, newExamples);
						if (examples[x[0]][x[1]] != estimatedSamples[x[0]][x[1]])
							errorCount++;
					}

					averageError[p] = (float) errorCount/(float)numberOfExamples;

					for (j = 0; j < examples.length; j++) {
						newExamples[j] = Arrays.copyOf(examples[j], examples[j].length);
						estimatedSamples[j] = Arrays.copyOf(examples[j], examples[j].length);
					}

					foldLength = foldIncrement;
				}
				meanError = meanError + averageError[p];
			}

			DecimalFormat df = new DecimalFormat("0.00");
			meanError = Double.parseDouble(df.format(meanError/(double)numberOfPermutations));
			double V = 0;
			double sigma = 0;

			for (p = 0; p < numberOfPermutations; p++) {
				V = V + Math.pow((averageError[p] - meanError),2);
			}

			V = V/(numberOfPermutations-1);
			sigma = Double.parseDouble(df.format(Math.sqrt(V)));

			for (i = 0; i < examples.length; i++) {
				for (j = 0; j < examples[i].length; j++) {
					if (newExamples[i][j] == '.')
						nearest(i, j, k, estimatedSamples, newExamples);
				}
			}
			output(k, meanError, sigma, estimatedSamples);

			for (j = 0; j < examples.length; j++) {
				newExamples[j] = Arrays.copyOf(examples[j], examples[j].length);
				estimatedSamples[j] = Arrays.copyOf(examples[j], examples[j].length);
			}
		}
	}

	/*
	 * Main Class that drives the program
	 */
	public static void main(String[] args) {
		CrossValidator validator = new CrossValidator();
		validator.setOutput();
		validator.execute();
	}

	public void setOutput() {
		try {
			this.output = new BufferedWriter(new FileWriter(outputFilePath));
			String str = "Durga Sai Preetham Palagummi\n\n";
			this.output.write(str);
			this.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
}
