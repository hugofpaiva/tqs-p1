import { Routes } from '@angular/router';

import { DashboardComponent } from '../../dashboard/dashboard.component';
import { CacheComponent } from '../../cache/cache.component';
import {MeasurementsDayComponent} from '../../measurements-day/measurements-day.component';


export const AdminLayoutRoutes: Routes = [
    { path: 'actual-measurement',      component: DashboardComponent },
    { path: 'measurement-by-date', component: MeasurementsDayComponent },
    { path: 'cache',      component: CacheComponent }
];
