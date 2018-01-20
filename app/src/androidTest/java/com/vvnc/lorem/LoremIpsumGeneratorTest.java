package com.vvnc.lorem;

import android.graphics.Paint;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LoremIpsumGeneratorTest {
    @Test
    public void firstWordCapitalizedTest(){
        LoremIpsumGenerator.reset();
        String generated = LoremIpsumGenerator.getNext(15,
                Capitalization.FIRST_WORD);
        String expected = "Lorem ipsum dolor sit amet consectetur adipiscing elit sed do " +
                "eiusmod tempor incididunt ut labore";
        assertEquals(expected, generated);
    }

    @Test
    public void uppercaseTet(){
        LoremIpsumGenerator.reset();
        String generated = LoremIpsumGenerator.getNext(70,
                Capitalization.UPPERCASE);
        // All 69 "lorem impsum ..." words and "lorem" one more time:
        String expected = "LOREM IPSUM DOLOR SIT AMET CONSECTETUR ADIPISCING ELIT SED DO EIUSMOD " +
                "TEMPOR INCIDIDUNT UT LABORE ET DOLORE MAGNA ALIQUA UT ENIM AD MINIM VENIAM QUIS " +
                "NOSTRUD EXERCITATION ULLAMCO LABORIS NISI UT ALIQUIP EX EA COMMODO CONSEQUAT " +
                "DUIS AUTE IRURE DOLOR IN REPREHENDERIT IN VOLUPTATE VELIT ESSE CILLUM DOLORE " +
                "EU FUGIAT NULLA PARIATUR EXCEPTEUR SINT OCCAECAT CUPIDATAT NON PROIDENT SUNT IN " +
                "CULPA QUI OFFICIA DESERUNT MOLLIT ANIM ID EST LABORUM " +
                "LOREM";
        assertEquals(expected, generated);
        generated = LoremIpsumGenerator.getNext(1, Capitalization.UPPERCASE);
        expected = "IPSUM";
        assertEquals(expected, generated);
    }

    @Test
    public void capitalizeEachTet(){
        LoremIpsumGenerator.reset();
        String generated = LoremIpsumGenerator.getNext(8, Capitalization.EACH_WORD);
        String expected = "Lorem Ipsum Dolor Sit Amet Consectetur Adipiscing Elit";
        assertEquals(expected, generated);
    }


    @Test
    public void loopTest() {
        LoremIpsumGenerator.reset();
        String generated = LoremIpsumGenerator.getNext(69 * 15 + 7, Capitalization.NONE);
        String[] words = generated.split("\\s");
        assertEquals("amet", words[4]);
        assertEquals("amet", words[69 + 4]);
        assertEquals("amet", words[69 * 7 + 4]);
        assertEquals("amet", words[69 * 15 + 4]);

        Random rand = new Random();
        for(int base = 0; base < 15; base++) {
            int offset = rand.nextInt(69);
            assertEquals(words[69 * base + offset], words[69 * base + offset]);
        }
    }

    @Test(expected = LoremException.class)
    public void unknownCapitalizationTest() {
        LoremIpsumGenerator.getNext(14, Capitalization.UNKNOWN);
    }
}
