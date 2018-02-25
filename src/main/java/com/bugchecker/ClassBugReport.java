package com.bugchecker;

import com.github.javaparser.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by elnggng on 2/19/18.
 */
public class ClassBugReport {

    private static Logger log = LoggerFactory.getLogger(ClassBugReport.class);

    private List<Bug> bugs = new LinkedList<Bug> ();
    private File javaFile;
    private long blockScanned = 0;

    BugCountsPerRule bugCountsPerRule = new BugCountsPerRule();

    public ClassBugReport(File javaFile) {
        this.javaFile = javaFile;
    }

    public void increaseBlockScanned () {blockScanned ++;}

    public void addBug(Bug bug) {
        bugs.add (bug);
        bugCountsPerRule.addCount(bug);
    }

    public long getNumBlockScanned() {return blockScanned;}

    public List<Bug> getBugs() {return bugs;}

    public BugCountsPerRule getBugCountsPerRule() { return bugCountsPerRule; }

    public File getJavaFile() {return javaFile;}

    public void generateClassReport_CSV (File reportFile) {
        try (PrintStream ps = new PrintStream(reportFile)) {
            //Print CSV titles
            ps.println ("Lines,Issue,Code Snippet");

            bugs.forEach( b -> {
                Optional<Range> range = b.getNode().getRange();
                if (range.isPresent()) {
                    ps.print(range.get().toString().replaceAll(",", ";"));
                }
                ps.print (",");
                ps.print (b.getRule().getName());
                ps.print (",");
                ps.print (b.getNode().toString().replaceAll ("\n", "\\n"));
                ps.println();
            });
        } catch (IOException e) {
            log.error ("Error when generating report to " + reportFile.toString(), e);
        }
    }
}
