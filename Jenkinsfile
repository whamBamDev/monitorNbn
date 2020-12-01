pipeline {
  agent {
    docker {
      image 'gradle:jdk8'
    }

  }
  stages {
    stage('Init') {
      steps {
        echo 'Hello World'
      }
    }

    stage('Build') {
      steps {
        sh '''cd naster
gradle build'''
      }
    }

  }
}