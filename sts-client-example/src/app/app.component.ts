import {Component} from '@angular/core';
import {KeycloakService} from "./keycloak/keycloak.service";
import {KeycloakHttp} from "./keycloak/keycloak.http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  sampleData: any;

  constructor(private http: KeycloakHttp, private keycloak: KeycloakService) {
  }

  login() {
    this.keycloak.login();
  }

  logout() {
    this.keycloak.logout();
  }

  get isLoggedIn() {
    return this.keycloak.isAuthenticated;
  }

  get initSuccess() {
    return this.keycloak.initSuccess;
  }

  getSampleData() {
    this.http.request(environment.serviceUrl)
      .subscribe(response => {
        this.sampleData = {
          status: response.status,
          text: response.text()
        };
      });
  }

  getSecretClaims() {
    if (this.keycloak.tokenParsed) {
      return this.keycloak.tokenParsed.secretClaim;
    }
  }
}
