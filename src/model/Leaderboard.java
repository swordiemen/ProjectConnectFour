package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Leaderboard {

	private List<LeaderboardEntry> entries;

	public Leaderboard(){
		entries = new ArrayList<LeaderboardEntry>();
	}

	public void addEntry(LeaderboardEntry entry){
		entries.add(entry);
		sortEntries();
	}

	//TODO: Maar 1 entry per keer wordt toegevoegd, dus bubble hoeft niet. Hoef alleen maar te zoeken waar deze hoort.
	public void sortEntries(){
		boolean sorted = false;
		int sortedAt = entries.size() - 1;
		while(!sorted){
			for(LeaderboardEntry lbe : entries){
				if(!(entries.indexOf(lbe) == entries.size() - 1)){
					int curIndex = entries.indexOf(lbe);
					LeaderboardEntry next = entries.get(curIndex + 1);
					if(lbe.getScore() < next.getScore()){
						LeaderboardEntry tempEntry = lbe;
						entries.set(curIndex, next);
						entries.set(curIndex + 1, tempEntry);
						System.out.println("GEWISSED: " + lbe.getName() + " MET " + next.getName());
					}
				}
			}
			sortedAt--;
			sorted = sortedAt < 1;
		}
	}

	public List<LeaderboardEntry> getEntries(){
		return entries;
	}

	public String getEntriesToString(){
		return entries.toString();
	}

	public static void main(String[] args){
		Leaderboard testLb = new Leaderboard();
		testLb.addEntry(new LeaderboardEntry("Henk1", 20, new Date(1, 1, 1, 1, 1, 1)));
		testLb.addEntry(new LeaderboardEntry("Kees1", 10, new Date(1, 1, 1, 1, 1, 1)));
		testLb.addEntry(new LeaderboardEntry("Piet1", 15, new Date(1, 1, 1, 1, 1, 1)));
		testLb.addEntry(new LeaderboardEntry("Henk2", 15, new Date(1, 1, 1, 1, 1, 1)));
		testLb.addEntry(new LeaderboardEntry("Kees2", 101, new Date(1, 1, 1, 1, 1, 1)));
		testLb.addEntry(new LeaderboardEntry("Piet2", 125, new Date(1, 1, 1, 1, 1, 1)));
		System.out.println("EERST");
		for(LeaderboardEntry le : testLb.getEntries()){
			System.out.println(le.getName() + ", " + le.getScore() + ", " + le.getDate().toString());
		}
		testLb.sortEntries();
		System.out.println("DAARNA");
		for(LeaderboardEntry le : testLb.getEntries()){
			System.out.println(le.getName() + ", " + le.getScore() + ", " + le.getDate().toString());
		}
	}
}
