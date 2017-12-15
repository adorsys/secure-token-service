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

  logout() {
    this.keycloak.logout();
  }

  getSampleData() {
    console.log(`get sample data from ${environment.serviceUrl}`);

    this.http.request(environment.serviceUrl)
      .subscribe(response => {
        this.sampleData = response.text();
        console.log(`got sample data: "${this.sampleData}"`);
      });
  }

  getRealmRoles() {
    return this.keycloak.realmAccess;
  };

  getResourceRoles() {
    return this.keycloak.resourceAccess;
  };

  getSecretClaims() {
    if (this.keycloak.tokenParsed) {
      return this.keycloak.tokenParsed.secretClaim;
    }
  }
}
