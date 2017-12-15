import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {KeycloakHttp} from "./keycloak/keycloak.http";
import {KeycloakService} from "./keycloak/keycloak.service";
import {HttpModule} from "@angular/http";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [
    KeycloakHttp,
    KeycloakService,
  ],
  exports: [
    HttpModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
