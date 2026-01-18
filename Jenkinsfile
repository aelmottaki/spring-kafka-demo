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


		stage('Test & Coverage') {
			steps {
				sh 'mvn test' // JaCoCo plugin automatically instruments and generates coverage
			}
			post {
				always {
					junit '**/target/surefire-reports/TEST-*.xml'
					jacoco execPattern: '**/target/jacoco.exec', classPattern: '**/target/classes', sourcePattern: '**/src/main/java', inclusionPattern: '**/*.class'
				}
			}
		}

		stage('SonarQube Analysis') {
			steps {
				// 'SonarQube' must match the name configured in Jenkins → with token/auth
				withSonarQubeEnv('SonarQube') {
					// Using the Sonar Scanner plugin step
					sonar(
						projectKey: 'spring-kafka-demo',
						projectName: 'Spring Kafka Demo',
						sources: 'src/main/java',
						language: 'java',
						java: '', // optional: path to Java home if needed
						extraProperties: [
							'sonar.coverage.jacoco.xmlReportPaths': 'target/site/jacoco/jacoco.xml'
						]
					)
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
