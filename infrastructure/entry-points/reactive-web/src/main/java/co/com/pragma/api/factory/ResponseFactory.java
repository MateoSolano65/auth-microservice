package co.com.pragma.api.factory;

import co.com.pragma.api.dto.ErrorInfoDto;
import co.com.pragma.api.dto.ResponseApiDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseFactory {

    private ResponseFactory() {
        // Private constructor to prevent instantiation
    }

    public static <T> ResponseEntity<ResponseApiDto<T>> ok(T data) {
        return ResponseEntity.ok(
                ResponseApiDto.<T>builder()
                        .status(HttpStatus.OK.value())
                        .message(HttpStatus.OK.getReasonPhrase())
                        .data(data)
                        .build()
        );
    }

    public static <T> ResponseEntity<ResponseApiDto<T>> ok(T data, String message) {
        return ResponseEntity.ok(
                ResponseApiDto.<T>builder()
                        .status(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .build()
        );
    }

    public static <T> ResponseEntity<ResponseApiDto<T>> created(String location, T data) {
        HttpHeaders headers = new HttpHeaders();
        if (location != null && !location.isEmpty()) {
            headers.add(HttpHeaders.LOCATION, location);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(ResponseApiDto.<T>builder()
                        .status(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .data(data)
                        .build()
                );
    }

    public static <T> ResponseEntity<ResponseApiDto<T>> error(
            HttpStatus httpStatus, String code, String detail) {
        return error(httpStatus, code, detail, null);
    }

    public static <T> ResponseEntity<ResponseApiDto<T>> error(
            HttpStatus httpStatus, String code, String detail, Map<String, Object> meta) {
        
        ErrorInfoDto errorInfo = ErrorInfoDto.builder()
                .code(code)
                .detail(detail)
                .build();

        return ResponseEntity
                .status(httpStatus)
                .body(ResponseApiDto.<T>builder()
                        .status(httpStatus.value())
                        .error(errorInfo)
                        .meta(meta)
                        .build()
                );
    }
}
