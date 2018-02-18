package com.soen691.aspirator;

import com.bugchecker.Bug;
import com.bugchecker.BugChecker;
import com.bugchecker.BugCheckerTest;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by elnggng on 2/18/18.
 */
public class Test1 {

    @Test
    public void testFileChecker() {
        File file = new File(BugCheckerTest.class.getResource("test1.java").getFile());

        List<Bug> bugs = BugChecker.getInstance().checkAllBugs(file);

        try {
            int a = 2;
        } catch (IOException | FileNotFoundException e) {
            int i = 0;
            // TODO: handle properly
            System.out.println ("Wrong");
        }

        try {
            int a = 2;
        } catch (IOException e) {
            //TODO
        }
    }
}
