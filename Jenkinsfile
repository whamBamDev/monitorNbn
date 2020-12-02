pipeline {
  agent {
    docker {
      image 'gradle:jdk8'
      args '-v $HOME/.gradle:/root/.gradle'
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
        sh '''
ls -l
cd master
gradle build'''
      }
    }

  }
}