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

		stage('SonarQube Analysis') {
			steps {
				sh """
            mvn sonar:sonar \
            -Dsonar.host.url=http://sonarqube:9000 \
            -Dsonar.login=sqa_38564625c23e9b9a4b4e2f66d241e4b206070aa0
      		  """
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
