#!/bin/bash
set -e

API_NAME="BookAnAudioAPI"
AUTHORIZER_NAME="CustomAuthorizer"
REGION="us-east-1"
ENDPOINT="http://localhost:4566"

echo "🌀 Creating REST API..."
API_ID=$(aws apigateway create-rest-api \
  --name "$API_NAME" \
  --endpoint-url "$ENDPOINT" | jq -r '.id')
echo "✅ Created REST API with ID: $API_ID"

echo "📥 Fetching root resource ID..."
ROOT_RESOURCE_ID=$(aws apigateway get-resources \
  --rest-api-id "$API_ID" \
  --endpoint-url "$ENDPOINT" | jq -r '.items[0].id')

echo "✅ Root resource ID: $ROOT_RESOURCE_ID"

echo "🔐 Creating Lambda Authorizer..."
AUTHORIZER_ID=$(aws apigateway create-authorizer \
  --rest-api-id "$API_ID" \
  --name "$AUTHORIZER_NAME" \
  --type "TOKEN" \
  --identity-source "method.request.header.Authorization" \
  --authorizer-uri "arn:aws:apigateway:$REGION:lambda:path/2015-03-31/functions/arn:aws:lambda:$REGION:000000000000:function:authorizer/invocations" \
  --endpoint-url "$ENDPOINT" | jq -r '.id')

#################################
# Helper: create a resource
create_resource() {
  local parent_id=$1
  local path_part=$2
  aws apigateway create-resource \
    --rest-api-id "$API_ID" \
    --parent-id "$parent_id" \
    --path-part "$path_part" \
    --endpoint-url "$ENDPOINT" | jq -r '.id'
}

# Helper: setup method + integration
create_method() {
  local resource_id=$1
  local http_method=$2
  local lambda_function=$3
  local auth=$4

  aws apigateway put-method \
    --rest-api-id "$API_ID" \
    --resource-id "$resource_id" \
    --http-method "$http_method" \
    --authorization-type "$auth" \
    ${auth:+--authorizer-id "$AUTHORIZER_ID"} \
    --endpoint-url "$ENDPOINT"

  aws apigateway put-integration \
    --rest-api-id "$API_ID" \
    --resource-id "$resource_id" \
    --http-method "$http_method" \
    --type AWS_PROXY \
    --integration-http-method POST \
    --uri "arn:aws:apigateway:$REGION:lambda:path/2015-03-31/functions/arn:aws:lambda:$REGION:000000000000:function:$lambda_function/invocations" \
    --endpoint-url "$ENDPOINT"
}

#################################
# API resource & method setup
BOOKS_ID=$(create_resource $ROOT_RESOURCE_ID books)
create_method $BOOKS_ID GET books "CUSTOM"
create_method $BOOKS_ID POST books "CUSTOM"

FILTER_ID=$(create_resource $BOOKS_ID filter)
create_method $FILTER_ID GET books "CUSTOM"

URL_ID=$(create_resource $BOOKS_ID pre-signed-url)
create_method $URL_ID GET books "CUSTOM"

CHECK_ID=$(create_resource $BOOKS_ID check-net)
create_method $CHECK_ID GET books "CUSTOM"

BOOK_ID_PATH=$(create_resource $BOOKS_ID "{book_id}")

PAGES_ID=$(create_resource $BOOK_ID_PATH pages)
create_method $PAGES_ID GET pages "CUSTOM"
create_method $PAGES_ID POST pages "CUSTOM"

AUTH_ID=$(create_resource $ROOT_RESOURCE_ID auth)

LOGIN_ID=$(create_resource $AUTH_ID login)
create_method $LOGIN_ID POST authenticate "NONE"

REGISTER_ID=$(create_resource $AUTH_ID register)
create_method $REGISTER_ID POST authenticate "NONE"

CALLBACK_ID=$(create_resource $AUTH_ID oauth)
CALLBACK_CB_ID=$(create_resource $CALLBACK_ID callback)
create_method $CALLBACK_CB_ID GET authenticate "NONE"

#################################
# Deploy the API
echo "🚀 Deploying API Gateway..."
aws apigateway create-deployment \
  --rest-api-id "$API_ID" \
  --stage-name dev \
  --endpoint-url "$ENDPOINT"

echo "✅ API Gateway deployed with ID: $API_ID"
echo "🌍 Base URL: http://localhost:4566/restapis/$API_ID/dev/_user_request_"
