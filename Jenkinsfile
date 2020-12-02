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

    stage('Reports') {
      steps {
        junit(allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml')
        jacoco(execPattern: '**/**.exec', classPattern: '**/classes', sourcePattern: '**/src/main/java', sourceInclusionPattern: '**/*.java,**/*.groovy,**/*.kt,**/*.kts')
      }
    }

  }
}