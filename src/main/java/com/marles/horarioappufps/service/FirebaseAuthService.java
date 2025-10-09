package com.marles.horarioappufps.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.marles.horarioappufps.exception.UserException;
import com.marles.horarioappufps.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class FirebaseAuthService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-z]+@ufps.edu.co$");

    @Autowired
    private UserService userService;

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public User getUserByToken(String idToken) throws FirebaseAuthException {
        FirebaseToken token = verifyToken(idToken);
        String uid = token.getUid();
        String email = token.getEmail();
        if(!EMAIL_PATTERN.matcher(email).matches()) {
            throw new UserException("El correo "+email+" no es un correo institucional UFPS.");
        }
        String name = token.getName();
        return userService.getOrCreateUser(uid, email, name);
    }
}
