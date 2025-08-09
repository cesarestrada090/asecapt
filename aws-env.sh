#!/bin/bash

# AWS S3 Configuration for ASECAPT Certificates
# Export these environment variables before running the Spring Boot application

# ASECAPT-specific AWS variables (to avoid conflicts with existing AWS vars)
export ASECAPT_AWS_ACCESS_KEY_ID=AKIAUF4PKCOLKJWLYBVK
export ASECAPT_AWS_SECRET_ACCESS_KEY=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
export ASECAPT_AWS_REGION=sa-east-1
export ASECAPT_AWS_S3_BUCKET_NAME=asecapt-certificates

# Standard AWS variables (if you want to override global ones)
export AWS_ACCESS_KEY_ID=AKIAUF4PKCOLKJWLYBVK
export AWS_SECRET_ACCESS_KEY=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
export AWS_DEFAULT_REGION=sa-east-1
export AWS_REGION=sa-east-1

# Spring Boot specific properties (match application.properties keys)
export AWS_S3_BUCKET_NAME=asecapt-certificates
export APP_BASE_URL=https://asecapt.com
export APP_CERTIFICATES_UPLOAD_DIR=s3://asecapt-certificates/certificates
export APP_QRCODES_UPLOAD_DIR=s3://asecapt-certificates/qrcodes

# Additional ASECAPT configuration
export ASECAPT_ENVIRONMENT=production
export ASECAPT_S3_REGION=sa-east-1
export ASECAPT_CERTIFICATE_BUCKET=asecapt-certificates

echo "✅ ASECAPT AWS environment variables exported successfully!"
echo "📍 Region: sa-east-1 (South America - São Paulo)"
echo "🪣 Bucket: asecapt-certificates"
echo "🏢 Environment: ${ASECAPT_ENVIRONMENT}"
echo ""
echo "Variables exported:"
echo "  - ASECAPT_AWS_ACCESS_KEY_ID"
echo "  - ASECAPT_AWS_SECRET_ACCESS_KEY" 
echo "  - ASECAPT_AWS_REGION"
echo "  - ASECAPT_AWS_S3_BUCKET_NAME"
echo "  - AWS_ACCESS_KEY_ID"
echo "  - AWS_SECRET_ACCESS_KEY"
echo "  - AWS_DEFAULT_REGION"
echo "  - AWS_S3_BUCKET_NAME"

# To use this file, run:
# source aws-env.sh
