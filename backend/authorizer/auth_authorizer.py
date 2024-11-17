import os
import jwt  
import base64
from datetime import datetime

SECRET_KEY = os.getenv("jwt_secret_key")
decoded_secret_key = base64.b64decode(SECRET_KEY)

def lambda_handler(event, context):
    print("Lambda function invoked")
    print(f"Event received: {event}")

    token = event.get('authorizationToken')
    method_arn = event.get('methodArn')

    if not token:
        print("No token found in the event")
        return generate_policy("user", "Deny", method_arn)

    # Strip 'Bearer ' prefix if present
    if token.startswith("Bearer "):
        token = token.split(" ")[1]

    print(f"Received token: {token}")
    print(f"Method ARN: {method_arn}")

    if not validate_token(token):
        print("Token validation failed")
        return generate_policy("user", "Deny", method_arn)

    principal_id = extract_username(token)
    print(f"Token valid. Principal ID extracted: {principal_id}")
    return generate_policy(principal_id, "Allow", method_arn)

def validate_token(token):
    try:
        print("Validating token...")
        print(f"Decoding token with secret: {repr(decoded_secret_key)}")
        payload = jwt.decode(token, decoded_secret_key, algorithms=["HS256"])
        print(f"Decoded payload: {payload}")

        if payload["exp"] < datetime.utcnow().timestamp():
            print("Token has expired")
            return False

        print("Token is valid")
        return True
    except Exception as e:
        print(f"Error validating token: {str(e)}")
        return False

def extract_username(token):
    try:
        print("Extracting username from token...")
        payload = jwt.decode(token, decoded_secret_key, algorithms=["HS256"])
        username = payload["sub"]
        print(f"Extracted username: {username}")
        return username
    except Exception as e:
        print(f"Error extracting username: {str(e)}")
        raise

def generate_policy(principal_id, effect, method_arn):
    print(f"Generating policy with effect: {effect}")
    policy_document = {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Action": "execute-api:Invoke",
                "Effect": effect,
                "Resource": method_arn
            }
        ]
    }
    policy = {
        "principalId": principal_id,
        "policyDocument": policy_document
    }
    print(f"Generated policy: {policy}")
    return policy
