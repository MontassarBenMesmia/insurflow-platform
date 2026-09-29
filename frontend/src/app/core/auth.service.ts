import { Injectable, signal } from '@angular/core';
import Keycloak from 'keycloak-js';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = new Keycloak(environment.keycloak);
  readonly authenticated = signal(false);
  readonly username = signal('');
  readonly identityAvailable = signal(true);

  async initialize(): Promise<void> {
    try {
      const authenticated = await this.keycloak.init({ onLoad: 'check-sso', pkceMethod: 'S256', checkLoginIframe: false });
      this.authenticated.set(authenticated);
      this.username.set(this.keycloak.tokenParsed?.['preferred_username'] ?? '');
    } catch {
      this.identityAvailable.set(false);
      this.authenticated.set(false);
    }
  }

  login(): void { void this.keycloak.login({ redirectUri: `${window.location.origin}/workspace` }); }
  logout(): void { void this.keycloak.logout({ redirectUri: window.location.origin }); }

  async validToken(): Promise<string | undefined> {
    if (!this.keycloak.authenticated) return undefined;
    await this.keycloak.updateToken(30);
    return this.keycloak.token;
  }
}
