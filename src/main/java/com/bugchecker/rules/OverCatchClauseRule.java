package com.bugchecker.rules;

import com.bugchecker.AbstractRule;
import com.bugchecker.Bug;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.TreeVisitor;

import java.util.Optional;

/**
 * Created by elnggng on 2/18/18.
 */
public class OverCatchClauseRule extends AbstractRule<CatchClause> {

    public OverCatchClauseRule() {
        super ("OverCatchClause",
                String.join (
                        "Shall not over catch and exit/abort in the catch clause. ",
                        "Specific exception shall be catched, especially if the error handling code exits or aborts."));
    }

    @Override
    public Bug match(CatchClause node) {

        Parameter parameter = (Parameter) node.getChildNodes().get(0);
        BlockStmt block = (BlockStmt) node.getChildNodes().get(1);

        String catchExceptionType = parameter.getType().toString();

        if (catchExceptionType.equals("Exception") ||
                catchExceptionType.equals("Throwable")) {
            // Visit call statements and check whether exit or abort like statement exists
            Optional<MethodCallExpr> exitOrAbortMethodCall = block
                    .findAll(MethodCallExpr.class).stream()
                    .filter(n -> {
                        String name = n.getName().toString();
                        return name.equals("exit") || name.equals("abort");
                    })
                    .findFirst();

            if (exitOrAbortMethodCall.isPresent()) {
                return new Bug(exitOrAbortMethodCall.get(), this);
            }
        }
        return null;
    }
}
