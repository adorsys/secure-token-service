import {Component, OnInit} from '@angular/core';
import {KeycloakService} from './keycloak/keycloak.service';
import {KeycloakHttp} from './keycloak/keycloak.http';
import 'rxjs';
import {StsClientConfig} from './env/sts-client-config.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  sampleData: any;

  constructor(private http: KeycloakHttp, private keycloak: KeycloakService, private clientConfig: StsClientConfig) {
  }

  ngOnInit(): void {
    this.keycloak.init();
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
    this.http.request(this.clientConfig.getServiceUrl())
      .subscribe(response => {
        this.sampleData = {
          status: response.status,
          text: response.text()
        };
      });
  }

  getSecret() {
    this.http.request(this.clientConfig.getSecretUrl())
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
