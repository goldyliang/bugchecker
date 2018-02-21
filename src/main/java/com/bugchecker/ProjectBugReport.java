package com.bugchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by elnggng on 2/19/18.
 */
public class ProjectBugReport {

    private static Logger log = LoggerFactory.getLogger(ProjectBugReport.class);

    private List<ClassBugReport> classReports = new LinkedList<ClassBugReport> ();

    private File projectFolder;
    private File reportFolder;
    private boolean reportFolderInitialized = false;

    private long blockScanned = 0;

    private long bugsFound = 0;

    public ProjectBugReport(File projectFolder, File reportFolder) {
        this.projectFolder = projectFolder;
        this.reportFolder = reportFolder;
    }

    public void increaseBlockScanned () {blockScanned ++;}

    public void addClassReport(ClassBugReport report) {
        classReports.add (report);
        blockScanned += report.getNumBlockScanned();
        bugsFound += report.getBugs().size();

        if (reportFolder != null && !reportFolderInitialized) {
            createReportFolder();
            reportFolderInitialized = true;
        }

        if (reportFolder != null) {
            try {
                generateClassReport_CSV (report);
            } catch (IOException e) {
                log.error("Exception when adding class report for ");
            }
        }
    }

    public void completeReport () {
        generateSummaryReport_CSV (new File(reportFolder, "summary.csv"));
    }

    public long getNumBugs() { return bugsFound;}

    public long getNumBlockScanned() {return blockScanned;}

    public List<ClassBugReport> getClassReports() {return classReports;}

    public void generateSummaryReport_CSV(File reportFile) {
        try (PrintStream ps = new PrintStream(reportFile)) {
            //Print CSV titles
            ps.println ("JavaSrc,BlockScanned,Issues");

            classReports.forEach( report -> {
                ps.print (report.getJavaFile().toString());
                ps.print (",");
                ps.print (report.getNumBlockScanned());
                ps.print (",");
                ps.print (report.getBugs().size());
                ps.println();
            });
        } catch (IOException e) {
            log.error ("Error when generating report to " + reportFile.toString(), e);
        }
    }

    private void createReportFolder() {
        try {
            if (reportFolder.exists()) {
                throw new IOException("Target folder already exists: " + reportFolder.toString());
            }

            if (!reportFolder.mkdir()) {
                throw new IOException("Dir not created: " + reportFolder.toString());
            }

            if (!new File(reportFolder, "classes").mkdir()) {
                throw new IOException("Dir not created: " + reportFolder.toString() + "/classes");
            }
        } catch (IOException e) {
            log.error ("Error when creating report folder: " + reportFolder.toString(), e);
            System.exit(1);
        }
    }

    private void generateClassReport_CSV (ClassBugReport report) throws IOException {
        URI srcFolderURI = projectFolder.toURI();

        URI srcFileURI = report.getJavaFile().toURI();
        String relative = srcFolderURI.relativize(srcFileURI).getPath();

        File folder = new File(reportFolder, "classes/" + relative).getParentFile();
        if (!folder.isDirectory() && !new File(reportFolder, "classes/" + relative).getParentFile().mkdirs()) {
            throw new IOException("Dirs not created for: " + reportFolder + "/classes/" + relative);
        }

        report.generateClassReport_CSV(
                new File(reportFolder, "classes/" + relative + ".csv"));
    }

    public void generateReportBundle_CSV(File reportFolder) {
        try {
            this.reportFolder = reportFolder;
            this.reportFolderInitialized = false;

            createReportFolder();
            for (ClassBugReport classBugReport : getClassReports()) {
                generateClassReport_CSV (classBugReport);
            }
            completeReport();
        } catch (IOException e) {
            log.error("IO Exception when generating project bug report.", e);
            System.exit(1);
        }
    }
}
