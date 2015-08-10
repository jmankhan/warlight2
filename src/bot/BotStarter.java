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

import history.HistoryEvent;
import internet.MapDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import map.MapMatrix;
import map.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;
import debug.Log;

public class BotStarter implements Bot {
	
	private ArrayList<Region> myStartingPickedRegions; //list of regions that this bot has chosen

	public BotStarter() {
		super();
		myStartingPickedRegions = new ArrayList<Region>();
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
		double rand = Math.random();
		int r = (int) (rand * state.getPickableStartingRegions().size());
		int regionId = state.getPickableStartingRegions().get(r).getId();
		Region startingRegion = state.getFullMap().getRegion(regionId);
		myStartingPickedRegions.add(startingRegion);
		
		//create history event of us picking a region
		PlaceArmiesMove move = new PlaceArmiesMove(state.getMyPlayerName(),
				startingRegion, 0);
		HistoryEvent event = new HistoryEvent(state, move);
		BotParser.tracker.add(event);
		//end event

		
		return startingRegion;
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
		
		try {
			String url = "http://theaigames.com/competitions/warlight-ai-challenge-2/games/55c7cd1335ec1d4702e53367/map";
			new MapMatrix(new BotState(), MapDownloader.createMap(MapDownloader.getMap(url)));
			
		} catch (IOException e) {e.printStackTrace();}
		
		BotParser parser = new BotParser(new BotStarter());
		parser.run();

		Log.close();
	}

}
