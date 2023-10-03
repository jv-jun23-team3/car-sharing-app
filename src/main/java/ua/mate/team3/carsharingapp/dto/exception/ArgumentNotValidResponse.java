package ua.mate.team3.carsharingapp.dto.exception;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ArgumentNotValidResponse {
    private LocalDateTime timeStamp;
    private String[] errors;
    private HttpStatus status;
}
