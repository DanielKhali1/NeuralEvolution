package survival;

import java.util.Arrays;

import GeneticAlgorithm.GA;

public class Template
{
	/**
	 *  
	 * A survivor's plane has crash landed on an island. He/She is the only one who survived.
	 * Nearby is a creek filled with unlimited fresh water and the survivor has found 10 meals in their plane's husk.
	 * 
	 * Your goal for this programming challenge is to survive as long as possible by strategically controlling your resources.
	 * 
	 * Energy, satiation, and hydration will all begin at 100%.
	 * Keep these values above 0% or else your survivor will die.
	 * 
	 * Every day, the survivor:
	 * 		Loses 1 energy
	 * 		Loses 2 satiation
	 * 		Loses 3 hydration
	 * 
	 * Your survivor has only so much food ( 10 ) in the beginning.
	 * Some of it has gone bad and is not safe for consumption (poisonous).
	 * 
	 * Your survivor carries all of their food with them (inside an inventory), all of which can be eaten, but if it is
	 * poisonous it is probably best to discard it. (poisonous food will subtract 50% from your Satiation).
	 * 
	 * To get more food, your survivor must hunt for food.
	 * Hunting doesn't always reward your survivor with the same results.
	 * It takes 20% of their energy away and has a chance of dropping [0,4] (inclusive) quantity of food.
	 * The food dropped has a 25% chance of being poisonous.
	 * 
	 * Moreover, the survivor's inventory has a limited size of only 20 items. If you hunt with a full inventory, you will get nothing!
	 * So make sure to keep your inventory's size in balance.
	 * 
	 * Your survivor can also drink to hydrate themselves, or rest to replenish their energy.
	 * 
	 * In total, your survivor has 5 actions:
	 * - eating
	 * - resting
	 * - drinking
	 * - discarding food from inventory
	 * - hunting
	 * 
	 * Only 1 action is allowed per day.
	 * If no action has been specified, the survivor will rest.
	 * 
	 * 
	 * HOW LONG WILL YOU LAST?
	 */
	Person survivor = new Person("dumbbitch");
	int[] topology = { 5, 50, 5 };
	GA geneticAlgorithm = new GA(100, 0.5, topology);

	public static void main(String[] args)
	{
		Template t = new Template();
		for(int generations = 0; generations < 50; generations++)
		{
			int[] fitnesses = new int[t.geneticAlgorithm.getPopulationSize()];
			for(int i = 0; i < t.geneticAlgorithm.getPopulationSize(); i++)
			{
				while(t.survivor.isAlive())
				{
					//will return array of outputs
					double[] actionArray = t.geneticAlgorithm.getPopulation()[i].process(t.getInputs(t.survivor));
					System.out.println(Arrays.toString(actionArray));
					int index = 0;
					double biggestElement = 0;
					for(int j = 0; j < actionArray.length; j++)
					{
						if(actionArray[j]  > biggestElement)
						{
							index = j;
							biggestElement = actionArray[j];
						}
					}
					
					switch(index)
					{
						case 0: t.eatHealthyFood();
							break;
						case 1: t.survivor.rest();
							break;
						case 2:  t.survivor.drink();
							break;
						case 3:  t.survivor.hunt();
							break;
						case 4:  t.DiscardUnhealthyFood();
							break;
					}
					
					
					t.survivor.nextDay();
				}
				fitnesses[i] = t.survivor.getDaysSurvived();
				t.survivor = new Person("dumb bitch" + t.geneticAlgorithm.getGeneration());

			}
			System.out.println(Arrays.toString(fitnesses));
			t.geneticAlgorithm.Eval(fitnesses);
			t.geneticAlgorithm.Evolve();
		}

	}
	
	public void eatHealthyFood()
	{
		for(int i = 0; i < survivor.getInventorySize(); i++)
		{
			if(survivor.getInventory()[i])
			{
				survivor.eat(i);
			}
		}
	}
	
	public void DiscardUnhealthyFood()
	{
		for(int i = 0; i < survivor.getInventorySize(); i++)
		{
			if(survivor.getInventory()[i])
			{
				survivor.discardFood(i);
			}
		}
	}
	
	
	public double[] getInputs(Person survivor)
	{
		double[] inputs = { relu(survivor.getHydration()), relu(survivor.getEnergy()), relu(survivor.getSatiation()), relu(getNumOfHealthy(survivor)), relu(getNumOfPoisonous(survivor)) };
		return inputs;
	} 
		
	public int getNumOfPoisonous(Person survivor)
	{
		int sum = 0;
		for(int i = 0; i < survivor.getInventorySize(); i++)
		{
			if(survivor.isPoisonousFood(i))
				sum++;
		}
		return sum;
	}
	
	public int getNumOfHealthy(Person survivor)
	{
		int sum = 0;
		for(int i = 0; i < survivor.getInventorySize(); i++)
		{
			if(survivor.isHealthyFood(i))
				sum++;
		}
		return sum;
	}
	
	
	public double relu(double val)
	{
		return Math.pow(Math.E, val) / (1 + Math.pow(Math.E, val));
	}
	
}
