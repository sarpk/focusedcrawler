package crawler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import com.google.common.collect.Lists;
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
			//System.out.println(score);
			//System.out.println(linksOrdered.get(address));
			Double newScore = linksOrdered.get(address) + score;
			linksOrdered.put(address, newScore);
		}
	}
	
	public List<String> getHighestScoreAddressesWithoutPopping() {
		return Lists.reverse(Lists.newArrayList(linksOrdered.keySet()));
	}
	
	/**
	 * 
	 * @return the amount of links added
	 */
	public Integer getSize() {
		return linksOrdered.size();
	}
	
	/**
	 * @param nth lowest score
	 * @return the minimum nth score
	 * @special case, if there aren't n elements then it returns the highest
	 * @special case, if there aren't any elements then it returns the minimum double value
	 */
	public Double getMinScore(int n) {
		Double minScore = Double.MIN_VALUE;
		for (Double score : linksOrdered.values()) {
			if ( --n == 0 ) { break; }
			minScore = score;
		}
		return minScore;
	}
	
	/**
	 * @param nth largest score
	 * @return the max nth score
	 * @special case, if there aren't n elements then it returns the  minimum double value
	 * @special case, if there aren't any elements then it returns the minimum double value
	 */
	public Double getMaxScore(int n) {
		Double maxScore = Double.MIN_VALUE;
		for (Double score : Lists.reverse(Lists.newArrayList(linksOrdered.values()))) {
			if ( --n == 0 ) { break; }
			maxScore = score;
		}
		if (n != 0) {maxScore = Double.MIN_VALUE;}
		return maxScore;
	}
	
	public String getHighestScoreAddress() {
		String highest = null;
		try {
			highest = linksOrdered.lastKey();
		}
		catch(Exception ex) {
		}
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
