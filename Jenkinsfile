pipeline {
	agent any

	tools {
		maven 'M3'
	}

	stages {

		stage('Checkout') {
			steps {
				git branch: 'main',
				url: 'https://github.com/aelmottaki/spring-kafka-demo'
			}
		}

		stage('Build') {
			steps {
				sh 'mvn clean compile'
			}
		}

		stage('Test') {
			steps {
				sh 'mvn test'
			}
			post {
				always {
					junit '**/target/surefire-reports/TEST-*.xml'
				}
			}
		}

		stage('Package') {
			steps {
				sh 'mvn -DskipTests package'
			}
			post {
				success {
					archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
				}
			}
		}
	}
}
