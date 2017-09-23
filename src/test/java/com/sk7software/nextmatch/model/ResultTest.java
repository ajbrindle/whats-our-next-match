package com.sk7software.nextmatch.model;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.sk7software.nextmatch.TestUtilities;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultTest {

    private List<Result> results;

    @Before
    public void setup() throws Exception {
        results = Result.createFromJSON(fetchJSON("results.json"));
    }

    private JSONObject fetchJSON(String filename) throws IOException, JSONException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
        return TestUtilities.fetchJSON(in);
    }

    @Test
    public void testGetResults() {
        assertEquals(3, results.size());
    }

    @Test
    public void testSortResults() {
        DateTime d1 = new DateTime(9999, 12, 31, 0, 0);
        for(Result r : results) {
            assertTrue(r.getDate().isBefore(d1));
            d1 = r.getDate();
        }
    }

    @Test
    public void testResult() {
        Result r = results.get(2);
        assertEquals("Chelsea", r.getTeam1());
        assertEquals(1, r.getScore1());
        assertEquals("Bramhall Juniors U14", r.getTeam2());
        assertEquals(2, r.getScore2());
    }
}
