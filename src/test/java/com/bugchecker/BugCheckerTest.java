package com.bugchecker;

import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by elnggng on 2/18/18.
 */
public class BugCheckerTest {

    @Test
    public void testFileChecker() {
        File file = new File(BugCheckerTest.class.getClassLoader().
                getResource("Test1.java").getFile());

        List<Bug> bugs = BugChecker.getInstance().checkAllBugs(file);

        assert bugs.size() == 1;
    }
}
