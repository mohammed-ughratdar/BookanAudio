#!/bin/bash
set -e

BUCKET_NAME="bookanaudio-lambda-bucket"

echo "Creating S3 bucket '$BUCKET_NAME' if it doesn't exist..."

awslocal --region us-east-1 s3api create-bucket \
  --bucket bookanaudio-lambda-bucket || echo "Bucket already exists"

upload_lambda() {
  local function_name=$1
  local file_path=$2
  local handler=$3
  local runtime=$4

  echo "Uploading $function_name package to S3..."
  awslocal s3 cp $file_path s3://$BUCKET_NAME/$function_name.zip

  echo "Creating Lambda function '$function_name' using S3 object..."
  awslocal lambda create-function \
    --function-name $function_name \
    --runtime $runtime \
    --handler $handler \
    --memory-size 256 \
    --timeout 30 \
    --role arn:aws:iam::000000000000:role/lambda-role \
    --code S3Bucket=$BUCKET_NAME,S3Key=$function_name.zip \
    --environment file://lambda-env.json

  echo "Created Lambda function '$function_name'"
}

echo "Deploying Java Lambdas..."

upload_lambda "books" "backend/books/target/main-0.0.1-SNAPSHOT.jar" "com.bookanaudio.StreamLambdaHandler::handleRequest" "java17"

upload_lambda "authenticate" "backend/auth/target/main-0.0.1-SNAPSHOT.jar" "com.bookanaudio.StreamLambdaHandler::handleRequest" "java17"

upload_lambda "pages" "backend/pages/target/main-0.0.1-SNAPSHOT.jar" "com.bookanaudio.StreamLambdaHandler::handleRequest" "java17"

upload_lambda "s3" "backend/s3/target/function-sample-aws-0.0.1-SNAPSHOT.jar" "com.bookanaudio.StreamLambdaHandler::handleRequest" "java17"

echo "Packaging and deploying Python Lambda 'authoriser'..."

cd backend/authorizer/
zip -r ../../authorizer.zip auth_authorizer.py
cd ../../

upload_lambda "authorizer" "authorizer.zip" "auth_authorizer.lambda_handler" "python3.9"

echo "✅ All Lambdas deployed successfully!"
