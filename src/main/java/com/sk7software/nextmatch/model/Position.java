package com.sk7software.nextmatch.model;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class Position {
    private int pos;
    private String team;
    private String p;
    private String pts;

    public Position() {}

    public static List<Position> createFromJSON(JSONObject response) throws IOException, JSONException {
        Position position;

        JSONArray responseArray = response.getJSONArray("table");
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Position> league = mapper.readValue(responseArray.toString(), new TypeReference<List<Position>>(){});
        league.sort(Comparator.comparing(Position::getPos));

        return league;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getPts() {
        return pts;
    }

    public void setPts(String pts) {
        this.pts = pts;
    }

    public String getSpokenText() {
        return pos + ", " + team + ", played " + p + ". points " + pts;
    }
}
