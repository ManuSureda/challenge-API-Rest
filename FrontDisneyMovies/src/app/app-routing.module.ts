import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { CharacterComponent } from './components/character/character.component';
import { LogInComponent } from './components/log-in/log-in.component';
import { LogOutComponent } from './components/log-out/log-out.component';
import { MovieComponent } from './components/movie/movie.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { RegisterComponent } from './components/register/register.component';

const routes: Routes = [
  { path: 'auth/login', component: LogInComponent },
  { path: 'auth/logout', component: LogOutComponent, canActivate: [AuthGuard] },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'movies', component: MovieComponent, canActivate: [AuthGuard] },
  { path: 'movie/:id', component: MovieComponent, canActivate: [AuthGuard] },
  { path: 'characters', component: CharacterComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
