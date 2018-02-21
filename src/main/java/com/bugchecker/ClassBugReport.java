package com.bugchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by elnggng on 2/19/18.
 */
public class ClassBugReport {

    private static Logger log = LoggerFactory.getLogger(ClassBugReport.class);

    private List<Bug> bugs = new LinkedList<Bug> ();
    private File javaFile;
    private long blockScanned = 0;

    public ClassBugReport(File javaFile) {
        this.javaFile = javaFile;
    }

    public void increaseBlockScanned () {blockScanned ++;}

    public void addBug(Bug bug) {
        bugs.add (bug);
    }

    public long getNumBlockScanned() {return blockScanned;}

    public List<Bug> getBugs() {return bugs;}

    public File getJavaFile() {return javaFile;}

    public void generateClassReport_CSV (File reportFile) {
        try (PrintStream ps = new PrintStream(reportFile)) {
            //Print CSV titles
            ps.println ("Lines,Issue,Code Snippet");

            bugs.forEach( b -> {
                ps.print (b.getNode().getRange().get().toString());
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
