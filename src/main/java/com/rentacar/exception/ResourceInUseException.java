package com.rentacar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Bu istisna fırlatıldığında HTTP 409 CONFLICT durum kodu döndürülmesini sağlar.
// Özellikle REST API'ler için faydalıdır, ancak MVC'de de durumu belirtir.
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}