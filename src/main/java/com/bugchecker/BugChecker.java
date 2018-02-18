package com.bugchecker;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.TreeVisitor;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by elnggng on 2/17/18.
 */
public class BugChecker {

    private static BugChecker ourInstance = new BugChecker();

    public static BugChecker getInstance() {
        return ourInstance;
    }

    private static Logger log = LoggerFactory.getLogger(BugChecker.class);

    private static List<AbstractRule> bugMatchers;

    static {
        Reflections reflections = new Reflections("com.bugchecker.rules");

        Set<Class<? extends AbstractRule>> all = reflections.getSubTypesOf(AbstractRule.class);

        bugMatchers = new ArrayList<AbstractRule>();

        for (Class<? extends AbstractRule> matcherClass : all) {
            try {
                Class<? extends Node > nodeType = (Class<Node>)
                        ((ParameterizedType)matcherClass.getGenericSuperclass()).getActualTypeArguments()[0];

                AbstractRule matcher = matcherClass.newInstance();
                matcher.setNodeClass(nodeType);
                bugMatchers.add(matcher);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error ("Error constructing bug rules: " + matcherClass.toString());
            }
        }
    }

    public List<Bug> checkAllBugs (Node node) {
        List<Bug> bugs = new LinkedList<Bug>();

        for (AbstractRule matcher: bugMatchers) {
            if (matcher.isInterested(node)) {
                Bug bug = matcher.match(node);
                if (bug != null) {
                    bugs.add(bug);
                }
            }
        }
        return bugs;
    }

    public List<Bug> checkAllBugs (File file) {
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = JavaParser.parse(file);
        } catch (IOException e) {
            log.error ("Error parsing file: " + file.toString(), e);
            return new ArrayList<Bug>();
        }

        final List <Bug> bugs = new LinkedList<Bug>();

        TreeVisitor treeVisitor = new TreeVisitor() {
            @Override
            public void process(Node node) {
                bugs.addAll(checkAllBugs(node));
            }
        };

        treeVisitor.visitPreOrder(compilationUnit);

        return bugs;
    }
}
