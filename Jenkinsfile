node {
 stage('checkout') {
  checkout scm
 }
 stage('deploy') {
  echo 'branch name ' + env.BRANCH_NAME

  if (env.BRANCH_NAME.startsWith("feature_")) {
   echo "Deploying to Dev environment after build"
  } else if (env.BRANCH_NAME.startsWith("release_")) {
   echo "Deploying to Stage after build and Dev Deployment"
  } else if (env.BRANCH_NAME.startsWith("master")) {
   echo "Deploying to PROD environment"
  }

  sh ""
  "chmod +x HelloWorld.sh
  . / HelloWorld.sh ""
  "

 }
}