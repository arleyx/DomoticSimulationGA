import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';

import { SocketService } from './socket.service';
import { InfoService } from './info.service';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [SocketService, InfoService],
  bootstrap: [AppComponent]
})
export class AppModule { }
