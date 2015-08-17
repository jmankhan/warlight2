/**
 * Warlight AI Game Bot
 *
 * Last update: January 29, 2015
 *
 * @author Jim van Eeden
 * @version 1.1
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */

package bot;

/**
 * This is a simple bot that does random (but correct) moves.
 * This class implements the Bot interface and overrides its Move methods.
 * You can implement these methods yourself very easily now,
 * since you can retrieve all information about the match from variable “state”.
 * When the bot decided on the move to make, it returns an ArrayList of Moves. 
 * The bot is started by creating a Parser to which you add
 * a new instance of your bot, and then the parser is started.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import map.MapMatrix;
import map.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;
import debug.Log;

public class BotStarter implements Bot {
	

	public BotStarter() {
		super();
	}
	
	@Override
	/**
	 * A method that returns which region the bot would like to start on, the pickable regions are stored in the BotState.
	 * The bots are asked in turn (ABBAABBAAB) where they would like to start and return a single region each time they are asked.
	 * This method returns one random region from the given pickable regions.
	 * 
	 * We will call a class from strategies package to handle this
	 */
	public Region getStartingRegion(BotState state, Long timeOut) {

		//find most valuable super regions
		MapMatrix mapMatrix = new MapMatrix(state.getFullMap());
		LinkedHashMap<Integer, Double> multVector = mapMatrix.findBottleNecks(mapMatrix.getSuperAdjMatrix());
		
		//get regions we are allowed to pick
		ArrayList<Region> pickable = state.getPickableStartingRegions();
		String pickall = "Available super regions\n";
		for(Region r:pickable) {
			pickall += r.getSuperRegion().getId() + "\n";
		}
		Log.log(pickall);
		//initialize a starting region to the first region, if there is an error
		//we will at least have a region to pick
		Region startingRegion = pickable.get(0);
		
		Set<Integer> keys = multVector.keySet();
		Integer[] desired = keys.toArray(new Integer[keys.size()]);
		
		//search through all the pickable regions to see if they belong to 
		//a super region we want
		for(int index=0; index<desired.length; index++) {
			
			for(Region region : pickable) {
				//if the region is in the most desirable and available super region,
				//pick it
				if(region.getSuperRegion().getId() == desired[index]) {
				
					//return the region so it doesn't check all the regions available
					startingRegion = region;
					Log.log("picked region: " + startingRegion.getId() + " from super region: " + startingRegion.getSuperRegion().getId());
					return startingRegion;
				}
			}
		}
		
		//in case there is an error, return the first pickable region
		return pickable.get(0);
	}

	@Override
	/**
	 * This method is called for at first part of each round. This example puts two armies on random regions
	 * until he has no more armies left to place.
	 * @return The list of PlaceArmiesMoves for one round
	 */
	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state,
			Long timeOut) {

		ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
		String myName = state.getMyPlayerName();
		int armies = 2;
		int armiesLeft = state.getStartingArmies();
		LinkedList<Region> visibleRegions = state.getVisibleMap().getRegions();

		while (armiesLeft > 0) {
			double rand = Math.random();
			int r = (int) (rand * visibleRegions.size());
			Region region = visibleRegions.get(r);

			if (region.ownedByPlayer(myName)) {
				placeArmiesMoves
						.add(new PlaceArmiesMove(myName, region, armies));
				armiesLeft -= armies;
			}
		}

		return placeArmiesMoves;
	}

	@Override
	/**
	 * This method is called for at the second part of each round. This example attacks if a region has
	 * more than 6 armies on it, and transfers if it has less than 6 and a neighboring owned region.
	 * @return The list of PlaceArmiesMoves for one round
	 */
	public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state,
			Long timeOut) {
		ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
		String myName = state.getMyPlayerName();
		int armies = 5;
		int maxTransfers = 10;
		int transfers = 0;

		for (Region fromRegion : state.getVisibleMap().getRegions()) {
			// cycle through all our own regions
			if (fromRegion.ownedByPlayer(myName)) {

				ArrayList<Region> possibleToRegions = new ArrayList<Region>();
				possibleToRegions.addAll(fromRegion.getNeighbors());

				ArrayList<Region> viableEnemyCombatRegions = new ArrayList<Region>();

				int friendlies = fromRegion.getArmies();

				for (Region neighbor : possibleToRegions) {

					// if neighbor is an enemy
					if (!neighbor.ownedByPlayer("neutral")
							&& !neighbor.ownedByPlayer(myName)) {
						int enemies = neighbor.getArmies();
						double ratio = enemies / friendlies;

						// for future use
						if (ratio > 0.7) {

						}

						viableEnemyCombatRegions.add(neighbor);
					}
				}

				while (!possibleToRegions.isEmpty()) {
					double rand = Math.random();
					int r = (int) (rand * possibleToRegions.size());
					Region toRegion = possibleToRegions.get(r);

					if (!toRegion.getPlayerName().equals(myName)
							&& fromRegion.getArmies() > 6) // do an attack
					{
						attackTransferMoves.add(new AttackTransferMove(myName,
								fromRegion, toRegion, armies));
						break;
					} else if (toRegion.getPlayerName().equals(myName)
							&& fromRegion.getArmies() > 1
							&& transfers < maxTransfers) // do a transfer
					{
						attackTransferMoves.add(new AttackTransferMove(myName,
								fromRegion, toRegion, armies));
						transfers++;
						break;
					} else
						possibleToRegions.remove(toRegion);
				}
			}
		}

		return attackTransferMoves;
	}

	public static void main(String[] args) {
		new Log();
		Log.log("Game Begin");
		
		BotParser parser = new BotParser(new BotStarter());
		parser.run();

		Log.close();
	}

}
