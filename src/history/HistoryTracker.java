package history;

import java.util.ArrayList;

/**
 * An in game log of all known events that occurred per round from both the enemies
 * and the allies. It will collect data but not perform calculations except very basic ones
 * to reduce response time of the bot
 * @author jalal
 *
 */
public class HistoryTracker {
	
	private ArrayList<HistoryEvent> history;
	
	public HistoryTracker() {
		history = new ArrayList<HistoryEvent>();
	}
	
	/**
	 * Get the number of indices on the timeline
	 * @return
	 */
	public int size() {
		return history.size();
	}
	
	/**
	 * Add an event to the end of the timeline
	 * @param event
	 */
	public void add(HistoryEvent event) {
		history.add(event);
	}
	
	/**
	 * Add an event at the given index on the timeline
	 * @param index
	 * @param event
	 */
	public void add(int index, HistoryEvent event) {
		history.add(index, event);
	}

	/**
	 * Returns the full list of accumulated events this game
	 * @return ArrayList<HistoryEvent>
	 */
	public ArrayList<HistoryEvent> getAll() {
		return history;
	}
	/**
	 * Get an event at a given index in the timeline
	 * @param index
	 * @return
	 */
	public HistoryEvent get(int index) {
		return history.get(index);
	}
	
	/**
	 * Get all events that occurred in a given round
	 * @param round
	 * @return
	 */
	public ArrayList<HistoryEvent> getEventsByRound(int round) {
		ArrayList<HistoryEvent> roundEvents = new ArrayList<HistoryEvent>();
		for(HistoryEvent e : history) {
			if(e.getRound() == round) {
				roundEvents.add(e);
			}
		}
		return roundEvents;
	}
	
	/**
	 * Get all events done by a specified player in the whole game
	 * @param playerName
	 * @return
	 */
	public ArrayList<HistoryEvent> getAllPlayerEvents(String playerName) {
		ArrayList<HistoryEvent> playerEvents = new ArrayList<HistoryEvent>();
		for(HistoryEvent e : history) {
			if(e.getPlayerName().equals(playerName)) {
				playerEvents.add(e);
			}
		}
		return playerEvents;
		
	}
	
}
