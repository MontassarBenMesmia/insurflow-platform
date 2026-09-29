import { Routes } from '@angular/router';
import { LandingComponent } from './features/landing/landing.component';
import { WorkspaceComponent } from './features/workspace/workspace.component';

export const routes: Routes = [
  { path: '', component: LandingComponent, title: 'InsurFlow | Intelligent insurance quotes' },
  { path: 'workspace', component: WorkspaceComponent, title: 'Quote workspace | InsurFlow' },
  { path: '**', redirectTo: '' },
];
