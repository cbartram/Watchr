    # Go parameters
    GOCMD=go
    GOBUILD=$(GOCMD) build
    GOCLEAN=$(GOCMD) clean
    GOTEST=$(GOCMD) test
    GOGET=$(GOCMD) get
    BINARY_NAME=mybinary
    BINARY_UNIX=$(BINARY_NAME)_unix

    all: test build
    build:
            $(GOBUILD) -o $(BINARY_NAME) -v
    test:
            $(GOTEST) -v ./...
    clean:
            $(GOCLEAN)
            rm -f $(BINARY_NAME)
            rm -f $(BINARY_UNIX)
    run:
            $(GOBUILD) -o $(BINARY_NAME) -v ./...
            ./$(BINARY_NAME)
    deps:
            $(GOGET) github.com/cbartram/watchr
            $(GOGET) github.com/cbartram/watchr


    # Cross compilation
    build-linux:
            CGO_ENABLED=0 GOOS=linux GOARCH=amd64 $(GOBUILD) -o $(BINARY_UNIX) -v
    docker-build:
            docker run --rm -it -v "$(GOPATH)":/go -w /go/src/watchr/cbartram/watchr golang:latest go build -o "$(BINARY_UNIX)" -v