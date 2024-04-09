package mx.edu.utez.carsishop.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomResponse<T> {
    T data;
    boolean error;
    int status;
    String message;
    int total;
}
