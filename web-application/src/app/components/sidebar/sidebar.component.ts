import { Component, OnInit } from '@angular/core';

declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
}
export const ROUTES: RouteInfo[] = [
    { path: '/actual-measurement', title: 'Actual Measurements',  icon: 'fas fa-street-view', class: '' },
    { path: '/cache', title: 'Cache',  icon: 'fas fa-history', class: '' },
    { path: '/icons', title: 'Icons',  icon: 'now-ui-icons education_atom', class: '' },
    { path: '/user-profile', title: 'User Profile',  icon: 'now-ui-icons users_single-02', class: '' },
    { path: '/table-list', title: 'Table List',  icon: 'now-ui-icons design_bullet-list-67', class: '' },
    { path: '/typography', title: 'Typography',  icon: 'now-ui-icons text_caps-small', class: '' },

];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  menuItems: any[];

  constructor() { }

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
  }
  isMobileMenu() {
      if ( window.innerWidth > 991) {
          return false;
      }
      return true;
  }
}
