package com.sk7software.nextmatch.model;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BinTest {

    @Test
    public void testCollectedOnDate() {
        Match b = new Match();
        b.setColour("blue");
        b.setDate(new DateTime(2017, 5, 30, 0, 0));
        assertTrue(b.isCollectedOnDate(new DateTime(2017,5,30,10,30)));
        assertFalse(b.isCollectedOnDate(new DateTime(2017, 5, 29, 10, 0)));
    }

    @Test
    public void testBinList1Bin() {
        List<Match> bl = new ArrayList<>();
        bl.add(new Match("blue", new DateTime()));

        assertEquals(Match.getSpokenBinList(bl), "The blue bin");
    }

    @Test
    public void testBinList2Bins() {
        List<Match> bl = new ArrayList<>();
        bl.add(new Match("blue", new DateTime()));
        bl.add(new Match("red", new DateTime()));

        assertEquals(Match.getSpokenBinList(bl), "The blue and red bins");
    }

    @Test
    public void testBinList3Bins() {
        List<Match> bl = new ArrayList<>();
        bl.add(new Match("blue", new DateTime()));
        bl.add(new Match("red", new DateTime()));
        bl.add(new Match("yellow", new DateTime()));

        assertEquals(Match.getSpokenBinList(bl), "The blue, red and yellow bins");
    }

    @Test
    public void testBinList4Bins() {
        List<Match> bl = new ArrayList<>();
        bl.add(new Match("blue", new DateTime()));
        bl.add(new Match("red", new DateTime()));
        bl.add(new Match("yellow", new DateTime()));
        bl.add(new Match("green", new DateTime()));

        assertEquals(Match.getSpokenBinList(bl), "The blue, red, yellow and green bins");
    }

}
