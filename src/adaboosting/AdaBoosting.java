package adaboosting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

public class AdaBoosting {
	
	int numberOfIterations;
	int numberOfExamples;
	double epsilon;
	double[] x;
	int[] y;
	double[] probabilities;
	String inputFilePath = "D:\\UTD_dxp141030\\Summer_2015\\CS6375_Machine Learning\\Fall\\Machine Learning\\Assignments\\Graded Homework 2\\input files\\adaboost-5.txt";
	String outputFilePathBinary = "D:\\UTD_dxp141030\\Summer_2015\\CS6375_Machine Learning\\Fall\\Machine Learning\\Assignments\\Graded Homework 2\\input files\\dxp141030_binary_5.txt";
	String outputFilePathReal = "D:\\UTD_dxp141030\\Summer_2015\\CS6375_Machine Learning\\Fall\\Machine Learning\\Assignments\\Graded Homework 2\\input files\\dxp141030_real_5.txt";
	
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
	
	public void readInputFile() {
		BufferedReader inputFile;
		try {
			inputFile = inputFileReader(inputFilePath);
			String[] boostingParams = inputFile.readLine().split(" ");
			
			numberOfIterations = Integer.parseInt(boostingParams[0]);
			numberOfExamples = Integer.parseInt(boostingParams[1]);
			epsilon = Double.parseDouble(boostingParams[2]);
			
			x = new double[numberOfExamples];
			y = new int[numberOfExamples];
			probabilities = new double[numberOfExamples];
			
			String inputs = inputFile.readLine();
			int i = 0;
			
			String[] inputsArray = inputs.split(" ");
			if (inputsArray.length == numberOfExamples) {
				for (i = 0; i < numberOfExamples; i++) {
					x[i] = Double.parseDouble(inputsArray[i]);
				}
			}
			else {
				System.out.println("The number of x values does not match the number of examples in line 1.");
				System.exit(0);
			}
			
			inputs = inputFile.readLine();
			inputsArray = inputs.split(" ");
			i = 0;
			if (inputsArray.length == numberOfExamples) {
				for (i = 0; i < numberOfExamples; i++) {
					y[i] = Integer.parseInt(inputsArray[i]);
				}
			}
			else {
				System.out.println("The number of y values does not match the number of examples in line 1.");
				System.exit(0);
			}
			
			inputs = inputFile.readLine();
			inputsArray = inputs.split(" ");
			i = 0;
			if (inputsArray.length == numberOfExamples) {
				for (i = 0; i < numberOfExamples; i++) {
					probabilities[i] = Double.parseDouble(inputsArray[i]);
				}
			}
			else {
				System.out.println("The number of probabilities does not match the number of examples in line 1.");
				System.exit(0);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void binaryAdaBoosting() {
		int i, j, t, chosenClassifierPosition;
		double errorProbability, minErrorProbability, bound = 1.0, boostedClassifierError;
		int[] classifiedy = new int[numberOfExamples];
		int[] chosenClassifier = new int[numberOfExamples];
		
		double alpha, normalizationFactor;
		double[] updatedProbabilities = Arrays.copyOf(probabilities, probabilities.length);
		double[] boostedClassifier = new double[numberOfExamples];
		String boostedClassifierFunction = "";
		String classifierH = "";
		
		for (t = 0; t < numberOfIterations; t++) {
			alpha = 0;
			normalizationFactor = 0;
			boostedClassifierError = 0;
			chosenClassifierPosition = 0;
			minErrorProbability = 1.0;
			
			for (i = 0; i < (numberOfExamples - 1); i++) {
				errorProbability = 0.0;
				for (j = 0; j <= i; j++) {
					classifiedy[j] = 1;
					if (y[j] != classifiedy[j])
						errorProbability = errorProbability + updatedProbabilities[j];
				}
				for (j = i+1; j < numberOfExamples; j++) {
					classifiedy[j] = -1;
					if (y[j] != classifiedy[j])
						errorProbability = errorProbability + updatedProbabilities[j];
				}
				
				if (errorProbability < minErrorProbability) {
					minErrorProbability = errorProbability;
					chosenClassifierPosition = i;
					chosenClassifier = Arrays.copyOf(classifiedy, classifiedy.length);
				}
				
				errorProbability = 0;
				for (j = 0; j <= i; j++) {
					classifiedy[j] = -1;
					if (y[j] != classifiedy[j])
						errorProbability = errorProbability + updatedProbabilities[j];
				}
				for (j = i+1; j < numberOfExamples; j++) {
					classifiedy[j] = 1;
					if (y[j] != classifiedy[j])
						errorProbability = errorProbability + updatedProbabilities[j];
				}
				
				if (errorProbability < minErrorProbability) {
					minErrorProbability = errorProbability;
					chosenClassifierPosition = i;
					chosenClassifier = Arrays.copyOf(classifiedy, classifiedy.length);
				}
			}
			
			alpha = 0.5 * Math.log((1 - minErrorProbability)/minErrorProbability);
			for (j = 0; j < numberOfExamples; j++) {
				if (y[j] == chosenClassifier[j])
					updatedProbabilities[j] = updatedProbabilities[j] * Math.pow(Math.E, -alpha);
				else
					updatedProbabilities[j] = updatedProbabilities[j] * Math.pow(Math.E, alpha);
				normalizationFactor = normalizationFactor + updatedProbabilities[j];
				boostedClassifier[j] = boostedClassifier[j] + (alpha * chosenClassifier[j]); 
				
				if ((y[j] < 0 && boostedClassifier[j] > 0) || (y[j] > 0 && boostedClassifier[j] < 0))
					boostedClassifierError++;
			}
			
			DecimalFormat df = new DecimalFormat("0.000");
			DecimalFormat df1 = new DecimalFormat("0.00000");
			alpha = Double.parseDouble(df.format(alpha));
			
			boostedClassifierError = boostedClassifierError/numberOfExamples;
			boostedClassifierError = Double.parseDouble(df.format(boostedClassifierError));
			
			double temp = (x[chosenClassifierPosition] + x[chosenClassifierPosition+1])/2; 
			temp = Double.parseDouble(df1.format(temp));
			
			if (chosenClassifier[0] == 1)
				classifierH = "I(x < " + temp + ")";
			else
				classifierH = "I(x > " + temp + ")";
			
			if (boostedClassifierFunction != "")
				boostedClassifierFunction = boostedClassifierFunction + " + " + alpha + " * " + classifierH;
			else
				boostedClassifierFunction = alpha + " * " + classifierH;
			
			for (j = 0; j < numberOfExamples; j++) {
				updatedProbabilities[j] = updatedProbabilities[j] / normalizationFactor;
			}
			
			bound = bound * normalizationFactor;
			
			outputBinary (classifierH, chosenClassifierPosition, minErrorProbability, alpha, normalizationFactor, updatedProbabilities, boostedClassifierFunction, boostedClassifierError, bound, t);
		}
	}
	
	public void realAdaBoosting() {
		int i, j, t, chosenClassifierPosition = 0;
		double errorProbability, minErrorProbability = 1.0, bound = 1.0, boostedClassifierError = 0;
		int[] classifiedy = new int[numberOfExamples];
		int[] chosenClassifier = new int[numberOfExamples];
		
		String classifierH = "";
		double normalizationFactor = 0, ctPlus = 0, ctMinus = 0, prPlus, prMinus, pwPlus, pwMinus;
		double minprPlus = 0, minprMinus = 0, minpwPlus = 0, minpwMinus = 0;
		
		double[] updatedProbabilities = Arrays.copyOf(probabilities, probabilities.length);
		double[] boostedClassifier = new double[numberOfExamples];
		
		for (t = 0; t < numberOfIterations; t++) {
			ctPlus = 0; ctMinus = 0;
			normalizationFactor = 0;
			boostedClassifierError = 0;
			chosenClassifierPosition = 0;
			minErrorProbability = 1.0;
			for (i = 0; i < numberOfExamples; i++) {
				errorProbability = 0;
				prPlus = 0; prMinus = 0; pwPlus = 0; pwMinus = 0;
				for (j = 0; j <= i; j++) {
					classifiedy[j] = 1;
				}
				for (j = i+1; j < numberOfExamples; j++) {
					classifiedy[j] = -1;
				}
				for (j = 0; j < numberOfExamples; j++) {
					if (y[j] == 1 && classifiedy[j] == 1)
						prPlus = prPlus + updatedProbabilities[j];
					else if (y[j] == -1 && classifiedy[j] == -1)
						prMinus = prMinus + updatedProbabilities[j];
					else if (y[j] == -1 && classifiedy[j] == 1)
						pwMinus = pwMinus + updatedProbabilities[j];
					else
						pwPlus = pwPlus + updatedProbabilities[j];
				}
				errorProbability = Math.pow((prPlus*pwMinus), 0.5) + Math.pow((pwPlus*prMinus), 0.5);
				if (errorProbability < minErrorProbability) {
					minErrorProbability = errorProbability;
					minprPlus = prPlus; minprMinus = prMinus; minpwPlus = pwPlus; minpwMinus = pwMinus;
					chosenClassifierPosition = i;
					chosenClassifier = Arrays.copyOf(classifiedy, classifiedy.length);
				}
				
				errorProbability = 0;
				prPlus = 0; prMinus = 0; pwPlus = 0; pwMinus = 0;
				for (j = 0; j <= i; j++) {
					classifiedy[j] = -1;
				}
				for (j = i+1; j < numberOfExamples; j++) {
					classifiedy[j] = 1;
				}
				for (j = 0; j < numberOfExamples; j++) {
					if (y[j] == 1 && classifiedy[j] == 1)
						prPlus = prPlus + updatedProbabilities[j];
					else if (y[j] == -1 && classifiedy[j] == -1)
						prMinus = prMinus + updatedProbabilities[j];
					else if (y[j] == -1 && classifiedy[j] == 1)
						pwMinus = pwMinus + updatedProbabilities[j];
					else
						pwPlus = pwPlus + updatedProbabilities[j];
				}
				errorProbability = Math.pow((prPlus*pwMinus), 0.5) + Math.pow((pwPlus*prMinus), 0.5);
				if (errorProbability < minErrorProbability) {
					minErrorProbability = errorProbability;
					minprPlus = prPlus; minprMinus = prMinus; minpwPlus = pwPlus; minpwMinus = pwMinus;
					chosenClassifierPosition = i;
					chosenClassifier = Arrays.copyOf(classifiedy, classifiedy.length);
				}			
			}
			ctPlus = 0.5 * Math.log((minprPlus + epsilon) / (minpwMinus + epsilon));
			ctMinus = 0.5 * Math.log((minpwPlus + epsilon) / (minprMinus + epsilon));
			
			for (j = 0; j < numberOfExamples; j++) {
				if (chosenClassifier[j] == 1) {
					updatedProbabilities[j] = updatedProbabilities[j] * Math.pow(Math.E, -y[j]*ctPlus);
					boostedClassifier[j] = boostedClassifier[j] + ctPlus;
				}
				else {
					updatedProbabilities[j] = updatedProbabilities[j] * Math.pow(Math.E, -y[j]*ctMinus);
					boostedClassifier[j] = boostedClassifier[j] + ctMinus;
				}
				normalizationFactor = normalizationFactor + updatedProbabilities[j]; 
				
				if ((y[j] < 0 && boostedClassifier[j] > 0) || (y[j] > 0 && boostedClassifier[j] < 0))
					boostedClassifierError++;
			}
			
			DecimalFormat df = new DecimalFormat("0.000");
			DecimalFormat df1 = new DecimalFormat("0.00000");
			
			boostedClassifierError = boostedClassifierError/numberOfExamples;
			boostedClassifierError = Double.parseDouble(df.format(boostedClassifierError));
			
			double temp = (x[chosenClassifierPosition] + x[chosenClassifierPosition+1])/2; 
			temp = Double.parseDouble(df1.format(temp));
			
			if (chosenClassifier[0] == 1)
				classifierH = "I(x < " + temp + ")";
			else
				classifierH = "I(x > " + temp + ")";
			
			for (j = 0; j < numberOfExamples; j++) {
				updatedProbabilities[j] = updatedProbabilities[j] / normalizationFactor;
			}
			
			bound = bound * normalizationFactor;
			
			outputReal (classifierH, chosenClassifierPosition, minErrorProbability, ctPlus, ctMinus, normalizationFactor, updatedProbabilities, boostedClassifier, boostedClassifierError, bound, t);
		}
	}
	
	public void outputBinary(String classifierH, int chosenClassifierPosition, double error, double alpha, double normalizationFactor, double[] updatedProbabilities, String boostedClassifierFunction, double boostedClassifierError, double bound, int t) {
		try {
			PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePathBinary, true)));
			output.printf("\nIteration %d:\n", t+1);
			output.printf("Classifier h = %s\n", classifierH);
			
			output.printf("Error = %.4f\n", error);
			output.printf("Alpha = %.4f\n", alpha);
			output.printf("Normalization Factor Z = %.4f\n", normalizationFactor);
			
			output.print("Pi after normalization = ");
			for (int i = 0; i < numberOfExamples; i++) {
				if (i == (numberOfExamples - 1))
					output.printf("%.4f", updatedProbabilities[i]);
				else
					output.printf("%.4f, ", updatedProbabilities[i]);
			}
			
			output.printf("\nBoosted Classifier f(x) = %s", boostedClassifierFunction);
			output.print("\nBoosted Classifier Error E = " + boostedClassifierError + "\n");
			output.printf("Bound on Error = %.4f\n", bound);
			output.flush();
			output.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void outputReal (String classifierH, int chosenClassifierPosition, double error, double ctPlus, double ctMinus, double normalizationFactor, double[] updatedProbabilities, double[] boostedClassifier, double boostedClassifierError, double bound, int t) {
		try {
			PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePathReal, true)));
			output.printf("\nIteration %d:\n", t+1);
			output.printf("Classifier h = %s\n", classifierH);
			
			output.printf("G error = %.4f\n", error);
			output.printf("C_Plus = %.4f, C_Minus = %.4f\n", ctPlus, ctMinus);
			output.printf("Normalization Factor Z = %.4f\n", normalizationFactor);
			
			output.print("Pi after normalization = ");
			for (int i = 0; i < numberOfExamples; i++) {
				if (i == (numberOfExamples - 1))
					output.printf("%.4f", updatedProbabilities[i]);
				else
					output.printf("%.4f, ", updatedProbabilities[i]);
			}
			
			output.printf("\nf(x) = ");
			for (int i = 0; i < numberOfExamples; i++) {
				if (i == (numberOfExamples - 1))
					output.printf("%.4f", boostedClassifier[i]);
				else
					output.printf("%.4f, ", boostedClassifier[i]);
			}
			
			output.print("\nBoosted Classifier Error = " + boostedClassifierError + "\n");
			output.printf("Bound on Error = %.4f\n", bound);
			output.flush();
			output.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void setOutputBinary() {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFilePathBinary));
			String str = "Durga Sai Preetham Palagummi\n";
			output.write(str);
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
	
	public void setOutputReal() {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(outputFilePathReal));
			String str = "Durga Sai Preetham Palagummi\n";
			output.write(str);
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}

	public static void main(String[] args) {
		AdaBoosting adaBoosting = new AdaBoosting();
		adaBoosting.readInputFile();
		adaBoosting.setOutputBinary();
		adaBoosting.setOutputReal();
		adaBoosting.binaryAdaBoosting();
		adaBoosting.realAdaBoosting();
	}

}
