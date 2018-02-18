package com.bugchecker.rules;

import com.bugchecker.Bug;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.bugchecker.AbstractRule;

/**
 * Created by elnggng on 2/18/18.
 */
public class EmptyCatchClauseRule extends AbstractRule<CatchClause> {

    public EmptyCatchClauseRule() {
        super ("EmptyCatchClause",
                String.join (
                        "Catch clause shall not be empty. ",
                        "It shall include at lease one statementnot except for varaible declartion."));
    }

    @Override
    public Bug match(CatchClause node) {
        BlockStmt block = (BlockStmt) node.getChildNodes().get(1);

        boolean validExpression = block.getChildNodes().stream()
                .anyMatch( n -> {
                    if (n instanceof ExpressionStmt) {
                        ExpressionStmt e = (ExpressionStmt) n;
                        return !(e.getExpression() instanceof VariableDeclarationExpr);
                    } else {
                        return false;
                    }
                });

        if (! validExpression) {
            return new Bug(node, this);
        } else
            return null;
    }
}
