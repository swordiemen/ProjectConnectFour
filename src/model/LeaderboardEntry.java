package model;

import java.util.Date;

public class LeaderboardEntry {
	private String name;
	private int score;
	private Date date;

	public LeaderboardEntry(String argName, int argScore, Date argDate) {
		setName(argName);
		setScore(argScore);
		setDate(argDate);
	}

	public String getName() {
		return name;
	}

	public void setName(String argName) {
		name = argName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int argScore) {
		score = argScore;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date argDate) {
		date = argDate;
	}
}
