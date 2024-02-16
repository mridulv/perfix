# Perfix

Perfix is a performance experimentation tool that allows you to conduct experiments against different data stores like MySQL, Redis, MongoDB, and DynamoDB. It provides a flexible and extensible framework for running performance experiments and analyzing the results.

## Features

- Conduct performance experiments against various data stores.
- Easily configure experiment parameters such as the number of rows, concurrent queries, and more.
- Run experiments locally or deploy them to a cloud environment.
- Analyze experiment results to identify performance bottlenecks and optimize system performance.

## Getting Started

### Prerequisites

- JDK 11 or higher
- SBT (Scala Build Tool)
- Docker (optional)

### Building the Project

To build the project, run the following command in the project root directory:

```bash
sbt compile
```

### Running the Project

To run the project locally, use the following command:

```
sbt run
```

### Publishing a Docker Image Locally

You can also publish a Docker image locally using the sbt-native-packager plugin. Run the following command to build and publish the Docker image:

```
sbt docker:publishLocal
```

### Running a Docker Container

After publishing the Docker image locally, you can run a Docker container based on the image. Use the following command to run the Docker container:

```
docker run -p 9000:9000 mridulverma/perfix:latest
```

### Configuration
You can configure various aspects of the experiment, such as the number of rows, concurrent queries, data store settings, etc., by editing the configuration files located in the conf directory.

### Contributing
Contributions are welcome! Please feel free to submit bug reports, feature requests, or pull requests on the GitHub repository.

### Acknowledgements
This project was inspired by the need for a flexible and extensible performance experimentation tool for evaluating different data store solutions. We would like to thank all contributors and users for their support and feedback.



