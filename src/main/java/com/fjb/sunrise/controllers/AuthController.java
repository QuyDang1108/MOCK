package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.requests.LoginRequest;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.requests.VerificationByEmail;
import com.fjb.sunrise.services.EmailService;
import com.fjb.sunrise.services.UserService;
import com.fjb.sunrise.utils.Constants;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;


    @GetMapping("/login")
    public ModelAndView indexLogin(@RequestParam(value = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.AUTH_VIEW);
        modelAndView.addObject(Constants.ApiConstant.LOGIN_OBJECT, new LoginRequest());
        modelAndView.addObject(Constants.ApiConstant.REGISTER_OBJECT, new RegisterRequest());
        if (error != null) {
            modelAndView.addObject(Constants.ApiConstant.ERROR_MESSAGE_OBJECT, "Đăng nhập không thành công!");
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView indexRegister() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.AUTH_VIEW);
        modelAndView.addObject(Constants.ApiConstant.LOGIN_OBJECT, new LoginRequest());
        modelAndView.addObject(Constants.ApiConstant.REGISTER_OBJECT, new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView doRegister(@ModelAttribute(Constants.ApiConstant.REGISTER_OBJECT)
                                       RegisterRequest registerRequest) {
        //setup object for view
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.AUTH_VIEW);
        modelAndView.addObject(Constants.ApiConstant.LOGIN_OBJECT, new LoginRequest());
        modelAndView.addObject(Constants.ApiConstant.REGISTER_OBJECT, new RegisterRequest());

        // implement register for user
        if (userService.checkRegister(registerRequest)) {
            modelAndView.setViewName(Constants.ApiConstant.AUTH_REDIRECT_LOGIN);
        } else {
            modelAndView.addObject(Constants.ApiConstant.ERROR_MESSAGE_OBJECT, "Đăng kí không thành công");
        }

        return modelAndView;
    }

    @GetMapping("/forgotPassword")
    public ModelAndView indexForgotPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.VERIFICATION_BY_EMAIL_VIEW);
        modelAndView.addObject(Constants.ApiConstant.EMAIL_OBJECT, "");
        return modelAndView;
    }

    @PostMapping("/sendToEmail")
    public ModelAndView doSendCodeToEmail(@ModelAttribute(Constants.ApiConstant.EMAIL_OBJECT)
                                              String email) {
        ModelAndView modelAndView = new ModelAndView();

        VerificationByEmail verification = new VerificationByEmail(email, LocalDateTime.now());

        String message = emailService.sendEmail(verification);

        modelAndView.setViewName(Constants.ApiConstant.VERIFICATION_BY_EMAIL_VIEW);
        modelAndView.addObject(Constants.ApiConstant.ERROR_MESSAGE_OBJECT, message);

        return modelAndView;
    }

    @GetMapping("/verify")
    public ModelAndView doVerify(@RequestParam("code") String code) {
        ModelAndView modelAndView = new ModelAndView();

        String message = emailService.checkCode(code);

        if (message != null) {
            modelAndView.setViewName(Constants.ApiConstant.VERIFICATION_BY_EMAIL_VIEW);
            modelAndView.addObject(Constants.ApiConstant.ERROR_MESSAGE_OBJECT, message);
            return modelAndView;
        }

        String email = emailService.getEmailFromCode(code);

        modelAndView.setViewName(Constants.ApiConstant.CHANGE_PASSWORD_VIEW);
        modelAndView.addObject(Constants.ApiConstant.EMAIL_OBJECT, email);
        modelAndView.addObject(Constants.ApiConstant.NEW_PASSWORD_OBJECT, "");
        return modelAndView;
    }


    @PostMapping("/changePassword")
    public ModelAndView doChangePassword(@ModelAttribute(Constants.ApiConstant.EMAIL_OBJECT) String email,
                                         @ModelAttribute(Constants.ApiConstant.NEW_PASSWORD_OBJECT) String password) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.ApiConstant.AUTH_REDIRECT_LOGIN);

        String message = userService.changePassword(email, password);

        if (message != null) {
            modelAndView.setViewName(Constants.ApiConstant.VERIFICATION_BY_EMAIL_VIEW);
            modelAndView.addObject(Constants.ApiConstant.ERROR_MESSAGE_OBJECT, message);
        }

        return modelAndView;
    }
}
