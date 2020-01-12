# Watchr

Continuously monitors your docker images for updates and changes.

## Description

Monitors the tags on your image on Docker Hub and will pull and start a new container
when a new version of the tag is pushed than the currently running version.

## Badges

![badge-issues](https://img.shields.io/github/issues/cbartram/Watchr)
![badge-fork](https://img.shields.io/github/forks/cbartram/Watchr)
![badge-stars](https://img.shields.io/github/stars/cbartram/Watchr)
![badge-license](https://img.shields.io/github/license/cbartram/Watchr)
![badge-twitter](https://img.shields.io/twitter/url?style=social)

## Installation

Watchr is packaged as a Docker Container you can pull it from docker hub by using the following command:

```
$ docker run -d \
    --name watchr \
    -v /var/run/docker.sock:/var/run/docker.sock \
    cbartram/watchr
```

## Usage

Coming Soon

## Tests

Coming Soon

## Support

If you have any issues running, deploying or using this software then please feel free to create a [new Github Issue](https://github.com/cbartram/Watchr/issues) on this board. 

## Roadmap

The future road map for this project includes:

- Support for updating images where tags are **not** semantically versioned

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors & Contributors

 - Author - **Christian Bartram**
 
## Built With

- **Java** - Programming Language Used
- **Spring** - Java Framework
- **Docker** - Containerization library
- **Maven** - Dependency management and build tool

## License

This project is licensed under the [MIT license](https://github.com/cbartram/Watchr/blob/master/LICENSE). Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software.

## Project status

This project is **ACTIVELY** being developed.