import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { QuoteApiService } from '../../core/quote-api.service';
import { Quote, QuoteType } from '../../models/quote';

@Component({
  selector: 'app-workspace',
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, DecimalPipe],
  templateUrl: './workspace.component.html',
  styleUrl: './workspace.component.scss',
})
export class WorkspaceComponent implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly api = inject(QuoteApiService);
  private readonly formBuilder = inject(FormBuilder);
  protected readonly quotes = signal<Quote[]>([]);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly error = signal('');

  protected readonly form = this.formBuilder.nonNullable.group({
    customerName: ['Demo Customer', [Validators.required, Validators.maxLength(120)]],
    age: [34, [Validators.required, Validators.min(18), Validators.max(100)]],
    annualIncome: [62000, [Validators.required, Validators.min(1)]],
    coverageAmount: [85000, [Validators.required, Validators.min(1000)]],
    previousClaims: [1, [Validators.required, Validators.min(0), Validators.max(20)]],
    vehicleAge: [5, [Validators.required, Validators.min(0), Validators.max(50)]],
    quoteType: ['AUTO' as QuoteType, Validators.required],
  });

  ngOnInit(): void { if (this.auth.authenticated()) this.loadQuotes(); }

  protected loadQuotes(): void {
    this.loading.set(true); this.error.set('');
    this.api.list().subscribe({
      next: (quotes) => { this.quotes.set(quotes); this.loading.set(false); },
      error: () => { this.error.set('The quote API is not available. Start the Docker stack and try again.'); this.loading.set(false); },
    });
  }

  protected submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true); this.error.set('');
    this.api.create(this.form.getRawValue()).subscribe({
      next: (quote) => { this.quotes.update((quotes) => [quote, ...quotes]); this.saving.set(false); },
      error: () => { this.error.set('The quote could not be created. Check the running services and your session.'); this.saving.set(false); },
    });
  }
}
