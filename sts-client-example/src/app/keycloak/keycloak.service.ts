import { Injectable } from '@angular/core';
import { StsClientConfig } from '../env/sts-client-config.service';
import Keycloak from 'keycloak-js';

@Injectable()
export class KeycloakService {
  private keycloak: any;

  public initSuccess: boolean;
  public isAuthenticated: boolean;

  constructor(private clientConfig: StsClientConfig) {}

  public init(): void {
    const keycloak = new Keycloak({
      url: this.clientConfig.getKeycloakAuthUrl(),
      realm: this.clientConfig.getKeycloakRealm(),
      clientId: this.clientConfig.getKeycloakClientId()
    });

    keycloak.onTokenExpired = this.onTokenExpired;
    keycloak.loginRequired = true;

    keycloak
      .init({ flow: 'implicit' })
      .then(authenticated => {
        console.log('on success' + authenticated);
        this.isAuthenticated = authenticated;
        this.initSuccess = authenticated;
      })
      .catch(err => {
        console.log('on error' + err);
      });

    this.keycloak = keycloak;
  }

  get token() {
    return this.keycloak.token;
  }

  login() {
    this.keycloak.login({ scope: this.clientConfig.getKeycloakScope() });
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
