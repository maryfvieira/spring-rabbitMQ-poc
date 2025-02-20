package org.example.rabbit.consumer;

import org.springframework.validation.FieldError;
import java.util.List;
import static java.util.stream.Collectors.toList;

public class ErrorUtils {

    public static List<Error> remapErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(error -> new Error(
        )).collect(toList());
    }
}
