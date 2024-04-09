import {Injectable} from '@angular/core';
import {AppConfig} from './app-config';

@Injectable()
export class StsClientConfig {

  constructor(private appConfig: AppConfig) { }

  public getKeycloakAuthUrl(): string {
    return this.appConfig.getValue('NG_KEYCLOAK_AUTH_URL');
  }

  public getKeycloakRealm(): string {
    return this.appConfig.getValue('NG_KEYCLOAK_REALM');
  }

  public getKeycloakClientId(): string {
    return this.appConfig.getValue('NG_KEYCLOAK_CLIENT_ID');
  }

  public getKeycloakScope(): string {
    return this.appConfig.getValue('NG_KEYCLOAK_SCOPE');
  }

  public getServiceUrl(): string {
    return this.appConfig.getValue('NG_SERVICE_URL');
  }

  public getSecretUrl(): string {
    return this.appConfig.getValue('NG_SECRET_URL');
  }
}
