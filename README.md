# About

This is a Spring Boot, Java application that exposes REST API endpoints to access vending machine functionality. It supports querying for products available and purchasing those products.
It has to ensure change is available and the correct amount is returned. The application uses in memory database H2, where it keeps the record of product and cash balances.

See flowcharts under resources/flowcharts for more details.
# Build

To build the app GitHub actions is used, where few steps, like packaging and testing are ran. From this public GitHub repository : https://github.com/Sandile100/vending-machine-api

# Docker Hub integration

Successful Build creates a docker image as part of CI/CD for the project and uploads it to public Docker Hub repository here : https://hub.docker.com/repository/docker/sandilembatha/vending-machine-api

# Runing the project

To run the project, a docker compose file has been added to the root folder of UI projects here (https://github.com/Sandile100/vending-machine-ui) because the UI projects depends on this backend API. This requires Docker to be install on the host machine. And the command below should be issued on the same directory as the docker-compose.yml file

Command : docker compose -f .\docker-compose.yml up

Alternatively one can checkout the project and run it locally on port 8080.

The application will then be served on http:localhost:8080

