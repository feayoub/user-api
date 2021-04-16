package br.com.felipejoao.userapi.domain.exception;

public class WrongPasswordException extends BusinessException {

    public WrongPasswordException(String message) {
        super(message);
    }
}
