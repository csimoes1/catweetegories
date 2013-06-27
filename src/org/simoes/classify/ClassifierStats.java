package org.simoes.classify;

import java.util.TreeMap;

public class ClassifierStats {
	private String scoringTable;
	private TreeMap<Category, Double> categoryScores;
	
	
	public String getScoringTable() {
		return scoringTable;
	}
	public void setScoringTable(String scoringTable) {
		this.scoringTable = scoringTable;
	}
	public TreeMap<Category, Double> getCategoryScores() {
		return categoryScores;
	}
	public void setCategoryScores(TreeMap<Category, Double> categoryScores) {
		this.categoryScores = categoryScores;
	}
	
	
}
