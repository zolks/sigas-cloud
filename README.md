# sigas-cloud

mvn clean install
eval $(minikube docker-env)
docker build -t sixbell/sigas-cloud:v1 .