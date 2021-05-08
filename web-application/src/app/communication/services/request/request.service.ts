import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { RequestStats } from '../../models/requestsStats';

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  constructor(private http: HttpClient) { }

  getRequests(pageNo: number = 0, pageSize: number = 10): Observable<Request[]> {
    const url = environment.baseURL + 'requests?pageNo=' + pageNo + '&pageSize=' + pageSize;
    return this.http.get<Request[]>(url);
  }

  getRequestsStats(): Observable<RequestStats> {
    const url = environment.baseURL + 'requests-stats';
    return this.http.get<RequestStats>(url);
  }

}
