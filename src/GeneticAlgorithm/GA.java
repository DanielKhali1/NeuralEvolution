package GeneticAlgorithm;

import NeuralNetwork.Mat;

public abstract class GA 
{
	private int populationSize;
	private int mutationRate;
	private int generation;
	private Mat[] population;
	
	public GA(int PopulationSize, int MutiationRate)
	{
		this.populationSize = PopulationSize;
		this.mutationRate = MutiationRate;
		this.generation = 1;
		
	}
	
	public abstract Eval();
	
	public void Evolve()
	{
		Mat[] newPopulation = new Mat[populationSize];
		
		for(int i = 0; i < populationSize; i++)
		{
			Mat Parent1 = SelectParent();
			Mat Parent2 = SelectParent();
			
			Mat Child = Crossover(Parent1, Parent2);
			Mat MutatedChild = Mutation(Child);
			
			newPopulation[i] = MutatedChild;
		}
		
		population = newPopulation;
	}
	


	private Mat SelectParent() 
	{
		return null;
	}
	
	private Mat Crossover(Mat parent1, Mat parent2) 
	{
		return null;
	}
	
	private Mat Mutation(Mat child) 
	{
		return null;
	}

	
}
