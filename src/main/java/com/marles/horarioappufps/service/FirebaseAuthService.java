package com.marles.horarioappufps.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.marles.horarioappufps.exception.UserException;
import com.marles.horarioappufps.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FirebaseAuthService {
    @Value("${ALLOWED_EMAIL_DOMAINS}")
    private List<String> allowedEmailDomains;

    private static final Pattern EMAIL_DOMAIN_PATTERN = Pattern.compile("(?<=@)\\w+(\\.\\w+)+$");

    @Autowired
    private UserService userService;

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public User getUserByToken(String idToken) throws FirebaseAuthException {
        FirebaseToken token = verifyToken(idToken);
        String uid = token.getUid();
        String email = token.getEmail();

        Matcher matcher = EMAIL_DOMAIN_PATTERN.matcher(email);

        if (!matcher.find()) {
            throw new UserException("Email inválido");
        }

        String domain = matcher.group();

        if (!allowedEmailDomains.contains("*") &&
                !allowedEmailDomains.contains(domain)) {

            String message = "Solo se permiten correos " +
                    allowedEmailDomains.stream()
                            .map(d -> "@" + d)
                            .collect(Collectors.joining(", "));

            throw new UserException(message);
        }

        String name = token.getName();
        return userService.getOrCreateUser(uid, email, name);
    }
}
