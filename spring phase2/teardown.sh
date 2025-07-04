minikube kubectl -- delete service wallet-service
minikube kubectl -- delete service  marketplace-service
minikube kubectl -- delete service  account-service
minikube kubectl -- delete service  h2db-service

minikube kubectl -- delete deployment  wallet-service
minikube kubectl -- delete deployment  marketplace-service
minikube kubectl -- delete deployment  account-service
minikube kubectl -- delete deployment  h2db-service

