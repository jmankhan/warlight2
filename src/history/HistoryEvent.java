package history;

import java.util.ArrayList;

import map.Region;
import move.AttackTransferMove;
import move.Move;
import move.PlaceArmiesMove;
import bot.BotState;
import debug.Log;

/**
 * A single event node that occurred in the game. It should be created as close to 
 * the event it represents as possible to get the most accurate details and properties
 * @author jalal
 *
 */
public class HistoryEvent {

	private AttackTransferMove atmove;
	private PlaceArmiesMove pamove;
	private BotState state;
	
	private boolean isATMove;
	
	/**
	 * Create a new HistoryEvent node with the data from an AttackTransferMove
	 * @param atmove
	 */
	public HistoryEvent(BotState state, Move move) {
		
		//store proper move and activate flag as needed
		if(move instanceof AttackTransferMove) {
			storeATMoveData((AttackTransferMove) move);
		}
		else if(move instanceof PlaceArmiesMove) {
			storePAMoveData((PlaceArmiesMove) move);
		}
		
		//if this somehow happens just exit out
		else {
			Log.log("Invalid move passed to HistoryTracker");
			return;
		}
	}

	/**
	 * Stores data from the move into our own variables
	 * @param move
	 */
	private void storeATMoveData(AttackTransferMove move) {
		//store event info
		this.atmove = move;
		isATMove = true;
		
	}
	
	/**
	 * Stores data from the move into our own variables
	 * @param move
	 */
	private void storePAMoveData(PlaceArmiesMove move) {
		//store event info
		isATMove = false;
		this.pamove = move;
	}
	
	/**
	 * The amount of armies handles in this move
	 * @return int armies
	 */
	public int getArmies() {
		return isATMove() ? atmove.getArmies() : pamove.getArmies(); 
	}
	
	/**
	 * The name of the player that made this move
	 * @return String playerName
	 */
	public String getPlayerName() {
		return isATMove() ? atmove.getPlayerName() : pamove.getPlayerName();
	}
	
	/**
	 * The region that this move originated from
	 * Same as toRegion if this was a PlaceArmiesMove
	 * @return Region fromRegion
	 */
	public Region getFromRegion() {
		return isATMove() ? atmove.getFromRegion() : pamove.getRegion();
	}
	
	/**
	 * The region that this move finished at
	 * Same as fromRegion if this was a PlaceArmiesMove
	 * @return Region toRegion
	 */
	public Region getToRegion() {
		return isATMove() ? atmove.getToRegion() : pamove.getRegion();
	}
	
	/**
	 * Returns true if this move was illegal
	 * @return boolean illegalMove
	 */
	public boolean isIllegal() {
		return isATMove() ? atmove.getIllegalMove().isEmpty() : pamove.getIllegalMove().isEmpty();
	}
	
	/**
	 * Returns true if this is an AttackTransferMove, false if it is a PlaceArmiesMove
	 * @return boolean isATMove
	 */
	public boolean isATMove() {
		return this.isATMove;
	}	

	/**
	 * Get regions available for picking in Picking Phase
	 * @return ArrayList<Region>
	 */
	public ArrayList<Region> getPickableRegions() {
		return state.getPickableStartingRegions();
	}
	
	/**
	 * Returns the round this event occurred on
	 * @return
	 */
	public int getRound() {
		return state.getRoundNumber();
	}
	
	@Override
	public String toString() {
		String playerName = this.getPlayerName();
		String moveType = isATMove() ? "attack/transferred " : "placed ";
		String round = "Round: " + getRound();
		String armies = getArmies() + " armies";
		String region = " from Region " + this.getFromRegion() + " to Region " + this.getToRegion();
		
		return "[HistoryEvent] " + round + playerName + moveType + armies + region;
	}
}
