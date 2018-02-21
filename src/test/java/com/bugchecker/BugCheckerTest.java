package com.bugchecker;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by elnggng on 2/18/18.
 */
public class BugCheckerTest {

    @Test
    public void testFileCheckerEmptyCatch() {
        File file = new File(BugCheckerTest.class.getClassLoader().
                getResource("Test1.java").getFile());

        ClassBugReport report = BugChecker.getInstance().checkAllBugs(file);

        assert report.getBugs().size() == 1;
        assert report.getNumBlockScanned() == 2;
    }

    @Test
    public void testFileCheckerOverCatch() {
        File file = new File(BugCheckerTest.class.getClassLoader().
                getResource("Test2.java").getFile());

        ClassBugReport report = BugChecker.getInstance().checkAllBugs(file);

        assert report.getBugs().size() == 3;
        assert report.getNumBlockScanned() == 3;
    }

    @Test
    public void testFileCheckerTODOCatch() {
        File file = new File(BugCheckerTest.class.getClassLoader().
                getResource("Test3.java").getFile());

        ClassBugReport report = BugChecker.getInstance().checkAllBugs(file);

        assert report.getBugs().size() == 2;
        assert report.getNumBlockScanned() == 2;
    }

    @Test
    public void testFileCheckerFolder () {
        File folder = new File(BugCheckerTest.class.getClassLoader().
                getResource("Test3.java").getFile()).getParentFile();

        ProjectBugReport projectReport = BugChecker.getInstance().checkAllBugsInProject(folder, null);

        assert projectReport.getClassReports().size() == 4;
        assert projectReport.getNumBugs() == 8;
        assert projectReport.getNumBlockScanned() == 9;
    }

    @Test
    public void testClassBugReportCSV () {
        File file = new File(BugCheckerTest.class.getClassLoader().
                getResource("Test2.java").getFile());

        ClassBugReport report = BugChecker.getInstance().checkAllBugs(file);

        report.generateClassReport_CSV(new File("Test2.csv"));

        final List <String> lines = new ArrayList<String>();

        try (Stream<String> sLines = Files.lines(new File("Test2.csv").toPath())) {
            sLines.forEach (line -> lines.add (line) );
        } catch (IOException e) {
            assert false;
        }

        assert lines.size() == 4;
        assert lines.get(0).equals("Lines,Issue,Code Snippet");
    }

    @Test
    public void testProjectBugReportCSV () {
        File folder = new File(BugCheckerTest.class.getClassLoader().
                getResource("Test3.java").getFile()).getParentFile();

        ProjectBugReport projectReport = BugChecker.getInstance().checkAllBugsInProject(folder, null);

        assert projectReport.getClassReports().size() == 4;
        assert projectReport.getNumBugs() == 8;
        assert projectReport.getNumBlockScanned() == 9;

        projectReport.generateSummaryReport_CSV(new File("project.csv"));

        final List <String> lines = new ArrayList<String>();

        try (Stream<String> sLines = Files.lines(new File("project.csv").toPath())) {
            sLines.forEach (line -> lines.add (line) );
        } catch (IOException e) {
            assert false;
        }

        assert lines.size() == 5;
        assert lines.get(0).equals("JavaSrc,BlockScanned,Issues");
    }
}
