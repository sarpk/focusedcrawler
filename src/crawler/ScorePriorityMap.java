package crawler;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.google.common.collect.Ordering;

public class ScorePriorityMap {
	private LinkedHashMap<String, Boolean> linksMap;//Boolean for visited indication
	private TreeMap<String, Double> linksOrdered;
	
	public ScorePriorityMap() {
		linksMap = new LinkedHashMap<String, Boolean>();
		linksOrdered = new ValueComparableMap<String, Double>(Ordering.natural());
	}
	
	public void addAddress(String address, Double score) {
		if (addressShouldBeAdded(address)) {//address has not added before
			addAddressToMaps(address, score);
		}
		else if (scoreShouldBeUpdated(address)) {//address is added before needs score updating
			Double newScore = linksOrdered.get(address) + score;
			linksOrdered.put(address, newScore);
		}
	}
	
	public String getHighestScoreAddress() {
		String highest = linksOrdered.lastKey();
		visitAddress(highest);
		return highest;
	}
	
	private void visitAddress(String address) {
		if (address == null) {
			return;
		}
		linksOrdered.remove(address);
		linksMap.put(address, true);
	}

	private boolean scoreShouldBeUpdated(String address) {
		Boolean addrB = linksMap.get(address);
		if (addrB == null) {//address doesn't exist can add
			return false;
		}
		else if (addrB.equals(false)) {//Page is not visited so should be updated
			return true;
		}
		return false;
	}
	
	private boolean addressShouldBeAdded(String address) {
		Boolean addrB = linksMap.get(address);
		if (addrB == null) {//address doesn't exist can add
			return true;
		}
		return false;
	}
	
	private void addAddressToMaps(String address, Double score) {
		linksMap.put(address, false);
		linksOrdered.put(address, score);
	}
	
}
