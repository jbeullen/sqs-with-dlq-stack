#!/usr/bin/env bash
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo '__________________  _________                                                        '
echo '\______   \_____  \ \_   ___ \                                                       '
echo ' |     ___//   |   \/    \  \/                                                       '
echo ' |    |   /    |    \     \____                                                      '
echo ' |____|   \_______  /\______  /                                                      '
echo '                  \/        \/                                                       '
echo '                                      .__  __  .__                  .___.__          '
echo '  ____________ ______         __  _  _|__|/  |_|  |__             __| _/|  |   ______'
echo ' /  ___/ ____//  ___/  ______ \ \/ \/ /  \   __\  |  \   ______  / __ | |  |  / ____/'
echo ' \___ < <_|  |\___ \  /_____/  \     /|  ||  | |   Y  \ /_____/ / /_/ | |  |_< <_|  |'
echo '/____  >__   /____  >           \/\_/ |__||__| |___|  /         \____ | |____/\__   |'
echo '     \/   |__|    \/                                \/               \/          |__|'
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
# Var declaration
environment=${environment:-dev}
version=${version:-1.0.0-SNAPSHOT}
deployment_bucket=${deployment_bucket:-upgrade-it-deployment}
project=${project:-poc}
application=${application:-sqs-with-dlq}

# Parse args --name value
while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        param="${1/--/}"
        declare $param="$2"
        # echo $1 $2 // Optional to see the parameter:value result
   fi

  shift
done

# Echo Script variables
echo "Environment: ${environment}"
echo "Version: ${version}"
echo "Deployment S3 Bucket: Name: ${deployment_bucket}"
echo "Deployment S3 Bucket: Path: /applications/${project}-${application}/${version}"
echo "Cloudformation Stack: Name: ${project}-${application}-${environment}"
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo 'Creating deployment package...'
mkdir ${version}
mkdir ${version}/lambda
mkdir ${version}/cloudformation
cp sqs-stack-with-dlq.yaml ${version}/cloudformation
cp ../producer/target/*-aws.jar ${version}/lambda
cp ../consumer/target/*-aws.jar ${version}/lambda
cp ../error/target/*-aws.jar ${version}/lambda
echo '...done'
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo 'Overview of the artifacts:'
ls -alFR ${version}
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo 'Using AWS CLI:'
aws --version
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo 'Uploading artifacts...'
aws s3 cp ${version} s3://${deployment_bucket}/applications/${project}-${application}/${version}/ --recursive
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo 'Deploying stack...'
aws cloudformation deploy \
--stack-name ${project}-${application}-${environment} \
--template-file ${version}/cloudformation/sqs-stack-with-dlq.yaml \
--capabilities CAPABILITY_NAMED_IAM \
--parameter-overrides "Environment=${environment}" "Version=${version}" "DeploymentBucketName=${deployment_bucket}" \
--tags "Project=${project}" "Application=${application}" "Environment=${environment}" "Version=${version}"
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo 'Cleanup deployment package...'
rm -fr ${version}
echo '...done'
echo '-----------------------------------------------------------------------------------------------------------------------------------------'
echo 'Overview of working directory:'
ls -alF .