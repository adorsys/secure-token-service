import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { error } from '@angular/compiler-cli/src/transformers/util';

@Injectable()
export class AppConfig {
  private keys: string[] = null;
  private env: {
    [key: string]: string;
  } = null;

  constructor(private http: HttpClient) {}

  /**
   * Use to get a property of the env file
   */
  public getValue(key: string) {
    return this.env[key];
  }

  /**
   * Get all env keys
   * @returns {string[]}
   */
  public getKeys(): string[] {
    return this.keys;
  }

  public getConfig() {
    return Object.assign({}, this.env);
  }

  /**
   * This method loads "env.json" to get the current working environment variables
   */
  public load() {
    return new Promise(resolve => {
      this.tryToGetEnvJson(resolve);
    });
  }

  private tryToGetEnvJson(resolve) {
    this.http.get<any>(environment.envJson).subscribe({
      next: envResponse => {
        this.env = envResponse;
        this.keys = this.extractKeys();
        resolve(true);
        console.log(`Configuration file "${environment.envJson}" is read successfully`);
        return true;
      },
      error: error => {
        console.log(`Configuration file "${environment.envJson}" could not be read`);
        resolve(true);
        return error;
      }
    });
  }

  private extractKeys(): string[] {
    const keys: string[] = [];

    for (const key in this.env) {
      keys.push(key);
    }

    return keys;
  }
}
