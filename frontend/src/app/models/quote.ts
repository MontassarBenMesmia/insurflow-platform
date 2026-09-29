export type QuoteType = 'AUTO' | 'HOME' | 'TRAVEL';
export type RiskBand = 'LOW' | 'MEDIUM' | 'HIGH';
export type QuoteStatus = 'ASSESSED' | 'PENDING_REVIEW';
export interface CreateQuote { customerName: string; age: number; annualIncome: number; coverageAmount: number; previousClaims: number; vehicleAge: number; quoteType: QuoteType; }
export interface Quote extends CreateQuote { id: string; status: QuoteStatus; riskScore: number | null; riskBand: RiskBand | null; recommendedPremium: number | null; createdBy: string; createdAt: string; }
