package br.com.felipejoao.userapi.api.model;

import br.com.felipejoao.userapi.api.util.ValidationMessages;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserModel {
    private Long id;

    @NotBlank(message = ValidationMessages.EMPTY)
    @Size(max = 60, message = ValidationMessages.EXCEEDED_SIZE)
    private String name;

    @NotBlank(message = ValidationMessages.EMPTY)
    @Email(message = ValidationMessages.EMAIL_WRONG_FORMAT)
    @Size(max = 255, message = ValidationMessages.EXCEEDED_SIZE)
    private String email;

    @NotBlank(message = ValidationMessages.EMPTY)
    @Size(max = 60, message = ValidationMessages.EXCEEDED_SIZE)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = ValidationMessages.PASSWORD_WRONG_FORMAT)
    private String password;
}
