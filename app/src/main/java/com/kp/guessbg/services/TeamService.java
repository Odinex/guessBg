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
    private static List<Team> currentTeams = new ArrayList<>();

    public TeamService() {

    }

    public List<Team> getCurrentTeams() {
        return currentTeams;
    }

    public int getCountOfTeams() {
        return currentTeams.size();
    }
    public void setAsCurrentGuesser(int index) {
        Team team = currentTeams.get(index);
        team.setIsHisTurn(true);
    }

    public void updatePoints(int index, int points) {
        Team team = currentTeams.get(index);
        team.setPoints(team.getPoints()+points);
    }

    public void addWin(int index) {
        Team team = currentTeams.get(index);
        team.setWins(team.getWins()+1);
    }

    public void addTeam(Team team) {
        currentTeams.add(team);
    }

    public void saveTeamsToFile(Context context) {
        if (!currentTeams.isEmpty()) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("teams.txt", Context.MODE_PRIVATE));

                for (Team team : currentTeams) outputStreamWriter.write(team.toString());

                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadTeams(Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("teams.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

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
}
