package org.top.exception;

public class LockFailureException extends LockException{

    public LockFailureException(){

    }

    public LockFailureException(String msg){
        super(msg);
    }

}
