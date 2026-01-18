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
				// Inject the token stored in Jenkins credentials (type: Secret Text)
				withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'TOKEN')]) {
					withSonarQubeEnv('SonarQube') {
						// Using the Sonar Scanner plugin step
						sonar(
							projectKey: 'spring-kafka-demo',
							projectName: 'Spring Kafka Demo',
							sources: 'src/main/java',
							language: 'java',
							java: '', // optional: path to Java home if needed
							extraProperties: [
								'sonar.coverage.jacoco.xmlReportPaths': 'target/site/jacoco/jacoco.xml',
								'sonar.login': "${sqa_38564625c23e9b9a4b4e2f66d241e4b206070aa0}" // inject token here
							]
						)
					}
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
