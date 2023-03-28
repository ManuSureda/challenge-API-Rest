import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private router : Router) { }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    const token = sessionStorage.getItem('token');

    let request = req;
    
    if (token) {
      
      request = req.clone({ 
        setHeaders: {
          'authorization': `${ token }`, 
        } 
      });
  
    }

    return next.handle(request).pipe(
      catchError((err : HttpErrorResponse) => {
        if (err.status === 401) {
          this.router.navigateByUrl('/auth/login');
        } else if (err.status === 403) {
          this.router.navigateByUrl('/auth/login');
        }
        return throwError(err);
      })
    );

  }
}
