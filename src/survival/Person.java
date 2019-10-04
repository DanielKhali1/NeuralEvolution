package survival;

import java.util.ArrayList;
import java.util.List;

/**
 * A simulation of a a person living in a desert with a supply of fresh water nearby.
 *  
 * @author AI Club
 */
public class Person
{
	private int satiation;
	private int hydration;
	private int energy;
	private int daysSurvived;
	private boolean actionTaken;
	private final String name;
	private final List<Boolean> inventory;
	private final boolean showWarnings;
	private static final int MAX_INVENTORY_SIZE = 20; 
	
	/**
	 * Creates a new person with the provided name.
	 * 
	 * <p><b>
	 * Fills the inventory with 10 items that have a 25% chance to be poisonous.
	 * </b></p>
	 * 
	 * @param name - name of the person
	 */
	public Person(String name)
	{
		this(name, true);
	}
	
	/**
	 * Creates a new person with the provided name.
	 * 
	 * <p><b>
	 * Fills the inventory with 10 items that have a 25% chance to be poisonous.
	 * </b></p>
	 * 
	 * @param name - name of the person
	 * @param showWarnings - whether to show warnings or not
	 */
	public Person(String name, boolean showWarnings)
	{
		this.name = name;
		this.showWarnings = showWarnings;
		actionTaken = false;
		satiation = 100;
		hydration = 100;
		energy = 100;
		daysSurvived = 0;
		inventory = new ArrayList<>();
		for(int i = 0; i < 10; i++)
		{
			boolean isPoisonous = Math.random() <= 0.25; // 25% poisonous
			addFood(isPoisonous);
		}
	}
	
	/**
	 * Proceeds the world to the next day. If the person has not performed any actions, they will {@link #rest()}.
	 * 
	 * <p><b>
	 * Every day, the person:
	 * <ul>
	 * <li>Loses 1 energy</li>
	 * <li>Loses 2 satiation</li>
	 * <li>Loses 3 hydration</li>
	 * </ul>
	 * </b></p>
	 */

	public void nextDay()
	{
		if(!isAlive(false))
			return;
		
		satiation = Math.max(0, satiation - 2);
		hydration = Math.max(0, hydration - 3);
		energy = Math.max(0, energy - 1);
		
		if(!actionTaken)
			rest();
		
		daysSurvived++;
		actionTaken = false;
		System.out.println(name + "'s Satiation: " + getSatiation() + "%\tHydration: " + getHydration() + "%\tEnergy: " + getEnergy() + "%\tInventory Size: " + getInventorySize() + " / " + getMaxInventorySize());
		System.out.print("Inventory: [");
		for(int i = 0; i < getInventorySize(); i++)
		{
			System.out.print(isHealthyFood(i) ? "Food" : "Poison");
			
			if(i != getInventorySize() - 1)
				System.out.print(", ");
		}
		System.out.println("]\n");
	}
	
	/**
	 * Discards the specified food index in the inventory. This will consume an action.
	 * An index of 0 refers to the first item in the inventory. The index ranges from 0 to {@link #getInventorySize()}-1.
	 * 
	 * <p><b>
	 * This costs no energy.
	 * </b></p>
	 * 
	 * @param inventoryIndex - the index of the inventory to discard (zero-based)
	 */
	public void discardFood(int inventoryIndex)
	{
		if(actionTaken)
		{
			printWarning("Cannot discard food as an action has already been done on this day.");
			return;
		}
		
		if(getInventorySize() == 0)
		{
			printWarning("Cannot discard food as the inventory is empty.");
			return;
		}
		
		if(inventoryIndex < 0 || inventoryIndex >= getInventorySize())
		{
			printWarning("Cannot discard food as the index (" + inventoryIndex + ") is out of bounds.");
			return;
		}
		
		actionTaken = true;
		System.out.println(name + " is discarding " + (isHealthyFood(inventoryIndex)?"healthy food." : "poisonous food."));
		inventory.remove(inventoryIndex);
	}
	
