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


    public Optional<User> getUserData(String jwtToken) throws Exception {
        
        
        try{
            // get the data from the provided token
            Claims claims = GetTokenDetail(jwtToken);
            if(claims == null) return null;
    
            User user = GetUser(claims);
            return Optional.of(user);

        } catch (Exception ex) {
            return null;
        }
    }

    private Claims GetTokenDetail(String jwtToken)  throws TokenVerificationException{
        VerifyTokenOptions options = VerifyTokenOptions.Builder.withSecretKey(token).build();
        return VerifyToken.verifyToken(jwtToken, options);
    }

    private User GetUser(Claims claims) throws Exception {
        // parse out oauthID / clerk user id
        String oauthID = claims.get("sub").toString();
        Optional<User> userQuery = userRepository.findByOauthID(oauthID);

        // if user exists, get user, else create new user and return it.
        return  userQuery.isPresent() ? userQuery.get() : CreateNewUserFromClerkData(oauthID);
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
        String email = emails.get(0).emailAddress();
        User user = new User(oauthID, firstName, lastName, email);
        user.setRoles(null);
        userRepository.save(user);
        return user;

    }

    

}
