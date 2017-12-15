import {Injectable} from "@angular/core";
import {Headers, Http, Request, RequestOptions, RequestOptionsArgs, Response} from "@angular/http";
import {KeycloakService} from "./keycloak.service";
import {Observable} from "rxjs/Rx";

@Injectable()
export class KeycloakHttp {
  constructor(private http: Http, private keycloak: KeycloakService) {
  }

  request(url: string | Request, options?: RequestOptionsArgs): Observable<Response> {
    let authOptions: RequestOptions = new RequestOptions({headers: new Headers({'Authorization': `Bearer ${this.keycloak.token}`})});
    let mergedOptions = new RequestOptions().merge(options).merge(authOptions);

    console.log('request')

    return this.http.request(url, mergedOptions);
  }
}
