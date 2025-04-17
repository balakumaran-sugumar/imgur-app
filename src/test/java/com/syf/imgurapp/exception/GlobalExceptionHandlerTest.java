package com.syf.imgurapp.exception;

import com.syf.imgurapp.model.AppExceptionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Test
    void handleImageAppExceptionShouldReturnInternalServerErrorByDefault() {
        ImageAppException ex = new ImageAppException("Test error message");
        ResponseEntity<AppExceptionResponse> response =
                globalExceptionHandler.handleImageAppException(ex, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test error message", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void handleImageAppExceptionShouldReturnConflictForUserAlreadyExistsException() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User already exists");
        ResponseEntity<AppExceptionResponse> response =
                globalExceptionHandler.handleImageAppException(ex, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody().getMessage());
    }

    @Test
    void handleMethodArgumentNotValidShouldReturnBadRequestWithErrorDetails() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("objectName", "field1", "Field1 error");
        FieldError fieldError2 = new FieldError("objectName", "field2", "Field2 error");

        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        ResponseEntity<Object> response =
                globalExceptionHandler.handleMethodArgumentNotValid(ex, headers, status, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errorMap = (Map<String, String>) response.getBody();
        assertNotNull(errorMap);
        assertEquals(2, errorMap.size());
        assertEquals("Field1 error", errorMap.get("field1"));
        assertEquals("Field2 error", errorMap.get("field2"));
    }

    @Test
    void handleMethodArgumentNotValidShouldHandleEmptyErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList());

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        ResponseEntity<Object> response =
                globalExceptionHandler.handleMethodArgumentNotValid(ex, headers, status, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errorMap = (Map<String, String>) response.getBody();
        assertNotNull(errorMap);
        assertTrue(errorMap.isEmpty());
    }


}
