package com.bugchecker;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by goldyliang on 2/17/18.
 */
public class BugChecker {

    private static BugChecker ourInstance = new BugChecker();

    public static BugChecker getInstance() {
        return ourInstance;
    }

    private static Logger log = LoggerFactory.getLogger(BugChecker.class);

    private static List<AbstractRule> bugRules;

    static {
        Reflections reflections = new Reflections("com.bugchecker.rules");

        Set<Class<? extends AbstractRule>> all = reflections.getSubTypesOf(AbstractRule.class);

        bugRules = new ArrayList<AbstractRule>();

        for (Class<? extends AbstractRule> ruleClass : all) {
            try {
                Type type =  ((ParameterizedType)ruleClass.getGenericSuperclass()).getActualTypeArguments()[0];

                if (! (type instanceof Class)) {
                    throw new IllegalArgumentException ("Class " + ruleClass + " not valid");
                }

                @SuppressWarnings("unchecked")
                Class<? extends Node > nodeClass = (Class<? extends Node>) type;

                AbstractRule matcher = ruleClass.newInstance();

                matcher.setNodeClass(nodeClass);
                bugRules.add(matcher);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error ("Error constructing bug rules and ignored: " + ruleClass.toString());
            }
        }
    }

    public void checkAllBugs (Node node, ClassBugReport report) {
        boolean anyRuleInterested = false;

        for (AbstractRule matcher: bugRules) {
            if (matcher.isInterested(node)) {
                anyRuleInterested = true;
                Bug bug = matcher.match(node);
                if (bug != null) {
                    report.addBug(bug);
                }
            }
        }

        if (anyRuleInterested) {
            report.increaseBlockScanned();
        }
    }

    public ClassBugReport checkAllBugs (File file) {
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = JavaParser.parse(file);
        } catch (IOException e) {
            log.error ("Error reading file: " + file.toString(), e);
            return null;
        } catch (ParseProblemException e) {
            log.error ("Error parsing file: " + file.toString(), e);
            return null;
        }

        ClassBugReport report = new ClassBugReport(file);

        final List <Bug> bugs = new LinkedList<Bug>();

        TreeVisitor treeVisitor = new TreeVisitor() {
            @Override
            public void process(Node node) {
                checkAllBugs(node, report);
            }
        };

        treeVisitor.visitPreOrder(compilationUnit);

        return report;
    }

    public ProjectBugReport checkAllBugsInProject(File folder, File reportFolder) {
        return checkAllBugsInProject(folder, reportFolder, null);
    }

    public ProjectBugReport checkAllBugsInProject(File folder, File reportFolder, String pathRegex) {
        final Pattern filePathPattern = pathRegex != null ? Pattern.compile(pathRegex) : null;

        ProjectBugReport projectReport = new ProjectBugReport(folder, reportFolder);

        try {
            Files.walk(folder.toPath())
                    .filter(Files::isRegularFile)
                    .filter(path ->  {
                        String filePath = path.toString();

                        return filePath.endsWith(".java") &&
                                ( filePathPattern == null ||
                                        filePathPattern.matcher(filePath).find());})
                    .forEach(path -> {
                        File javaFile = path.toFile();
                        ClassBugReport report = checkAllBugs(javaFile);
                        if (report != null) {
                            log.info("Src file " + javaFile.toString() + " scanned. " +
                                    "Blocks scaned:" + report.getNumBlockScanned() +
                                    "; bugs found:" + report.getBugs().size());
                            projectReport.addClassReport(report);
                        } else {
                            log.warn("Src file " + javaFile.toString() +
                                    " scanned with exception. No report is generated.");
                        }
                    });
            projectReport.completeReport();
        } catch (IOException e) {
            log.error ("Error reading folder: " + folder.toString(), e);
        }

        return projectReport;
    }
}
