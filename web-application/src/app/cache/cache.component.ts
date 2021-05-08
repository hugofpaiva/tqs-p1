import { Component, OnInit } from '@angular/core';
import {Request} from '../communication/models/request';
import {RequestService} from '../communication/services/request/request.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-cache',
  templateUrl: './cache.component.html',
  styleUrls: ['./cache.component.css'],
  providers: [DatePipe]
})
export class CacheComponent implements OnInit {
  requests: Request[] = [];
  hits: number;
  nRequests: number;
  misses: number;
  page: number = Number(0);

  constructor(private requestService: RequestService, public datepipe: DatePipe) { }

  ngOnInit(): void {
    this.getRequests();
    this.getRequestsStats();
  }

  getPage(event) {
    this.getRequestsStats();
    this.getRequests(event - 1);
    console.log(this.page);
  }

  getRequests(pageNo: number = 0, pageSize: number = 8): void {
    this.requestService.getRequests(pageNo, pageSize).subscribe(requests => {
      console.log(requests);
      // @ts-ignore
      this.requests = requests;
    });
  }

  getRequestsStats(): void {
    this.requestService.getRequestsStats().subscribe(stats => {
      this.hits = stats.hits;
      this.nRequests = stats.nRequests;
      this.misses = stats.misses;
    });
  }

}
