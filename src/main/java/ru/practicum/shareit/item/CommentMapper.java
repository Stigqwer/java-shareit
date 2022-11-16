package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, UserDto userDto) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(userDto.getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto, Long itemId, Long authorId) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
