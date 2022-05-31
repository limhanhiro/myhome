package com.example.thymeleafex.vaildator;

import com.example.thymeleafex.model.Board;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.thymeleaf.util.StringUtils;

@Component
public class BoardVaildator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Board.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Board b = (Board)obj;
        if(StringUtils.isEmpty(b.getTitle())) {
            errors.rejectValue("title","key","제목을 입력하세요");
        }else if(StringUtils.isEmpty(b.getContent())){
                errors.rejectValue("content","key","내용을 입력하세요");
            }

    }
}
