import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {KeycloakHttp} from "./keycloak/keycloak.http";
import {KeycloakService} from "./keycloak/keycloak.service";
import {HttpModule} from "@angular/http";
import {StsClientConfig} from "./env/sts-client-config.service";
import {AppConfig} from "./env/app-config";
import {HttpClientModule} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [
    AppConfig, {
      provide: APP_INITIALIZER,
      useFactory: (config: AppConfig) => () => config.load(),
      deps: [AppConfig], multi: true
    },
    StsClientConfig,
    KeycloakHttp,
    KeycloakService,
  ],
  exports: [
    HttpModule,
    HttpClientModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
