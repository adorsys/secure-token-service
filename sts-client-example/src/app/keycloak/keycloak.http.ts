import {Injectable} from '@angular/core';
import {HttpHeaders, HttpClient} from '@angular/common/http';
import {KeycloakService} from './keycloak.service';
import {Observable} from 'rxjs';

@Injectable()
export class KeycloakHttp {
  constructor(private http: HttpClient, private keycloak: KeycloakService) {
  }

  request(url: string): Observable<any> {
    const headers = new HttpHeaders();
    if (this.keycloak.hasToken) {
      headers.append('Authorization', `Bearer ${this.keycloak.token}`);
    }

    return this.http.get(url, { headers });
  }
}
