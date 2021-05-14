import { HttpHeaders } from '@angular/common/http';
export const environment = {
  httpOptions: {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  },
  baseURL: 'http://host.docker.internal:8080/api/',
  production: true
};
