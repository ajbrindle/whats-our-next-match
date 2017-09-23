package com.sk7software.nextmatch.model;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.sk7software.nextmatch.TestUtilities;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MatchTest {

    private List<Match> matches;

    @Before
    public void setup() throws Exception {
        matches = Match.createFromJSON(fetchJSON("matches.json"));
    }

    private JSONObject fetchJSON(String filename) throws IOException, JSONException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        return TestUtilities.fetchJSON(in);
    }

    @Test
    public void testGetMatches() {
        System.out.println(matches);
        assertEquals(7, matches.size());
    }

    @Test
    public void testSortMatches() {
        DateTime d1 = new DateTime(2000,1,1,0,0);
        for (Match m : matches) {
            assertTrue(m.getDate().isAfter(d1));
            d1 = m.getDate();
        }
    }

    @Test
    public void testNextMatch() {
        assertEquals("Marple Athletic Reds U14", matches.get(0).getTeam1());
    }

    @Test
    public void testOpponentTeam1() {
        assertEquals("Marple Athletic Reds U14", matches.get(0).getOpponent("BRAMHALL"));
    }

    @Test
    public void testOpponentTeam2() {
        assertEquals("FC Bluestar Comets U14", matches.get(1).getOpponent("BRAMHALL"));
    }
}
