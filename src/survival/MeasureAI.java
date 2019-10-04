package survival;

public class MeasureAI
{
	public static void main(String[] args)
	{
		int sum = 0;
		int iterations = 100;
		int minSurvival = Integer.MAX_VALUE;
		int maxSurvival = Integer.MIN_VALUE;
		
		for(int j = 0; j < iterations; j++)
		{
			Person survivor = new Person("GenericAI");
			
			while(survivor.isAlive())
			{
				System.out.println("Day " + survivor.getDaysSurvived());
				
				// -------------------------- YOUR CODE STARTS HERE  --------------------------//
				int spoiledFood = 0;
				int healthyFood = 0;
				
				/***
				 * Counts spoiled food in inventory
				 */
				for(int i = 0; i < survivor.getInventorySize(); i++)
                {
                	if(!survivor.getInventory()[i])
                	{
                		spoiledFood++;
                	}
                }
				
				/***
				 * Counts healthy food in inventory (Not currently used)
				 */
				for(int i = 0; i < survivor.getInventorySize(); i++)
                {
                	if(survivor.getInventory()[i])
                	{
                		healthyFood++;
                	}
                }
				
				/***
				 * Hunts for food as long as we have excess energy/hydration and space to obtain the maximum amount possible from a hunt.
				 */
				if(survivor.getInventorySize() <= (survivor.getMaxInventorySize() - 4) && survivor.getEnergy() >= 35 && survivor.getHydration() >= 15)
                {
                    survivor.hunt();
                }
				
                if(survivor.getHydration() < 40 && !survivor.hasTakenAction())
                {
                	/**
                	 * Eats healthy food if saturation is considerably lower than hydration, otherwise drinks
                	 */
                	if(survivor.getSatiation() < survivor.getHydration()/2)
                    {
                        for(int i = 0; i < survivor.getInventorySize(); i++)
                        {
                            if(survivor.isHealthyFood(i) == true)
                            {
                                survivor.eat(i);
                                break;
                            }
                        }
                    }
                	else
                	{
                		survivor.drink();
                	}
                }
                
                /**
                 * Eats healthy food
                 */
                if(survivor.getSatiation() < 40 && !survivor.hasTakenAction())
                {
                    for(int i = 0; i < survivor.getInventorySize(); i++)
                    {
                        if(survivor.isHealthyFood(i) == true)
                        {
                            survivor.eat(i);
                            break;
                        }
                    }
                }

                /**
                 * Makes sure we have space for more food before resting
                 */
                if(spoiledFood > 16 && !survivor.hasTakenAction())
                {
                	survivor.discardFood(0);
                }
                
                /**
                 * Rest
                 */
                if(survivor.getEnergy() < 40 && !survivor.hasTakenAction())
                {
                    survivor.rest();
                }

                /**
                 * Discards bad food
                 */
                for(int i = 0; i < survivor.getInventorySize(); i++)
                {
                    if(survivor.getInventory()[i] == false && !survivor.hasTakenAction())
                    {
                        survivor.discardFood(i);
                    }
                }
				
				// -------------------------- YOUR CODE ENDS HERE  ---------------------------//
				
				survivor.nextDay();
			}
			
			sum += survivor.getDaysSurvived();
			minSurvival = Math.min(minSurvival, survivor.getDaysSurvived());
			maxSurvival = Math.max(maxSurvival, survivor.getDaysSurvived());
		}
		
		System.out.println();
		System.out.println("Survival Average = " + (double) sum / iterations);
		System.out.println("Survival Min = " + minSurvival);
		System.out.println("Survival Max = " + maxSurvival);
	}
}
