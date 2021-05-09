import { Routes } from '@angular/router';

import { DashboardComponent } from '../../dashboard/dashboard.component';
import { CacheComponent } from '../../cache/cache.component';


export const AdminLayoutRoutes: Routes = [
    { path: 'actual-measurement',      component: DashboardComponent },
    { path: 'cache',      component: CacheComponent }
];
