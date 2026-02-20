package com.company.project.presentation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Reusable API response annotations for common endpoint patterns
 */
public class ApiResponses {

    /**
     * Standard response for endpoints that may return 404 when resource is not found
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_NOT_FOUND_ERROR)
                    ))
    })
    public @interface TodoNotFound {
    }

    /**
     * Response for successful todo deletion (204 No Content + 404)
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todo deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_NOT_FOUND_ERROR)
                    ))
    })
    public @interface TodoDeleted {
    }

    /**
     * Response for successful todo update (200 OK + 400/404)
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_UPDATED)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.VALIDATION_ERROR)
                    )),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_NOT_FOUND_ERROR)
                    ))
    })
    public @interface TodoUpdated {
    }

    /**
     * Response for successful todo creation (201 Created + 400)
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Todo created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_CREATED)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid input - title is required",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.VALIDATION_ERROR)
                    ))
    })
    public @interface TodoCreated {
    }

    /**
     * Response for successful todo retrieval (200 OK + 404)
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todo found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_FOUND)
                    )),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_NOT_FOUND_ERROR)
                    ))
    })
    public @interface TodoFound {
    }

    /**
     * Response for successful todo list retrieval (200 OK)
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of todos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = ApiResponseExamples.TODO_LIST)
                    ))
    })
    public @interface TodoListRetrieved {
    }
}
