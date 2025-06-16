#!/bin/bash


# LocalStack SAM Deployment Script

set -e  # Exit on error

echo "Setting AWS environment variables..."
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

echo "Creating S3 bucket in LocalStack..."
aws --endpoint-url=http://localhost:4566 s3 mb s3://bookanaudio-artifacts-bucket || true

echo "Packaging SAM template..."
sam package \
  --template-file template.yml \
  --output-template-file packaged.yml \
  --s3-bucket bookanaudio-artifacts-bucket \
  --region us-east-1

echo "Loading environment variables from .env..."
if [ -f .env ]; then
  source .env
else
  echo ".env file not found!"
  exit 1
fi

echo "Deploying to LocalStack..."
aws --endpoint-url=http://localhost:4566 cloudformation deploy \
  --template-file packaged.yml \
  --stack-name bookanaudio-local \
  --capabilities CAPABILITY_IAM \
  --parameter-overrides \
    S3BucketName=$S3BucketName \
    GoogleTokenEndpoint=$GoogleTokenEndpoint \
    JwtExpiration=$JwtExpiration \
    JwtSecretKey=$JwtSecretKey \
    GoogleClientId=$GoogleClientId \
    DbUrl=$DbUrl \
    GoogleRedirectUri=$GoogleRedirectUri \
    DbUsername=$DbUsername \
    GoogleUserInfoEndpoint=$GoogleUserInfoEndpoint \
    DbPassword=$DbPassword \
    GoogleClientSecret=$GoogleClientSecret \
    AwsRegion=$AwsRegion \
    SqsUrl=$SqsUrl \
    OpenaiKey=$OpenaiKey \
    OpenaiUrl=$OpenaiUrl \
    OcrApiUrl=$OcrApiUrl \
    OcrApiKey=$OcrApiKey \
    MainClass=$MainClass

echo "Checking CloudFormation stack status..."
aws --endpoint-url=http://localhost:4566 cloudformation describe-stacks \
  --stack-name bookanaudio-local \
  --query 'Stacks[0].Outputs' || echo "No outputs found."

echo "Deployment complete."
