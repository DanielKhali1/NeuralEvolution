package GeneticAlgorithm;

import NeuralNetwork.Mat;
import NeuralNetwork.NeuralNetwork;

public class GA 
{
	private int populationSize;
	private double mutationRate;
	private int generation;
	private NeuralNetwork[] population;
	private int[] topology;
	
	public GA(int PopulationSize, double MutiationRate, int[] topology, int hiddenLayers)
	{
		this.setPopulationSize(PopulationSize);
		this.mutationRate =(MutiationRate);
		this.generation = (1);
		this.topology = topology;
		population = new NeuralNetwork[PopulationSize];
		generateRandomPopulation(hiddenLayers);
		
	}
	
	private void generateRandomPopulation(int hiddenLayers) 
	{
		for(int i = 0; i < populationSize; i++)
		{
			NeuralNetwork Individual = new NeuralNetwork(topology[0], hiddenLayers, topology[1], topology[2]);
			getPopulation()[i] = Individual;
		}
	}

	public void Eval(int[] fitnesses)
	{
		for(int i = 0; i < fitnesses.length; i++)
		{
			getPopulation()[i].setFitness(fitnesses[i]);
		}
	}
	
	public void Evolve()
	{
		 NeuralNetwork[] newPopulation = new NeuralNetwork[getPopulation().length];
		 
		 newPopulation[0] = bestChromosome();
		 
		for(int i = 1; i < getPopulationSize(); i++)
		{
			NeuralNetwork Parent1 = SelectParent();
			NeuralNetwork Parent2 = SelectParent();
			
			NeuralNetwork Child = Crossover(Parent1, Parent2);
			NeuralNetwork MutatedChild = Mutation(Child);
			
			newPopulation[i] = MutatedChild;
		}
		
		generation += 1;
		setPopulation(newPopulation);
	}

	private NeuralNetwork SelectParent() 
	{
		NeuralNetwork fighter1 = getPopulation()[(int)(Math.random() * getPopulationSize())];
		NeuralNetwork fighter2 = getPopulation()[(int)(Math.random() * getPopulationSize())];
		
		return (fighter1.getFitness() > fighter2.getFitness()) ? fighter1 : fighter2;
	}
	
	private NeuralNetwork Crossover(NeuralNetwork parent1, NeuralNetwork parent2) 
	{
		return parent1.clone().Breed(parent2.clone());
		
	}
	
	private NeuralNetwork Mutation(NeuralNetwork child) 
	{
		NeuralNetwork x = child.clone().mutateWeights(mutationRate);
		return x;
		
	}

	
	public int getGeneration() 
	{
		return generation;
	}
	
	public double getMutationRate() 
	{
		return mutationRate;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public NeuralNetwork[] getPopulation() {
		return population;
	}

	public void setPopulation(NeuralNetwork[] population) {
		this.population = population;
	}

	public int bestChromosomeFitness() 
	{
		int bestFit = 0;
		for(int i = 0; i < population.length; i++)
		{
			if(population[i].getFitness() > bestFit)
			{
				bestFit = population[i].getFitness();
			}
		}
		return bestFit;
	}
	
	public NeuralNetwork bestChromosome() 
	{
		int bestFit = 0;
		NeuralNetwork best = population[0];
		for(int i = 0; i < population.length; i++)
		{
			if(population[i].getFitness() > bestFit)
			{
				bestFit = population[i].getFitness();
				best = population[i];
			}
		}
		return best.clone();
	}
	
}
