# AWS SQS with DLQ Cloudformation Stack

## Deploying the stack
```
mvn clean package
cd infra
./deploy.sh \
  --version 1.0.0-SNAPSHOT \
  --environment dev \
  --deployment_bucket my-deployment-bucket
  --project poc \
  --application sqs-with-dlq
```