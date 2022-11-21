pipeline {
    agent any

    stages {
        stage('pull code') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'gitee-auth-password', url: 'https://gitee.com/myczb/gulimall.git']]])
            }
        }
        stage('build project') {
            steps {
                sh 'mvn clean package -Dmaven.test.skip=true'
            }
        }
    }
}