import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreateQuote, Quote } from '../models/quote';

@Injectable({ providedIn: 'root' })
export class QuoteApiService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/api/quotes`;
  list(): Observable<Quote[]> { return this.http.get<Quote[]>(this.url); }
  create(payload: CreateQuote): Observable<Quote> { return this.http.post<Quote>(this.url, payload); }
}
