import {Injectable} from '@angular/core';
import {StsClientConfig} from '../env/sts-client-config.service';

import * as Keycloak_ from 'keycloak-js';

export const Keycloak = Keycloak_;

@Injectable()
export class KeycloakService {
  private keycloak: any;

  public initSuccess: boolean;
  public isAuthenticated: boolean;

  constructor(private clientConfig: StsClientConfig) {
  }

  public init(): void {
    const keycloak = Keycloak({
      'url': this.clientConfig.getKeycloakAuthUrl(),
      'realm': this.clientConfig.getKeycloakRealm(),
      'clientId': this.clientConfig.getKeycloakClientId()
    });

    keycloak.onTokenExpired = this.onTokenExpired;
    keycloak.loginRequired = true;

    keycloak.init({flow: 'implicit'})
      .success(authenticated => {
        this.initSuccess = true;
        this.isAuthenticated = authenticated;
      })
      .error(e => {
        this.initSuccess = false;
        console.log(e);
      });

    this.keycloak = keycloak;
  }

  get token() {
    return this.keycloak.token;
  }

  login() {
    this.keycloak.login({scope: this.clientConfig.getKeycloakScope()});
  }

  logout() {
    this.keycloak.logout();
  }

  get tokenParsed() {
    return this.keycloak.tokenParsed;
  }

  get hasToken(): boolean {
    return this.keycloak.token !== null && this.keycloak.token !== undefined;
  }

  private onTokenExpired() {
    console.info('Access token expired.');
    this.keycloak.logout();
  }
}
