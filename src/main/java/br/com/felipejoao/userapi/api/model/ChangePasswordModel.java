package br.com.felipejoao.userapi.api.model;

import br.com.felipejoao.userapi.api.util.ValidationMessages;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordModel {
    @NotBlank(message = ValidationMessages.EMPTY)
    private String oldPassword;

    @NotBlank(message = ValidationMessages.EMPTY)
    @Size(max = 60, message = ValidationMessages.EXCEEDED_SIZE)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = ValidationMessages.PASSWORD_WRONG_FORMAT)
    private String newPassword;
}
