package co.com.pragma.api.factory;

import co.com.pragma.api.dto.ErrorInfoDto;
import co.com.pragma.api.dto.ResponseApiDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseFactoryTest {

    @Test
    void ok_shouldWrapDataWith200AndDefaultMessage() {
        String data = "payload";

        ResponseEntity<ResponseApiDto<String>> response = ResponseFactory.ok(data);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals(HttpStatus.OK.getReasonPhrase(), response.getBody().getMessage());
        assertEquals(data, response.getBody().getData());
        assertNull(response.getBody().getError());
        assertNull(response.getBody().getMeta());
        assertFalse(response.getHeaders().containsKey(HttpHeaders.LOCATION));
    }

    @Test
    void ok_withCustomMessageShouldWrapDataWith200AndCustomMessage() {
        String data = "payload";
        String message = "custom-ok";

        ResponseEntity<ResponseApiDto<String>> response = ResponseFactory.ok(data, message);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals(message, response.getBody().getMessage());
        assertEquals(data, response.getBody().getData());
        assertNull(response.getBody().getError());
        assertNull(response.getBody().getMeta());
    }

    @Test
    void created_shouldSet201AndLocationHeaderWhenProvided() {
        String location = "/api/users/1";
        String data = "created";

        ResponseEntity<ResponseApiDto<String>> response = ResponseFactory.created(location, data);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatus());
        assertEquals(HttpStatus.CREATED.getReasonPhrase(), response.getBody().getMessage());
        assertEquals(data, response.getBody().getData());
        assertEquals(location, response.getHeaders().getFirst(HttpHeaders.LOCATION));
    }

    @Test
    void created_shouldNotSetLocationHeaderWhenNullOrEmpty() {
        String data = "created";

        ResponseEntity<ResponseApiDto<String>> nullLocation = ResponseFactory.created(null, data);
        ResponseEntity<ResponseApiDto<String>> emptyLocation = ResponseFactory.created("", data);

        assertFalse(nullLocation.getHeaders().containsKey(HttpHeaders.LOCATION));
        assertFalse(emptyLocation.getHeaders().containsKey(HttpHeaders.LOCATION));
        assertEquals(HttpStatus.CREATED, nullLocation.getStatusCode());
        assertEquals(HttpStatus.CREATED, emptyLocation.getStatusCode());
    }

    @Test
    void error_shouldWrapErrorInfoWithStatusAndNoMeta() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String code = "VAL_001";
        String detail = "invalid input";

        ResponseEntity<ResponseApiDto<String>> response = ResponseFactory.error(status, code, detail);

        assertEquals(status, response.getStatusCode());
        assertEquals(status.value(), response.getBody().getStatus());
        ErrorInfoDto err = response.getBody().getError();
        assertNotNull(err);
        assertEquals(code, err.getCode());
        assertEquals(detail, err.getDetail());
        assertNull(response.getBody().getMeta());
        assertNull(response.getBody().getData());
    }

    @Test
    void error_withMetaShouldIncludeMetaMap() {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String code = "VAL_002";
        String detail = "missing field";
        Map<String, Object> meta = new HashMap<>();
        meta.put("field", "email");
        meta.put("reason", "blank");

        ResponseEntity<ResponseApiDto<String>> response = ResponseFactory.error(status, code, detail, meta);

        assertEquals(status, response.getStatusCode());
        assertEquals(status.value(), response.getBody().getStatus());
        assertEquals(meta, response.getBody().getMeta());
        assertEquals(code, response.getBody().getError().getCode());
        assertEquals(detail, response.getBody().getError().getDetail());
    }
}
