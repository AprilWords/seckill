package com.learn.miaosha.exception;

import com.learn.miaosha.result.CodeMsg;

public class GlobleException extends RuntimeException{
    public CodeMsg getCm() {
        return cm;
    }

    public void setCm(CodeMsg cm) {
        this.cm = cm;
    }

    private CodeMsg cm;
    public GlobleException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }
}
