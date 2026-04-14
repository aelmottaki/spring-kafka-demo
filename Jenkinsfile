pipeline {
    agent any


    tools {
        maven 'M3'
        jdk 'jdk21'
    }

	stages {

		stage('Checkout') {
			steps {
				git branch: 'main',
				url: 'https://github.com/aelmottaki/spring-kafka-demo'
			}
		}

  stage('Check Java') {
            steps {
                sh 'echo Using JAVA_HOME=$JAVA_HOME'
                sh 'java -version'
                sh 'mvn -v'
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
