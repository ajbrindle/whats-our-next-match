package com.sk7software.nextmatch.model;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Result {
    @JsonDeserialize(using = JSONDateAdapter.class)
    private DateTime date;
    private String team1;
    private String team2;
    private String score;

    private int score1;
    private int score2;

    public Result() {}

    public static List<Result> createFromJSON(JSONObject response) throws IOException, JSONException {
        Result result;

        JSONArray responseArray = response.getJSONArray("results");
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Result> results = mapper.readValue(responseArray.toString(), new TypeReference<List<Result>>(){});
        results.sort(Comparator.comparing(Result::getDate).reversed());
        results = setScores(results);

        return results;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    private static List<Result> setScores(List<Result> results) {
        List<Result> filteredResults = new ArrayList<>();
        Iterator<Result> i = results.iterator();
        while(i.hasNext()) {
            Result r = i.next();
            String[] scores = r.getScore().split("-");

            try {
                r.setScore1(Integer.parseInt(scores[0].trim()));
                r.setScore2(Integer.parseInt(scores[1].trim()));
                filteredResults.add(r);
            } catch (NumberFormatException nfe) {
                // Not a valid score
                i.remove();
            }
        }

        return filteredResults;
    }
}