	/**
	 * <p><b>
	 * Loses 2 energy and if food is healthy, gains 12 satiation, otherwise loses 50 satiation.
	 * </b></p>
	 * 
	 * Eats the specified food in the inventory. This will consume an action.
	 * An index of 0 refers to the first item in the inventory. The index ranges from 0 to {@link #getInventorySize()}-1.
	 * 
	 * @param inventoryIndex - the index of the food to eat (zero-based)
	 */
	public void eat(int inventoryIndex)
	{
		if(actionTaken)
		{
			printWarning("Cannot eat as an action has already been done on this day.");
			return;
		}
		
		if(inventoryIndex < 0 || inventoryIndex >= getInventorySize())
		{
			printWarning("Cannot eat as the index (" + inventoryIndex + ") is out of bounds.");
			return;
		}
		
		actionTaken = true;
		System.out.println(name + " is eating.");
		boolean isHealthy = isHealthyFood(inventoryIndex);
		if(isHealthy)
		{
			satiation = Math.min(100, satiation + 12);
		}
		else
		{
			satiation = Math.max(0, satiation - 50);
			System.out.println(name + " was poisoned by eating bad food!");
		}
		inventory.remove(inventoryIndex);
		energy = Math.max(0, energy - 2);
	}
	
	/**
	 * <p><b>
	 * Loses 1 energy and gains 13 hydration. 
	 * </b></p>
	 * 
	 * This will consume an action.
	 */
	public void drink()
	{
		if(actionTaken)
		{
			printWarning("Cannot drink as an action has already been done on this day.");
			return;
		}
		
		actionTaken = true;
		hydration = Math.min(100, hydration + 13);
		energy = Math.max(0, energy - 1);
		System.out.println(name + " is drinking.");
	}
	
	/**
	 * <p><b>
	 * Gains 11 energy. 
	 * </b></p>
	 * 
	 * This will consume an action.
	 */
	public void rest()
	{
		if(actionTaken)
		{
			printWarning("Cannot rest as an action has already been done on this day.");
			return;
		}
		
		actionTaken = true;
		energy = Math.min(100, energy + 11);
		System.out.println(name + " is resting.");
	}
	
	/**
	 * <p><b>
	 * Loses 20 energy and gains random food in the inventory. 
	 * </b></p>
	 * 
	 * <p><b>
	 * The quantity of food gained can be 0 to 4.
	 * The food itself has a 75% chance to be food and 25% chance to be poison.
	 * </b></p>
	 * 
	 * <p><b><u>
	 * Note: Inventory has a limited size, and if it is full, the hunt will yield nothing! Try discarding items.
	 * </u></b></p>
	 * 
	 * This will consume an action.
	 */
	public void hunt()
	{
		if(actionTaken)
		{
			printWarning("Cannot hunt as an action has already been done on this day.");
			return;
		}
		
		actionTaken = true;
		int remainingSpace = getRemainingInventorySpace();
		int huntGainCount = Math.min(remainingSpace, (int) (Math.random() * 4));
		String huntGainString = "[";
		for(int i = 0; i < huntGainCount; i++)
		{
			boolean isPoisonous = Math.random() <= 0.25; // 25% poisonous
			addFood(isPoisonous);
			
			if(isPoisonous)
				huntGainString += "Poison";
			else
				huntGainString += "Food";
			
			if(i != huntGainCount - 1)
				huntGainString += ", ";
		}
		huntGainString += "]";
		energy = Math.max(0, energy - 20);
		if(huntGainCount == 0)
			System.out.println(name + " is hunting. Found no items.");
		else
			System.out.println(name + " is hunting. Found " + huntGainCount + " items of which are: " + huntGainString + ".");
	}
	
	/**
	 * Adds food to the inventory, if not full.
	 * 
	 * @param poisonous - true if the food is poisonous, false if healthy
	 */
	private void addFood(boolean poisonous)
	{
		if(isInventoryFull())
			return;
		
		inventory.add(!poisonous);
	}
	
	/**
	 * Shows a warning message to the console only if the option to show warnings is enabled.
	 * 
	 * @param message - the warning message to display
	 */
	private void printWarning(String message)
	{
		if(showWarnings)
			System.out.println("[WARNING] " + message);
	}
	
	/**
	 * Returns true if the person is alive, and false if dead.
	 * 
	 * <p><b>
	 * A person is dead if any of the following conditions are met:
	 * <ol>
	 * <li>Satiation reaches 0.</li>
	 * <li>Hydration reaches 0.</li>
	 * <li>Energy reaches 0.</li>
	 * </ol>
	 * </b></p>
	 * 
	 * @return true if alive, false if dead.
	 */
	public boolean isAlive()
	{
		return isAlive(true);
	}
	
