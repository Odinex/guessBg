package com.kp.guessbg.models;

/**
 * Created by KO on 20-Aug-19.
 */

public class Team {
    private int id;
    private String name;
    private int points;
    private int wins;
    private int losses;

    public Team(int id, String name) {
        this.id = id;
        this.name = name;
        this.points = 0;
        this.wins = 0;
        this.losses = 0;
    }
    public Team(String teamToString) {
        int idStart = teamToString.indexOf("id=") + 3;
        int idEnd = teamToString.indexOf(",", idStart);
        this.id = Integer.parseInt(teamToString.substring(idStart, idEnd));
        int beginName = idEnd + 8;
        int endName = teamToString.indexOf("\'", beginName);
        this.name = teamToString.substring(beginName, endName);
        int startPoints = endName + 10;
        int endPoints = teamToString.indexOf(",", startPoints);
        this.points = Integer.parseInt(teamToString.substring(
                startPoints, endPoints));
        int beginWins = teamToString.indexOf(endPoints + 7);
        int endWins = teamToString.indexOf(",", beginWins);
        this.wins = Integer.parseInt(teamToString.substring(beginWins, endWins));
        int beginLosses = endWins + 9;
        int endLosses = teamToString.indexOf("}", beginLosses);
        this.losses = Integer.parseInt(teamToString.substring(beginLosses, endLosses));
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", points=" + points +
                ", wins=" + wins +
                ", losses=" + losses +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }



}
