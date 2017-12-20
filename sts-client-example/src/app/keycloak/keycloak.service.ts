import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {isNullOrUndefined, isUndefined} from "util";

declare let Keycloak: any;

@Injectable()
export class KeycloakService {
  private keycloak: any;

  public initSuccess: boolean;
  public isAuthenticated: boolean;

  constructor() {
    const keycloak = new Keycloak({
      "url": environment.keycloak.url,
      "realm": environment.keycloak.realm,
      "clientId": environment.keycloak.clientId
    });

    keycloak.onTokenExpired = this.onTokenExpired;
    keycloak.loginRequired = true;

    keycloak.init({flow: 'implicit'})
      .success(authenticated => {
        this.initSuccess = true;
        this.isAuthenticated = authenticated;
      })
      .error(function () {
        this.initSuccess = false;
      });

    this.keycloak = keycloak;
  }

  get token() {
    return this.keycloak.token;
  }

  login() {
    this.keycloak.login({scope: environment.keycloak.scope});
  }

  logout() {
    this.keycloak.logout();
  }

  get tokenParsed() {
    return this.keycloak.tokenParsed;
  }

  get hasToken(): boolean {
    return !isNullOrUndefined(this.keycloak.token);
  }

  private onTokenExpired() {
    console.info('Access token expired.');
    this.keycloak.logout();
  }
}
