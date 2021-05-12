import { Component, OnInit, OnDestroy } from '@angular/core';
import {Request} from '../communication/models/request';
import {RequestService} from '../communication/services/request/request.service';
import { DatePipe } from '@angular/common';
import {interval} from 'rxjs';
import {NgxSpinnerService} from 'ngx-spinner';

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
  private requestingTable = false;
  private requestingStats = false;
  private subscription;

  constructor(private requestService: RequestService, public datepipe: DatePipe, private spinner: NgxSpinnerService) { }

  ngOnInit(): void {
    this.getRequests();
    this.getRequestsStats();
    this.spinner.show();
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
    this.requestingTable = true;
    this.requestService.getRequests(pageNo, pageSize).subscribe(requests => {
      // @ts-ignore
      this.requests = requests;
      this.requestingTable = false;
      this.stopSpinner();
    });
  }

  getRequestsStats(): void {
    this.requestingStats = true;
    this.requestService.getRequestsStats().subscribe(stats => {
      this.hits = stats.hits;
      this.nRequests = stats.nRequests;
      this.misses = stats.misses;
      this.requestingStats = false;
      this.stopSpinner();
    });
  }

  stopSpinner(): void {
    if (!this.requestingStats && !this.requestingTable) {
      this.spinner.hide();
    }
  }

}
