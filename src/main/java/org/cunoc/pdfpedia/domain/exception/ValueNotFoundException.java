package org.cunoc.pdfpedia.domain.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = NOT_FOUND)
public class ValueNotFoundException extends RuntimeException {

    public ValueNotFoundException(String message) {
        super(message);
    }
}
