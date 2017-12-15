import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {isNullOrUndefined, isUndefined} from "util";

declare let Keycloak: any;

@Injectable()
export class KeycloakService {
  private keycloak: any;

  constructor() {
    let keycloak = new Keycloak({
      "url": environment.keycloak.url,
      "realm": environment.keycloak.realm,
      "clientId": environment.keycloak.clientId
    });

    keycloak.onTokenExpired = this.onTokenExpired;
    keycloak.loginRequired = true;

    keycloak.init({flow: 'implicit'})
      .success(initSuccess)
      .error(function () {
        window.location.reload();
      });

    function initSuccess(authenticated) {
      if (!authenticated) {
        keycloak.login({scope: environment.keycloak.scope});
      } else {
        console.log('TODO: do something')
      }
    }

    this.keycloak = keycloak;
  }

  get token() {
    return this.keycloak.token;
  }

  logout() {
    this.keycloak.logout();
  }

  get tokenParsed() {
    return this.keycloak.tokenParsed;
  }

  get realmAccess() {
    return this.keycloak.realmAccess;
  }

  get resourceAccess() {
    return this.keycloak.resourceAccess;
  }

  get hasToken(): boolean {
    return !isNullOrUndefined(this.keycloak.token);
  }

  private onTokenExpired() {
    console.info('Access token expired.');
    this.keycloak.logout();
  }
}
