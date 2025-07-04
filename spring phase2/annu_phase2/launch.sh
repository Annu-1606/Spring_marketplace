minikube start --driver=docker

eval $(minikube docker-env)

chmod 777 launch.sh

chmod 777 stop.sh
 
cd h2db/

docker build -t h2db-service:v7 .

minikube kubectl -- create deployment h2db-service --image=h2db-service:v7

minikube kubectl -- expose deployment h2db-service --type=ClusterIP --port=9082

minikube kubectl -- apply -f hpa.yaml
 
 
cd ../account-service/

docker build -t account-service:v7 .

minikube kubectl -- create deployment account-service --image=account-service:v7

minikube kubectl -- expose deployment account-service --type=LoadBalancer --port=8080

minikube kubectl -- apply -f hpa.yaml
 
 
cd ../marketplace-service/

docker build -t marketplace-service:v7 .

minikube kubectl -- create deployment marketplace-service --image=marketplace-service:v7

minikube kubectl -- expose deployment marketplace-service --type=LoadBalancer --port=8081

minikube kubectl -- apply -f hpa.yaml
 
 
cd ../wallet-service/

docker build -t wallet-service:v7 .

minikube kubectl -- create deployment wallet-service --image=wallet-service:v7

minikube kubectl -- expose deployment wallet-service --type=LoadBalancer --port=8082

minikube kubectl -- apply -f hpa.yaml 

cd ../
 
pkill -f port-forward

sleep 60s

minikube kubectl port-forward service/wallet-service 8082:8082 &

minikube kubectl port-forward service/marketplace-service 8081:8081 &

minikube kubectl port-forward service/account-service 8080:8080 &

minikube tunnel &

wait

