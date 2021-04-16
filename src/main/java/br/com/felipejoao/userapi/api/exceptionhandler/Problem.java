package br.com.felipejoao.userapi.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Builder
class Problem {
    private Integer status;
    private OffsetDateTime dateTime;
    private String title;
    private List<Field> fields;

    @Data
    @AllArgsConstructor
    @Builder
    static class Field {
        private String name;
        private String message;
    }

}
