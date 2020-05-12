package com.learn.miaosha.exception;

import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequestWrapper;

import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequestWrapper request,Exception e){
        e.printStackTrace();
        if(e instanceof  GlobleException){
                GlobleException ex = (GlobleException)e;
                return Result.error(ex.getCm());
        }

        else if(e instanceof BindException){
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }
}
