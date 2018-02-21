package com.bugchecker.rules;

import com.bugchecker.AbstractRule;
import com.bugchecker.Bug;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Optional;

/**
 * Created by elnggng on 2/18/18.
 */
public class TODOCatchClauseRule extends AbstractRule<CatchClause> {

    public TODOCatchClauseRule() {
        super ("TODOCatchClause",
                String.join (
                        "Catch clause shall not be marked as TODO or FIXME. ",
                        "Correct error handlng code shall be present from the beginning.."));
    }

    @Override
    public Bug match(CatchClause node) {
        BlockStmt block = (BlockStmt) node.getChildNodes().get(1);

        Optional<Comment> toDoComment = block.findAll(Comment.class)
                .stream()
                .filter ( n -> {
                    String comment = n.toString();
                    return comment.contains("TODO") || comment.contains("FIXME");
                }).findFirst();

        if (toDoComment.isPresent()) {
            return new Bug(toDoComment.get(), this);
        } else
            return null;
    }
}
