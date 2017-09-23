package com.sk7software.nextmatch.model;

import com.sk7software.nextmatch.util.DateUtil;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateUtilTest {

    @Test
    public void testToday() {
        DateTime d1 = new DateTime().withTimeAtStartOfDay();
        assertEquals(DateUtil.calcNumberOfDays(d1), 0);
        assertEquals(DateUtil.getDayDescription(d1, false), "today");
    }

    @Test
    public void testTomorrow() {
        DateTime d1 = new DateTime().withTimeAtStartOfDay().plusDays(1);
        assertEquals(DateUtil.calcNumberOfDays(d1), 1);
        assertEquals(DateUtil.getDayDescription(d1, false), "tomorrow");
    }

    @Test
    public void test4Days() {
        DateTime d1 = new DateTime().withTimeAtStartOfDay().plusDays(4);
        assertEquals(DateUtil.calcNumberOfDays(d1), 4);
        assertTrue(DateUtil.getDayDescription(d1, true).indexOf("in 4 days") >= 0);
        assertFalse(DateUtil.getDayDescription(d1, false).indexOf("in 4 days") >= 0);
    }

    @Test
    public void testTimeMorning() {
        DateTime d1 = new DateTime(2017, 10, 15, 9, 0);
        assertEquals("09:00 AM", DateUtil.getTimeDescription(d1));
    }

    @Test
    public void testTimeAfternoon() {
        DateTime d1 = new DateTime(2017, 10, 15, 15, 30);
        assertEquals("03:30 PM", DateUtil.getTimeDescription(d1));
    }

}