	/**
	 * Returns true if the person is alive, and false if dead.
	 * 
	 * <p><b>
	 * A person is dead if any of the following conditions are met:
	 * <ol>
	 * <li>Satiation reaches 0.</li>
	 * <li>Hydration reaches 0.</li>
	 * <li>Energy reaches 0.</li>
	 * </ol>
	 * </b></p>
	 * 
	 * @return true if alive, false if dead.
	 */
	private boolean isAlive(boolean print)
	{
		if(energy <= 0)
		{
			if(print) System.out.println(name + " died of delirious hallucination at day " + daysSurvived + ".");
			return false;
		}
		
		if(satiation <= 0)
		{
			if(print) System.out.println(name + " died of starvation at day " + daysSurvived + ".");
			return false;
		}
		
		if(hydration <= 0)
		{
			if(print) System.out.println(name + " died of dehydration at day " + daysSurvived + ".");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns true if an action has been taken on this turn.
	 * 
	 * <p><b>
	 * The following are the possible actions:
	 * <ul>
	 * <li>{@link #eat()} or {@link #eat(int)}</li>
	 * <li>{@link #drink()}</li>
	 * <li>{@link #discardFood()} or {@link #discardFood(int)}</li>
	 * <li>{@link #rest()}</li>
	 * <li>{@link #hunt()}</li>
	 * </ul>
	 * </b></p>
	 * 
	 * <p><b>
	 * Note: If no actions have been taken this turn, then the person will rest.
	 * </b></p>
	 * 
	 * @return true if action has been taken, false otherwise
	 */
	public boolean hasTakenAction()
	{
		return actionTaken;
	}
	
	/**
	 * Returns the satiation of the person. It is a value from 0 (starving) to 100 (well-fed).
	 * Once it reaches 0, the person dies.
	 * 
	 * @return satiation of person
	 */
	public int getSatiation()
	{
		return satiation;
	}
	
	/**
	 * Returns the hydration of the person. It is a value from 0 (thirsty) to 100 (hydrated).
	 * Once it reaches 0, the person dies.
	 * 
	 * @return hydration of person
	 */
	public int getHydration()
	{
		return hydration;
	}

	/**
	 * Returns the energy of the person. It is a value from 0 (tired) to 100 (energetic).
	 * Once it reaches 0, the person dies.
	 * 
	 * @return energy of person
	 */
	public int getEnergy()
	{
		return energy;
	}

	/**
	 * Returns the name of the person.
	 * 
	 * @return name of person
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the number of days survived by the person.
	 * 
	 * @return survival days
	 */
	public int getDaysSurvived()
	{
		return daysSurvived;
	}

	/**
	 * Returns an array of the current food inventory.
	 * The array is of type boolean, where true represents healthy food and false represents poisonous food.
	 * 
	 * <p>
	 * See {@link #isHealthyFood(int)} and {@link #isPoisonousFood(int)}.
	 * </p>
	 * 
	 * @return the food inventory
	 */
	public boolean[] getInventory()
	{
		boolean[] clone = new boolean[inventory.size()];
		for(int i = 0; i < inventory.size(); i++)
		{
			clone[i] = inventory.get(i);
		}
		return clone;
	}
	
	/**
	 * Returns true if the specified food is healthy, false otherwise.
	 * Returns false if index is out of bounds.
	 * 
	 * @param inventoryIndex the food to check if healthy
	 * 
	 * @return true if healthy, false otherwise
	 */
	public boolean isHealthyFood(int inventoryIndex)
	{
		if(inventoryIndex < 0 || inventoryIndex >= getInventorySize())
			return false;
		
		return inventory.get(inventoryIndex);
	}
	
	/**
	 * Returns true if the specified food is poisonous, false otherwise.
	 * Returns false if index is out of bounds.
	 * 
	 * @param inventoryIndex the food to check if poisonous
	 * 
	 * @return true if poisonous, false otherwise
	 */
	public boolean isPoisonousFood(int inventoryIndex)
	{
		if(inventoryIndex < 0 || inventoryIndex >= getInventorySize())
			return false;
		
		return !inventory.get(inventoryIndex);
	}
	
	/**
	 * Returns the number of food in the inventory. Note food can be poisonous.
	 * 
	 * @return size of inventory
	 */
	public int getInventorySize()
	{
		return inventory.size();
	}
	
	/**
	 * Returns the maximum allowable number of items in the inventory.
	 * 
	 * @return max size of inventory
	 */
	public int getMaxInventorySize()
	{
		return MAX_INVENTORY_SIZE;
	}
	
	/**
	 * Returns true if the inventory is full, false otherwise.
	 * 
	 * @return true if full, false otherwise
	 */
	public boolean isInventoryFull()
	{
		return getInventorySize() == getMaxInventorySize();
	}
	
	/**
	 * Returns how many more items can be fit in the inventory before it is full.
	 * 
	 * @return remaining space in inventory
	 */
	public int getRemainingInventorySpace()
	{
		return getMaxInventorySize() - getInventorySize();
	}
}