package com.marles.horarioappufps.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.marles.horarioappufps.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseAuthService {

    @Autowired
    private UserService userService;

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public User getUserByToken(String idToken) throws FirebaseAuthException {
        FirebaseToken token = verifyToken(idToken);
        String email =  token.getEmail();
        String uid  = token.getUid();
        return userService.getOrCreateUser(email, uid);
    }
}
