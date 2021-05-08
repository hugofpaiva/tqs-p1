import { Component, OnInit, OnDestroy } from '@angular/core';
import {Request} from '../communication/models/request';
import {RequestService} from '../communication/services/request/request.service';
import { DatePipe } from '@angular/common';
import {interval} from 'rxjs';

@Component({
  selector: 'app-cache',
  templateUrl: './cache.component.html',
  styleUrls: ['./cache.component.css'],
  providers: [DatePipe]
})
export class CacheComponent implements OnInit, OnDestroy {
  requests: Request[] = [];
  hits: number;
  nRequests: number;
  misses: number;
  page: number = Number(1);
  private subscription;

  constructor(private requestService: RequestService, public datepipe: DatePipe) { }

  ngOnInit(): void {
    this.getRequests();
    this.getRequestsStats();
    this.subscription = interval(5000).subscribe(() => {
      this.getRequests(this.page - 1);
      this.getRequestsStats();
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getPage(event) {
    this.getRequestsStats();
    this.getRequests(event - 1);
  }

  getRequests(pageNo: number = 0, pageSize: number = 8): void {
    this.requestService.getRequests(pageNo, pageSize).subscribe(requests => {
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
