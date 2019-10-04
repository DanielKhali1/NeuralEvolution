package GeneticAlgorithm;

import NeuralNetwork.Mat;
import NeuralNetwork.NeuralNetwork;

public abstract class GA 
{
	private int populationSize;
	private int mutationRate;
	private int generation;
	private NeuralNetwork[] population;
	private int[] topology;
	
	public GA(int PopulationSize, int MutiationRate, NeuralNetwork nn, int[] topology)
	{
		this.populationSize = PopulationSize;
		this.mutationRate = MutiationRate;
		this.generation = 1;
		this.topology = topology;
		
		generateRandomPopulation();
		
	}
	
	private void generateRandomPopulation() 
	{
		for(int i = 0; i < population.length; i++)
		{
			NeuralNetwork Individual = new NeuralNetwork(topology[0], topology[1], topology[2]);
			population[i] = Individual;
		}
	}

	public abstract int Eval();
	
	public void Evolve()
	{
		 NeuralNetwork[] newPopulation = new NeuralNetwork[population.length];
		
		for(int i = 0; i < populationSize; i++)
		{
			NeuralNetwork Parent1 = SelectParent();
			NeuralNetwork Parent2 = SelectParent();
			
			NeuralNetwork Child = Crossover(Parent1, Parent2);
			NeuralNetwork MutatedChild = Mutation(Child);
			
			newPopulation[i] = MutatedChild;
		}
		
		generation ++;
		population = newPopulation;
	}

	private NeuralNetwork SelectParent() 
	{
		NeuralNetwork fighter1 = population[(int)(Math.random() * populationSize)];
		NeuralNetwork fighter2 = population[(int)(Math.random() * populationSize)];
		
		return (fighter1.getFitness() > fighter2.getFitness()) ? fighter1 : fighter2;
	}
	
	private NeuralNetwork Crossover(NeuralNetwork parent1, NeuralNetwork parent2) 
	{
		//creating a new child NN
		NeuralNetwork Child = new NeuralNetwork(topology[0], topology[1], topology[2]);
		
		Mat[] parent1Weights = parent1.weights.clone();
		Mat[] parent2Weights = parent2.weights.clone();
		
		
		for(int i = 0; i < parent1Weights.length; i++)
		{
			parent1Weights[i].add(parent2Weights[i]);
			parent1Weights[i].mult(0.5);
		}
		
		Child.setWeights(parent1Weights);
		return Child;
		
	}
	
	private NeuralNetwork Mutation(NeuralNetwork child) 
	{
		return null;
	}

	
}
