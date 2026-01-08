package de.assecor.persons.controller.advice;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ControllerAdviceUtils {

    private ControllerAdviceUtils() {
        // Utility class
    }

    public static ProblemDetail problem(
            HttpStatus status,
            String title,
            String detail
    ) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }
}
