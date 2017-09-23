package com.sk7software.nextmatch.model;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class Match {
    private String team1;
    private String team2;
    private String venue;

    @JsonDeserialize(using = JSONDateAdapter.class)
    private DateTime date;

    public Match() {}

    public static List<Match> createFromJSON(JSONObject response) throws IOException, JSONException {
        Match match;

        JSONArray responseArray = response.getJSONArray("matches");
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Match> matches = mapper.readValue(responseArray.toString(), new TypeReference<List<Match>>(){});
        matches.sort(Comparator.comparing(Match::getDate));

        return matches;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public boolean isMatchOnDate(DateTime matchDate) {
        if (getDate().withTimeAtStartOfDay().equals(matchDate.withTimeAtStartOfDay())) {
            return true;
        }
        return false;
    }

    public String getOpponent(String us) {
        if (team1.toUpperCase().indexOf(us) < 0) {
            return team1;
        } else {
            return team2;
        }
    }
}
