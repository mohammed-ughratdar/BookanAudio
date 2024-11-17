package com.bookanaudio;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerRequest;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Collections;
import java.util.Date;

public class AuthAuthorizer implements RequestHandler, APIGatewayCustomAuthorizerResponse> {

    private final String SECRET_KEY;

    public AuthAuthorizer() {
        this.SECRET_KEY = System.getenv("jwt_secret_key");
        if (this.SECRET_KEY == null) {
            throw new RuntimeException("JWT Secret Key is not set in environment variables");
        }
    }

    @Override
    public APIGatewayCustomAuthorizerResponse handleRequest(APIGatewayCustomAuthorizerRequest input, Context context) {
        String token = input.getAuthorizationToken();
        String methodArn = input.getMethodArn();

        if (token == null || !validateToken(token)) {
            return generateDeny("user", methodArn);
        }

        String principalId = extractUsername(token);
        return generateAllow(principalId, methodArn);
    }

    private boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String extractUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject(); 
    }

    private APIGatewayCustomAuthorizerResponse generateAllow(String principalId, String resource) {
        return generatePolicy(principalId, "Allow", resource);
    }

    private APIGatewayCustomAuthorizerResponse generateDeny(String principalId, String resource) {
        return generatePolicy(principalId, "Deny", resource);
    }

    private APIGatewayCustomAuthorizerResponse generatePolicy(String principalId, String effect, String resource) {
        APIGatewayCustomAuthorizerResponse.PolicyDocument policyDocument = new APIGatewayCustomAuthorizerResponse.PolicyDocument();
        APIGatewayCustomAuthorizerResponse.PolicyDocument.Statement statement = new APIGatewayCustomAuthorizerResponse.PolicyDocument.Statement();
        
        statement.setEffect(effect);
        statement.setAction("execute-api:Invoke");
        statement.setResource(resource);
        
        policyDocument.setStatement(Collections.singletonList(statement));

        return new APIGatewayCustomAuthorizerResponse(principalId, policyDocument);
    }
}
