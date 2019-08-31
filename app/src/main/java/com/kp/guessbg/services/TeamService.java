package com.kp.guessbg.services;

import android.content.Context;
import android.util.Log;

import com.kp.guessbg.models.Team;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KO on 22-Aug-19.
 */

public class TeamService {
    private static final int MAX_POINTS = 10;
    private static List<Team> currentTeams = new ArrayList<>();


    public static List<Team> getCurrentTeams() {
        return currentTeams;
    }

    public static  void setAsCurrentGuesser(int index) {
        Team team = currentTeams.get(index);
        team.setIsHisTurn(true);
    }

    public static void addTeam(Team team) {
        currentTeams.add(team);
    }

    public static void saveTeamsToFile(Context context) {
        if (!currentTeams.isEmpty()) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("teams.txt", Context.MODE_PRIVATE));

                for (Team team : currentTeams) {
                    outputStreamWriter.write(team.toString());
                    outputStreamWriter.write('\n');
                }

                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getWinsInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Победи:\n");
        for(Team team : currentTeams) {
            info.append(team.getName()).append(":")
                    .append('\t').append(team.getWins()).append('\n');
        }
        return info.toString();
    }

    public static String getPointsInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Точки:\n");
        for(Team team : currentTeams) {
            info.append(team.getName()).append(":")
                    .append('\t').append(team.getPoints()).append('\n');
        }
        return info.toString();
    }

    public static void loadTeams(Context context) {

        try {
            InputStream inputStream = context.openFileInput("teams.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                TeamService.clearTeams();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    Team team = new Team(receiveString);
                    currentTeams.add(team);
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    public static boolean hasWon(int currentIndex, int points) {

        Team team = currentTeams.get(currentIndex);
        team.addPoints(points);
        boolean hasWon = team.getPoints() >= MAX_POINTS;
        if(hasWon) {
            team.addWin();
            for(Team team1 : currentTeams) {
                team1.setPoints(0);
                team1.setIsHisTurn(false);
            }
            currentTeams.get(0).setIsHisTurn(true);
        }
        return hasWon;
    }

    public static Team getCurrentTeam() {

        for(Team team : currentTeams) {
            if(team.isHisTurn()) {
                return team;
            }
        }
        Team team = currentTeams.get(0);
        team.setIsHisTurn(true);
        return team;
    }

    public static Team getNextTeam(int currentIndex) {
        Team team1 = currentTeams.get(currentIndex);
        team1.setIsHisTurn(false);
        currentTeams.set(currentIndex,team1);
        Team team;
        currentIndex++;
        if(currentIndex < currentTeams.size()) {
            team = currentTeams.get(currentIndex);
        } else {
            team = currentTeams.get(0);
        }
        team.setIsHisTurn(true);
        return team;
    }

    public static void clearTeams() {
        currentTeams.clear();
    }
}
