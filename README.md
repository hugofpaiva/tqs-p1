# Air Quality Dashboard

<!--
Not Working because of lack of ssl on server
![](http://34.89.73.181:9000/api/project_badges/quality_gate?project=com.hugopaiva%3Aair-quality-service)

![](http://34.89.73.181:9000/api/project_badges/measure?project=com.hugopaiva%3Aair-quality-service&metric=coverage)

![](http://34.89.73.181:9000/api/project_badges/measure?project=com.hugopaiva%3Aair-quality-service&metric=alert_status)

-->

A small application to provide details on air quality for a certain city/coordinates. 

This work aims to demonstrate the student's ability to test a platform through JUnit, Mockito, Selenium, etc.

Built with:

<table>
  <tr>
    <td valign="top" style="width:100px">
      <div align="center">
      <img style="margin: 20px" src="https://profilinator.rishav.dev/skills-assets/angularjs-original.svg" alt="Angular" height="50" /> 
      <p>Angular</p>
      </div>
    </td>
    <td valign="top" style="width:100px">
      <div align="center">
      <img style="margin: 20px" src="https://profilinator.rishav.dev/skills-assets/springio-icon.svg" alt="Spring" height="50" /> 
      <p>Spring Boot</p>
      </div>
    </td>
  </tr>
</table> 

## Architecture

<p align="center">
  <img  src="/report/architecture.png">
</p>

### Components

- [**Web Application (Angular)**](./web-application)
- [**Service (Spring Boot)**](./air-quality-service)

## Tests

## How to Run

To run the system, _Docker Compose_ must be installed and updated.

That said, the steps are as follows:

1. Change the _Angular_ environment to do the requests of the API to your machine

   Example to run on `localhost`. If you are hosting in another machine, the IP address of it should be in the place of `localhost`.
   
     - Change the [environment.prod.ts](./web-application/src/environments/environment.prod.ts) file as the following:
     
      ```
      import { HttpHeaders } from '@angular/common/http';
      export const environment = {
        httpOptions: {
          headers: new HttpHeaders({'Content-Type': 'application/json'})
        },
        baseURL: 'http://localhost:8080/api/',
        production: true
      };
      ```
    
2. Compile services for the execution of _containers Docker_, running at the root of the repository:
   
    ```
    $ docker-compose build
    ```
    
3. Start the _containers_:
    
    ```
    $ docker-compose up -d
    ```
    
The **Web application** will be available at: [localhost](http://localhost)

## Deploy

The system was made available through the _Google Cloud Platform_ and according to [this guide](https://cloud.google.com/community/tutorials/docker-compose-on-container-optimized-os), using _free tier_. Briefly, a _VM_ was created, the repository was cloned, the `baseURL` was changed in the `environment.prod.ts` file to match the machine's IP and the _Docker Compose_ was executed according to the guide and with the _1.27.4_ version to support the _3.8_ version of the _Compose_ file. Finally, in the _Firewall_ definitions, ports 80 and 8080 were opened to allow access to the _web_ application and service, respectively.

**Web application available at:** [35.246.89.129](http://35.246.89.129)

**API docs available at:** [35.246.89.129:8080/api/swagger-ui/index.html](http://35.246.89.129:8080/api/swagger-ui/index.html)

## Details

You can find all the details in the [Work Report](./report/relatorio.pdf). Be aware, the Work Report was made in Portuguese.


