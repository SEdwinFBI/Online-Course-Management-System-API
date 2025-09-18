package com.edwinbaquiax.courseadministratorservice.validations;

import com.edwinbaquiax.courseadministratorservice.services.user.IUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistsByUsernameValidation implements ConstraintValidator<ExistsByUsername,String> {

    @Autowired
    private IUserService userService;



    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        //TODO: por verificar funcionamiento
        return userService.existsByUsername(s);
    }
}
