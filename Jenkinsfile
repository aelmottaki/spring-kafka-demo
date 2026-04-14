pipeline {
    agent any


    tools {
        maven 'M3'
        jdk 'jdk21'
    }

    parameters {
        booleanParam(name: 'STOP_SERVICE', defaultValue: false, description: 'Stop the running kafkademo service instead of starting it')
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

        stage('Deploy Artifact') {
            when {
                expression { !params.STOP_SERVICE }
            }
            steps {
                // Copy latest built jar to Desktop as kafkademo.jar
                sh '''
                    set -e
                    DEST_DIR="/Users/mac/Desktop"
                    mkdir -p "$DEST_DIR"
                    JAR_FILE=$(ls -t target/*.jar | head -n1)
                    if [ -z "$JAR_FILE" ]; then
                      echo "No JAR found in target/. Did the build succeed?" >&2
                      exit 1
                    fi
                    echo "Deploying $JAR_FILE to $DEST_DIR/kafkademo.jar"
                    cp "$JAR_FILE" "$DEST_DIR/kafkademo.jar"
                '''
            }
        }

        stage('Stop Service') {
            when {
                expression { params.STOP_SERVICE }
            }
            steps {
                sh '''
                    set +e
                    PID_FILE="/Users/mac/Desktop/kafkademo.pid"
                    if [ -f "$PID_FILE" ]; then
                      PID=$(cat "$PID_FILE")
                      if ps -p $PID > /dev/null 2>&1; then
                        echo "Stopping kafkademo (PID $PID)"
                        kill -TERM $PID
                        # wait up to 10s
                        for i in $(seq 1 10); do
                          if ps -p $PID > /dev/null 2>&1; then
                            sleep 1
                          else
                            break
                          fi
                        done
                        if ps -p $PID > /dev/null 2>&1; then
                          echo "Process still running, sending KILL"
                          kill -KILL $PID
                        fi
                      else
                        echo "No running process with PID $PID; removing stale PID file."
                      fi
                      rm -f "$PID_FILE"
                    else
                      echo "No PID file found; service may not be running."
                    fi
                '''
            }
        }

        stage('Start Service') {
            when {
                expression { !params.STOP_SERVICE }
            }
            steps {
                withEnv(["JAVA_HOME=${tool 'jdk21'}", "PATH=${tool 'jdk21'}/bin:${env.PATH}", "JENKINS_NODE_COOKIE=dontKillMe"]) {
                    sh '''
                        set -e
                        APP_JAR="/Users/mac/Desktop/kafkademo.jar"
                        LOG_OUT="/Users/mac/Desktop/kafkademo.out"
                        LOG_ERR="/Users/mac/Desktop/kafkademo.err"
                        PID_FILE="/Users/mac/Desktop/kafkademo.pid"

                        # Stop any previously running instance first
                        if [ -f "$PID_FILE" ]; then
                            OLD_PID=$(cat "$PID_FILE")
                            if ps -p $OLD_PID > /dev/null 2>&1; then
                                echo "Stopping existing kafkademo (PID $OLD_PID)"
                                kill -TERM $OLD_PID || true
                                sleep 3
                            fi
                            rm -f "$PID_FILE"
                        fi

                        echo "Starting kafkademo with JAVA_HOME=$JAVA_HOME"
                        nohup java -jar "$APP_JAR" > "$LOG_OUT" 2> "$LOG_ERR" &
                        echo $! > "$PID_FILE"

                        sleep 5

                        NEW_PID=$(cat "$PID_FILE")
                        if ps -p $NEW_PID > /dev/null 2>&1; then
                            echo "kafkademo started successfully as PID $NEW_PID"
                        else
                            echo "=== STDOUT ===" >&2
                            cat "$LOG_OUT" >&2
                            echo "=== STDERR ===" >&2
                            cat "$LOG_ERR" >&2
                            echo "Failed to start kafkademo" >&2
                            exit 1
                        fi
                    '''
                }
            }
        }
    }
}
