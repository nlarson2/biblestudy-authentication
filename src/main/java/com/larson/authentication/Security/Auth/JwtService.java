package com.larson.authentication.Security.Auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.clerk.backend_api.Clerk;
import com.clerk.backend_api.helpers.jwks.TokenVerificationException;
import com.clerk.backend_api.helpers.jwks.VerifyToken;
import com.clerk.backend_api.helpers.jwks.VerifyTokenOptions;
import io.jsonwebtoken.Claims;

import com.clerk.backend_api.models.components.EmailAddress;

import com.clerk.backend_api.models.operations.GetUserResponse;
import com.larson.authentication.Repositories.UserRepository;
import com.larson.authentication.Security.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-secrets.yaml")
public class JwtService {

    @Value("${CLERK_SECRET}")
    private String token;
    
    private Clerk sdk;
    private final UserRepository userRepository;


    public Optional<User> getUserData(String jwtToken) throws TokenVerificationException, Exception {
        
        // get the data from the provided token
        VerifyTokenOptions options = VerifyTokenOptions.Builder.withSecretKey(token).build();
        Claims claims = VerifyToken.verifyToken(jwtToken, options);
        if(claims == null) return null;

        // parse out oauthID / clerk user id
        String oauthID = claims.get("sub").toString();
        Optional<User> userQuery = userRepository.findByOauthID(oauthID);

        // if user exists, get user, else create new user and return it.
        User user = userQuery.isPresent() ? userQuery.get() : CreateNewUserFromClerkData(oauthID);
        return Optional.of(user);
    }

    private User CreateNewUserFromClerkData(String oauthID) throws Exception {

        // setup clerk sdk connection
        sdk = Clerk.builder().bearerAuth(token).build();

        //get user with oauthID from clerk service
        GetUserResponse res = sdk.users().get()
            .userId(oauthID)
            .call();
        if(!res.user().isPresent()) throw new Exception("Failed to find user: Server error");

        //use clerk user to populated data into fill in data for a new user
        com.clerk.backend_api.models.components.User userData = res.user().get();
        String firstName = userData.firstName().get();
        String lastName = userData.lastName().get();
        List<EmailAddress> emails = userData.emailAddresses().get();
        User user = new User();
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setAdmin(false);
        user.setOauthID(oauthID);
        if(emails.size() > 0)
            user.setEmail(emails.get(0).emailAddress());
        userRepository.save(user);
        return user;

    }

    

}
