import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { StatisticsComponent } from './statistics/statistics.component';
import { FormsModule } from '@angular/forms';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { StatisticsService } from './services/statistics.service';
import { HttpClientModule } from '@angular/common/http';
 
  
@NgModule({
  declarations: [
    AppComponent,
    StatisticsComponent,
    NavBarComponent,
  ],
  imports: [ 
    BrowserModule,NgbModule,FormsModule,HttpClientModule

  ],
  providers: [StatisticsService],
  bootstrap: [AppComponent]
})
export class AppModule { }
