package com.github.runly.riforum_android;

import com.github.runly.riforum_android.utils.PlateHeaderNumUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void plateNumberTest() throws Exception {
        for (int i = 0; i < 100; i ++) {
            int y = PlateHeaderNumUtil.getPlateHeaderNumber(i);
            System.out.println(i + " : " + y);
        }
    }

    @Test
    public void getInsertIndexTest() throws Exception {
        for (int i = 1; i < 8; i ++) {
            int y = PlateHeaderNumUtil.getInsertIndex(i);
            System.out.println(i + " : " + y);
        }
    }
}