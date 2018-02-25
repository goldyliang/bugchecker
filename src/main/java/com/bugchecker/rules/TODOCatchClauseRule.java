package com.bugchecker.rules;

import com.bugchecker.AbstractRule;
import com.bugchecker.Bug;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by goldyliang on 2/18/18.
 */
public class TODOCatchClauseRule extends AbstractRule<CatchClause> {

    public TODOCatchClauseRule() {
        super ("TODOCatchClause",
                String.join (
                        "Catch clause shall not be marked as TODO or FIXME. ",
                        "Correct error handlng code shall be present from the beginning.."));
    }

    private Pattern todoPattern = Pattern.compile("\\bTODO\\b|\\bFIXME\\b|\\bFIX ME\\b");

    private boolean isTODOComment (String comment) {
        return todoPattern.matcher(comment.toUpperCase()).find();
    }

    @Override
    public Bug match(CatchClause node) {
        BlockStmt block = (BlockStmt) node.getChildNodes().get(1);

        final Comment[] todoComment = new Comment[1];

        boolean containsToDoComment = block.stream()
                .anyMatch ( n -> {
                    Comment comment = null;
                    if (n instanceof Comment) {
                        // Block comment
                        comment = (Comment)n;
                    } else if (n.getComment().isPresent()) {
                        // Line comment exists by getComment()
                        comment = n.getComment().get();
                    } else {
                        comment = null;
                    }

                    if (comment != null && isTODOComment(comment.getContent())) {
                        todoComment[0] = comment;
                        return true;
                    } else {
                        return false;
                    }
                });

        if (containsToDoComment) {
            return new Bug(todoComment[0], this);
        } else
            return null;
    }
}
