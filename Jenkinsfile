pipeline {
  agent {
    docker {
      image 'gradle:jdk8'
      args '-v $HOME/.gradle:/home/gradle/.gradle'
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
        sh '''cd master
gradle -i clean build'''
      }
    }

    stage('Reports') {
      steps {
        junit(allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml')
        jacoco(execPattern: '**/**.exec', classPattern: '**/classes', sourcePattern: '**/src/main/java', sourceInclusionPattern: '**/*.java,**/*.groovy,**/*.kt,**/*.kts')
        sh '''cd master
gradle sonarqube'''
        recordIssues tools: [codeNarc(pattern: '**/codenarc/test.xml')]
      }
    }

  }
}