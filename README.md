# multi-stack-poc
Single repo, multiple CloudFormation stacks (proof of concept).

![Deploy CloudFormation Stack](https://github.com/gustav-sundin-wcar/multi-stack-poc/workflows/Deploy%20CloudFormation%20Stack/badge.svg)

## Setup

### Github Secrets

Maybe you want to clone this repo and set it up for yourself? Then you need to generate a set of AWS credentials and add as Github Secrets (Settings -> Secrets) using the following keys:
* AWS_ACCESS_KEY_ID
* AWS_SECRET_ACCESS_KEY

### S3 Bucket
The S3 bucket where the deploy pipeline uploads stuff was created manually in AWS. In the future we can add a step in the pipeline to make sure it exists and create it otherwise.
