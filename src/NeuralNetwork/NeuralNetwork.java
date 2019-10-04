package NeuralNetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import NeuralNetwork.Mat.MatFunc;

/**
 * This class is used to create new Neural Networks, configure them
 * (completely), and alter their functionality.
 * 
 * @author Hashim Kayani
 * 
 *         used and modified by
 * @author Daniel Khalil
 * 
 */
public class NeuralNetwork implements Cloneable {
	/**
	 * The amount of input nodes that this network has
	 */
	public final int inputNodes;
	/**
	 * The amount of hidden layers that this network has
	 */
	public final int hiddenLayers;
	/**
	 * The amount of hidden nodes per layer that this network has
	 */
	public final int hiddenNodes;
	/**
	 * The amount of output nodes that this network has
	 */
	public final int outputNodes;
	/**
	 * The weights that each layer has
	 */
	public Mat[] weights;
	/**
	 * The biases that each layer has
	 */
	public final Mat[] biases;
	/**
	 * This is beyond me and I suggest you google it
	 */
	private ActivationFunction activationFunction;

	private int fitness;

	/**
	 * This is how big the learning steps of this network will be. If too big, then
	 * the network might overshoot and fall further back in the learning process.
	 */
	private double learningRate;

	/**
	 * This creates a new randomized neural network with one hidden layer and the
	 * given input, hidden, and output nodes.
	 * 
	 * @param inputNodes
	 * @param hiddenNodes
	 * @param outputNodes
	 */
	public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
		this(inputNodes, 1, hiddenNodes, outputNodes);
	}

	public NeuralNetwork(int inputNodes, int hiddenLayers, int hiddenNodes, int outputNodes) {
		this.inputNodes = inputNodes;
		this.hiddenLayers = hiddenLayers;
		this.hiddenNodes = hiddenNodes;
		this.outputNodes = outputNodes;

		weights = new Mat[hiddenLayers + 1];
		for (int i = 0; i < hiddenLayers + 1; i++) {
			if (i == 0) {
				weights[i] = new Mat(hiddenNodes, inputNodes);
			} else if (i == hiddenLayers) {
				weights[i] = new Mat(outputNodes, hiddenNodes);
			} else {
				weights[i] = new Mat(hiddenNodes, hiddenNodes);
			}

			weights[i].randomize();
		}

		biases = new Mat[hiddenLayers + 1];
		for (int i = 0; i < hiddenLayers + 1; i++) {
			if (i == hiddenLayers) {
				biases[i] = new Mat(outputNodes, 1);
			} else {
				biases[i] = new Mat(hiddenNodes, 1);
			}

			biases[i].randomize();
		}

		learningRate = 0.01;
		activationFunction = new ActivationFunction(Mat.SIGMOID, Mat.SIGMOID_DERIVATIVE);
	}

	public NeuralNetwork(NeuralNetwork copy) {
		this.inputNodes = copy.inputNodes;
		this.hiddenLayers = copy.hiddenLayers;
		this.hiddenNodes = copy.hiddenNodes;
		this.outputNodes = copy.outputNodes;

		weights = new Mat[copy.weights.length];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = copy.weights[i].clone();
		}

		biases = new Mat[copy.biases.length];
		for (int i = 0; i < biases.length; i++) {
			biases[i] = copy.biases[i].clone();
		}
		learningRate = copy.learningRate;
		activationFunction = copy.activationFunction;
	}

	public double[] process(double[] inputArray) {
		if (inputArray.length != inputNodes)
			throw new IllegalArgumentException(
					"Input must have " + inputNodes + " element" + (inputNodes == 1 ? "" : "s"));

		for (int i = 0; i < inputNodes; i++) {
			if (Math.abs(inputArray[i]) > 2) {
				System.err.println("Index " + i + " is a bit too out-there");
			}
		}

		Mat input = Mat.fromArray(inputArray);

		for (int i = 1; i < hiddenLayers + 2; i++) {
			input = weights[i - 1].mult(input).add(biases[i - 1]).map(activationFunction.function);
		}

		return input.toArray();
	}

	public void setWeights(Mat[] weights) {
		this.weights = weights;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public NeuralNetwork setLearningRate(double learningRate) {
		this.learningRate = learningRate;
		return this;
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public NeuralNetwork setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
		return this;
	}

	public NeuralNetwork Breed(final NeuralNetwork other) {
		if (inputNodes != other.inputNodes || hiddenLayers != other.hiddenLayers || hiddenNodes != other.hiddenNodes
				|| outputNodes != other.outputNodes) {
			throw new IllegalArgumentException("These neural networks aren't compatible");
		}

		NeuralNetwork nn = clone();
		for (int i = 0; i < hiddenLayers + 1; i++) {
			Mat weight = nn.weights[i];

			final int indx = i;
			weight.map(new MatFunc() {
				@Override
				public double perform(double val, int r, int c) {
					return Math.random() >= 0.5 ? val : other.weights[indx].data[r][c];
				}
			});
		}
		return nn;
	}

	public NeuralNetwork mutateWeights(double chance) {
		return mutateWeights(chance, ThreadLocalRandom.current());
	}

	public NeuralNetwork mutateWeights(final double chance, final Random rand) {
		for (int i = 0; i < weights.length; i++) {
			weights[i].map(new MatFunc() {
				@Override
				public double perform(double val, int r, int c) {
					return rand.nextDouble() < chance ? val + (rand.nextDouble() * 2 - 1) / 10 : val;
				}
			});
		}
		return this;
	}

	public NeuralNetwork mutateBiases(double chance) {
		return mutateWeights(chance, ThreadLocalRandom.current());
	}

	public NeuralNetwork mutateBiases(final double chance, final Random rand) {
		for (int i = 0; i < weights.length; i++) {
			weights[i].map(new MatFunc() {
				@Override
				public double perform(double val, int r, int c) {
					return rand.nextDouble() < chance ? val + (rand.nextDouble() * 3 - 1.5) : val;
				}
			});
		}
		return this;
	}

	public NeuralNetwork clone() {
		return new NeuralNetwork(this);
	}

	public static class ActivationFunction {
		public final MatFunc function, derivative;

		public ActivationFunction(MatFunc function, MatFunc derivative) {
			this.function = function;
			this.derivative = derivative;
		}
	}

	public void setFitness(int fit) {
		fitness = fit;
	}

	public int getFitness() {
		return fitness;
	}
}
